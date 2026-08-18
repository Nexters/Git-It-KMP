package com.nexters.hytime.gitit.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nexters.hytime.gitit.BuildConfig
import com.nexters.hytime.gitit.MainActivity
import com.nexters.hytime.gitit.R
import com.nexters.hytime.gitit.domain.auth.LoginSessionStorage
import com.nexters.hytime.gitit.domain.model.DeviceInfo
import com.nexters.hytime.gitit.domain.model.ProjectGenerationStatus
import com.nexters.hytime.gitit.domain.repository.MemberRepository
import com.nexters.hytime.gitit.feature.quiz.create.session.QuizCreateStatusSynchronizer
import com.nexters.hytime.gitit.logging.gitItLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/** FCM 앱 설치 등록을 서버와 동기화하고, 수신 메시지의 문제 생성 결과 반영과 시스템 알림 표시를 처리한다. */
class GitItFirebaseMessagingService : FirebaseMessagingService() {
    /** FCM 수신과 토큰 갱신 상태를 기록하는 로거다. */
    private val logger by lazy { gitItLogger(tag = "FCM") }

    /** 회원 기기 정보를 등록하는 저장소다. */
    private val memberRepository by inject<MemberRepository>()

    /** API 호출 가능 여부를 확인할 로그인 세션 저장소다. */
    private val sessionStorage by inject<LoginSessionStorage>()

    /** 문제 생성 결과를 서버 상태와 맞춘 뒤 앱 범위 생성 세션에 반영한다. */
    private val quizCreateStatusSynchronizer by inject<QuizCreateStatusSynchronizer>()

    /** 서비스 콜백 이후에도 기기 등록과 생성 결과 반영을 완료하는 코루틴 범위다. */
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * data payload의 문제 생성 결과를 반영하고, data 또는 notification payload에 제목과 본문이 있으면 학습 알림을 게시한다.
     *
     * @param remoteMessage Firebase가 전달한 원본 메시지
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        logger.i {
            "FCM 메시지 수신: messageId=${remoteMessage.messageId}, " +
                "dataKeys=${remoteMessage.data.keys}, notification=${remoteMessage.notification != null}"
        }
        val data = remoteMessage.data
        val hasCreateFields = QUIZ_CREATE_PROJECT_ID_KEY in data || QUIZ_CREATE_STATUS_KEY in data
        val createResult = data.toQuizCreateResult()
        if (createResult != null) {
            serviceScope.launch {
                quizCreateStatusSynchronizer
                    .sync(createResult.projectId)
                    .onFailure { error ->
                        logger.w(throwable = error) { "FCM 문제 생성 상태 API 동기화 실패" }
                        quizCreateStatusSynchronizer.applyFallback(
                            projectId = createResult.projectId,
                            status = createResult.status.toProjectGenerationStatus(),
                        )
                    }
                }
        } else if (hasCreateFields) {
            logger.w { "FCM 문제 생성 결과 payload의 projectId 또는 status가 올바르지 않습니다." }
        }

        val content =
            resolveNotificationContent(
                data = data,
                notificationTitle = remoteMessage.notification?.title,
                notificationBody = remoteMessage.notification?.body,
            )
        if (content == null) {
            // 문제 생성 결과 전용 메시지는 제목과 본문이 없는 것이 정상이라 경고하지 않는다.
            if (!hasCreateFields) {
                logger.w { "FCM payload에 title 또는 body가 없습니다." }
            }
            return
        }
        showNotification(content, remoteMessage.messageId)
    }

    /**
     * 등록된 Firebase Installation ID를 회원의 기기 정보와 동기화한다.
     *
     * @param installationId Firebase가 앱 인스턴스에 발급한 식별자
     */
    override fun onRegistered(installationId: String) {
        logger.i { "FCM 등록 완료" }
        serviceScope.launch { registerDevice(installationId) }
    }

    /**
     * 로그인 상태라면 현재 Firebase 앱 설치 정보를 서버에 등록한다.
     *
     * @param installationId FCM 직접 발송 대상으로 사용하는 Firebase Installation ID
     */
    private suspend fun registerDevice(installationId: String) {
        if (sessionStorage.load() == null) return
        val deviceInfo =
            createAndroidDeviceInfo(
                installationId = installationId,
                appVersion = BuildConfig.VERSION_NAME,
                osVersion = Build.VERSION.RELEASE.ifBlank { Build.VERSION.SDK_INT.toString() },
                notificationsEnabled = getSystemService(NotificationManager::class.java).areNotificationsEnabled(),
            ) ?: return
        memberRepository
            .registerDevice(deviceInfo)
            .onFailure { error -> logger.w(throwable = error) { "회원 기기 정보 등록 실패" } }
    }

    /**
     * 사용자가 누르면 앱을 여는 알림을 게시한다.
     *
     * @param content 표시할 제목과 본문
     * @param messageId 같은 FCM 메시지의 알림을 식별하는 값
     */
    private fun showNotification(
        content: NotificationContent,
        messageId: String?,
    ) {
        val notificationManager = getSystemService(NotificationManager::class.java)
        val openAppIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                OPEN_APP_REQUEST_CODE,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        val builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
            } else {
                @Suppress("DEPRECATION")
                Notification.Builder(this)
            }
        val notification =
            builder
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(content.title)
                .setContentText(content.body)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .build()

        logger.i { "FCM 알림 게시: messageId=$messageId, 알림허용=${notificationManager.areNotificationsEnabled()}" }
        notificationManager.notify(messageId?.hashCode() ?: SystemClock.elapsedRealtime().toInt(), notification)
    }

    private companion object {
        /** 앱을 여는 PendingIntent를 갱신할 때 사용하는 요청 코드다. */
        private const val OPEN_APP_REQUEST_CODE = 1
    }
}

/** Android 8.0 이상에서 포그라운드와 백그라운드 FCM이 공유할 학습 알림 채널을 생성한다. */
internal fun Context.createLearningNotificationChannel() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    getSystemService(NotificationManager::class.java).createNotificationChannel(
        NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            getString(R.string.notification_channel_learning),
            NotificationManager.IMPORTANCE_DEFAULT,
        ),
    )
}

/**
 * 시스템 알림에 표시할 FCM data payload다.
 *
 * @property title 알림의 한 줄 제목
 * @property body 제목 아래에 표시할 본문
 */
internal data class NotificationContent(
    val title: String,
    val body: String,
)

/** 서버가 FCM data payload로 전달하는 문제 생성 결과 상태다. */
internal enum class QuizCreateResultStatus {
    /** 문제 생성이 정상적으로 끝났다. */
    Success,

    /** 문제 생성 처리 중 오류가 발생했다. */
    Failed,

    /** 서버가 문제 생성 요청을 거절했다. */
    Rejected,
}

/** @return FCM 결과를 서버 상태 API와 같은 도메인 상태로 변환한 값 */
private fun QuizCreateResultStatus.toProjectGenerationStatus(): ProjectGenerationStatus =
    when (this) {
        QuizCreateResultStatus.Success -> ProjectGenerationStatus.Completed
        QuizCreateResultStatus.Failed -> ProjectGenerationStatus.Failed
        QuizCreateResultStatus.Rejected -> ProjectGenerationStatus.Rejected
    }

/**
 * FCM으로 수신한 문제 생성 결과다.
 *
 * @property projectId 결과를 반영할 프로젝트 식별자
 * @property status 서버가 전달한 생성 처리 상태
 */
internal data class QuizCreateResult(
    val projectId: String,
    val status: QuizCreateResultStatus,
)

/**
 * FCM data payload의 `projectId`와 `status`를 문제 생성 결과로 변환한다.
 *
 * @return 두 필드가 서버 규격에 맞으면 생성 결과, 아니면 `null`
 */
internal fun Map<String, String>.toQuizCreateResult(): QuizCreateResult? {
    val projectId = get(QUIZ_CREATE_PROJECT_ID_KEY)?.trim().orEmpty()
    if (projectId.isBlank()) return null
    val status =
        when (get(QUIZ_CREATE_STATUS_KEY)?.trim()) {
            "success" -> QuizCreateResultStatus.Success
            "failed" -> QuizCreateResultStatus.Failed
            "rejected" -> QuizCreateResultStatus.Rejected
            else -> return null
        }
    return QuizCreateResult(projectId = projectId, status = status)
}

/**
 * FCM data payload에서 필수 제목과 본문을 꺼내 공백을 정리한다.
 *
 * @return 제목과 본문이 모두 있으면 알림 내용, 아니면 `null`
 */
internal fun Map<String, String>.toNotificationContent(): NotificationContent? {
    return createNotificationContent(get("title"), get("body"))
}

/**
 * data payload을 우선하고 유효하지 않으면 notification payload로 알림 내용을 구성한다.
 *
 * @param data FCM data payload
 * @param notificationTitle notification payload의 제목
 * @param notificationBody notification payload의 본문
 * @return 두 payload 중 먼저 유효한 알림 내용, 모두 유효하지 않으면 `null`
 */
internal fun resolveNotificationContent(
    data: Map<String, String>,
    notificationTitle: String?,
    notificationBody: String?,
): NotificationContent? = data.toNotificationContent() ?: createNotificationContent(notificationTitle, notificationBody)

/**
 * nullable 제목과 본문을 시스템 알림 내용으로 정리한다.
 *
 * @param title 공백을 제거할 알림 제목
 * @param body 공백을 제거할 알림 본문
 * @return 제목과 본문이 모두 있으면 알림 내용, 아니면 `null`
 */
internal fun createNotificationContent(
    title: String?,
    body: String?,
): NotificationContent? {
    val normalizedTitle = title?.trim().orEmpty()
    val normalizedBody = body?.trim().orEmpty()
    return if (normalizedTitle.isBlank() || normalizedBody.isBlank()) null else NotificationContent(normalizedTitle, normalizedBody)
}

/** 포그라운드와 백그라운드 FCM 알림이 공유하는 Android 채널 식별자다. */
private const val NOTIFICATION_CHANNEL_ID = "learning"

/**
 * Firebase Installation ID와 현재 앱 환경을 서버 기기 정보로 변환한다.
 *
 * @param installationId FCM 직접 발송 대상으로 사용하는 Firebase Installation ID
 * @param appVersion 설치된 앱 버전
 * @param osVersion 기기 Android 버전
 * @param notificationsEnabled 시스템 알림 활성화 여부
 * @return 유효한 FID면 등록할 기기 정보, 비어 있으면 `null`
 */
internal fun createAndroidDeviceInfo(
    installationId: String,
    appVersion: String,
    osVersion: String,
    notificationsEnabled: Boolean,
): DeviceInfo? {
    val normalizedInstallationId = installationId.trim()
    if (normalizedInstallationId.isEmpty()) return null
    return DeviceInfo(
        deviceId = normalizedInstallationId,
        deviceType = "android",
        appVersion = appVersion,
        osVersion = osVersion,
        deviceToken = normalizedInstallationId.takeIf { notificationsEnabled },
    )
}

/** FCM data payload에서 문제 생성 프로젝트를 식별하는 키다. */
private const val QUIZ_CREATE_PROJECT_ID_KEY = "projectId"

/** FCM data payload에서 문제 생성 결과 상태를 식별하는 키다. */
private const val QUIZ_CREATE_STATUS_KEY = "status"

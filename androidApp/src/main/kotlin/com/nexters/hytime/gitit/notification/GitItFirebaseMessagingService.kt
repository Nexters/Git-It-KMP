package com.nexters.hytime.gitit.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import com.nexters.hytime.gitit.domain.repository.AccountRepository
import com.nexters.hytime.gitit.logging.gitItLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/** FCM 앱 설치 등록을 서버와 동기화하고 수신 메시지를 Android 시스템 알림으로 표시한다. */
class GitItFirebaseMessagingService : FirebaseMessagingService() {
    /** FCM 수신과 토큰 갱신 상태를 기록하는 로거다. */
    private val logger by lazy { gitItLogger(tag = "FCM") }

    /** 회원 기기 정보를 등록하는 저장소다. */
    private val accountRepository by inject<AccountRepository>()

    /** API 호출 가능 여부를 확인할 로그인 세션 저장소다. */
    private val sessionStorage by inject<LoginSessionStorage>()

    /** 서비스 콜백 이후에도 기기 등록 요청을 완료하는 코루틴 범위다. */
    private val registrationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * data 또는 notification payload의 제목과 본문을 검증한 뒤 학습 알림을 게시한다.
     *
     * @param remoteMessage Firebase가 전달한 원본 메시지
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        logger.i {
            "FCM 메시지 수신: messageId=${remoteMessage.messageId}, " +
                "dataKeys=${remoteMessage.data.keys}, notification=${remoteMessage.notification != null}"
        }
        val content =
            remoteMessage.data.toNotificationContent()
                ?: remoteMessage.notification?.let { notification ->
                    createNotificationContent(notification.title, notification.body)
                }
        if (content == null) {
            logger.w { "FCM payload에 title 또는 body가 없습니다." }
            return
        }
        showNotification(content, remoteMessage.messageId)
        logger.i { "FCM 알림 표시 완료: messageId=${remoteMessage.messageId}" }
    }

    /**
     * 등록된 Firebase Installation ID를 회원의 기기 정보와 동기화한다.
     *
     * @param installationId Firebase가 앱 인스턴스에 발급한 식별자
     */
    override fun onRegistered(installationId: String) {
        logger.i { "FCM 등록 완료" }
        registrationScope.launch { registerDevice(installationId) }
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
        accountRepository
            .registerDevice(deviceInfo)
            .onFailure { error -> logger.w(throwable = error) { "회원 기기 정보 등록 실패" } }
    }

    /**
     * 학습 알림 채널을 준비하고 사용자가 누르면 앱을 여는 알림을 게시한다.
     *
     * @param content 표시할 제목과 본문
     * @param messageId 같은 FCM 메시지의 알림을 식별하는 값
     */
    private fun showNotification(
        content: NotificationContent,
        messageId: String?,
    ) {
        val notificationManager = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    getString(R.string.notification_channel_learning),
                    NotificationManager.IMPORTANCE_DEFAULT,
                ),
            )
        }

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

        notificationManager.notify(messageId?.hashCode() ?: SystemClock.elapsedRealtime().toInt(), notification)
    }

    private companion object {
        /** 학습 알림 설정을 유지하는 Android 채널 식별자다. */
        private const val NOTIFICATION_CHANNEL_ID = "learning"

        /** 앱을 여는 PendingIntent를 갱신할 때 사용하는 요청 코드다. */
        private const val OPEN_APP_REQUEST_CODE = 1
    }
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

/**
 * FCM data payload에서 필수 제목과 본문을 꺼내 공백을 정리한다.
 *
 * @return 제목과 본문이 모두 있으면 알림 내용, 아니면 `null`
 */
internal fun Map<String, String>.toNotificationContent(): NotificationContent? {
    return createNotificationContent(get("title"), get("body"))
}

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

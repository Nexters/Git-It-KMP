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
import com.nexters.hytime.gitit.logging.gitItLogger

/** data-only FCM 메시지를 Android 시스템 알림으로 표시한다. */
class GitItFirebaseMessagingService : FirebaseMessagingService() {
    /** FCM 수신과 토큰 갱신 상태를 기록하는 로거다. */
    private val logger by lazy { gitItLogger(tag = "FCM") }

    /**
     * data payload의 제목과 본문을 검증한 뒤 학습 알림을 게시한다.
     *
     * @param remoteMessage Firebase가 전달한 원본 메시지
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val content = remoteMessage.data.toNotificationContent()
        if (content == null) {
            logger.w { "FCM data payload에 title 또는 body가 없습니다." }
            return
        }
        showNotification(content, remoteMessage.messageId)
    }

    /**
     * 등록된 Firebase Installation ID를 디버그 빌드에서 테스트 발송용으로 기록한다.
     *
     * @param installationId Firebase가 앱 인스턴스에 발급한 식별자
     */
    override fun onRegistered(installationId: String) {
        if (BuildConfig.DEBUG) logger.d { "FCM Installation ID: $installationId" }
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
    val title = get("title")?.trim().orEmpty()
    val body = get("body")?.trim().orEmpty()
    return if (title.isBlank() || body.isBlank()) null else NotificationContent(title, body)
}

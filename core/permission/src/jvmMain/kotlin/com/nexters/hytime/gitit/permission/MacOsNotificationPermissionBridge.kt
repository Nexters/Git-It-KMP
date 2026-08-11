package com.nexters.hytime.gitit.permission

import com.sun.jna.Callback
import com.sun.jna.Library
import com.sun.jna.Native
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.concurrent.ConcurrentHashMap

/**
 * macOS UserNotifications 프레임워크를 C 인터페이스로 호출한다.
 */
internal class MacOsNotificationPermissionBridge {
    private val nativeLibrary: NativeNotificationPermissionLibrary = loadNativeLibrary()

    /**
     * macOS에 저장된 현재 알림 권한 상태를 비동기로 조회한다.
     *
     * @param callback 조회 결과를 전달받을 콜백
     */
    fun getPermissionStatus(callback: MacOsNotificationPermissionCallback) {
        val retainedCallback = retainUntilInvoked(callback)
        try {
            nativeLibrary.gititNotificationPermissionStatus(retainedCallback)
        } catch (throwable: Throwable) {
            pendingCallbacks.remove(retainedCallback)
            throw throwable
        }
    }

    /**
     * macOS 알림 권한 대화상자를 표시하고 변경된 상태를 비동기로 조회한다.
     *
     * @param callback 요청 이후 권한 상태를 전달받을 콜백
     */
    fun requestPermission(callback: MacOsNotificationPermissionCallback) {
        val retainedCallback = retainUntilInvoked(callback)
        try {
            nativeLibrary.gititRequestNotificationPermission(retainedCallback)
        } catch (throwable: Throwable) {
            pendingCallbacks.remove(retainedCallback)
            throw throwable
        }
    }

    private companion object {
        private const val NATIVE_LIBRARY_RESOURCE = "/native/macos/libgitit_permission.dylib"
        private val pendingCallbacks = ConcurrentHashMap.newKeySet<MacOsNotificationPermissionCallback>()

        /**
         * 비동기 네이티브 호출이 끝날 때까지 JNA 콜백의 강한 참조를 유지한다.
         *
         * @param callback 실제 권한 결과를 받을 콜백
         * @return 완료 후 자동으로 참조가 해제되는 콜백
         */
        private fun retainUntilInvoked(callback: MacOsNotificationPermissionCallback): MacOsNotificationPermissionCallback {
            lateinit var retainedCallback: MacOsNotificationPermissionCallback
            retainedCallback =
                MacOsNotificationPermissionCallback { status ->
                    try {
                        callback.invoke(status)
                    } finally {
                        pendingCallbacks.remove(retainedCallback)
                    }
                }
            pendingCallbacks.add(retainedCallback)
            return retainedCallback
        }

        /**
         * 애플리케이션 리소스의 범용 macOS 동적 라이브러리를 임시 경로로 추출해 로드한다.
         *
         * @return JNA가 매핑한 네이티브 알림 권한 라이브러리
         */
        private fun loadNativeLibrary(): NativeNotificationPermissionLibrary {
            val libraryDirectory = Files.createTempDirectory("gitit-permission-")
            val libraryFile = libraryDirectory.resolve("libgitit_permission.dylib")
            val input =
                MacOsNotificationPermissionBridge::class.java.getResourceAsStream(NATIVE_LIBRARY_RESOURCE)
                    ?: error("macOS 알림 권한 네이티브 라이브러리를 찾을 수 없습니다.")

            input.use {
                Files.copy(it, libraryFile, StandardCopyOption.REPLACE_EXISTING)
            }
            libraryDirectory.toFile().deleteOnExit()
            libraryFile.toFile().deleteOnExit()

            return Native.load(libraryFile.toAbsolutePath().toString(), NativeNotificationPermissionLibrary::class.java)
        }
    }
}

/**
 * macOS 네이티브 권한 결과를 JVM으로 전달한다.
 */
internal fun interface MacOsNotificationPermissionCallback : Callback {
    /**
     * 네이티브 권한 상태 코드를 전달한다.
     *
     * @param status Apple 권한 상태에 대응하는 정수 코드
     */
    fun invoke(status: Int)
}

/**
 * 네이티브 macOS 권한 상태 코드를 공통 권한 상태로 변환한다.
 *
 * Apple의 provisional 상태는 조용한 알림을 게시할 수 있으므로 허용 상태로 처리한다.
 *
 * @return 공통 알림 권한 상태
 */
internal fun Int.toNotificationPermissionStatus(): NotificationPermissionStatus =
    when (this) {
        NativeNotificationPermissionStatus.NOT_DETERMINED -> NotificationPermissionStatus.NOT_DETERMINED
        NativeNotificationPermissionStatus.DENIED -> NotificationPermissionStatus.DENIED
        NativeNotificationPermissionStatus.AUTHORIZED,
        NativeNotificationPermissionStatus.PROVISIONAL,
        -> NotificationPermissionStatus.GRANTED
        else -> NotificationPermissionStatus.UNAVAILABLE
    }

/**
 * JNA가 매핑할 macOS 네이티브 라이브러리 함수다.
 */
private interface NativeNotificationPermissionLibrary : Library {
    /**
     * 현재 알림 권한 상태를 조회한다.
     *
     * @param callback 조회 결과를 전달받을 콜백
     */
    fun gititNotificationPermissionStatus(callback: MacOsNotificationPermissionCallback)

    /**
     * 알림 권한을 요청하고 변경된 상태를 조회한다.
     *
     * @param callback 요청 이후 결과를 전달받을 콜백
     */
    fun gititRequestNotificationPermission(callback: MacOsNotificationPermissionCallback)
}

/**
 * Objective-C 브리지와 JVM 사이에서 공유하는 권한 상태 코드다.
 */
private object NativeNotificationPermissionStatus {
    const val NOT_DETERMINED = 0
    const val DENIED = 1
    const val AUTHORIZED = 2
    const val PROVISIONAL = 3
}

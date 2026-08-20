package com.nexters.hytime.gitit

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.google.firebase.messaging.FirebaseMessaging
import com.nexters.hytime.gitit.auth.AndroidLoginSessionStorage
import com.nexters.hytime.gitit.auth.AndroidGoogleAuthenticator
import com.nexters.hytime.gitit.auth.GoogleAuthenticator
import com.nexters.hytime.gitit.auth.GoogleAuthTokenProvider
import com.nexters.hytime.gitit.data.di.dataModule
import com.nexters.hytime.gitit.domain.auth.AuthTokenProvider
import com.nexters.hytime.gitit.domain.auth.LoginSessionStorage
import com.nexters.hytime.gitit.domain.model.LoginSession
import com.nexters.hytime.gitit.feature.onboarding.onboardingModule
import com.nexters.hytime.gitit.logging.AppLogger
import com.nexters.hytime.gitit.logging.gitItLogger
import com.nexters.hytime.gitit.logging.initLogger
import com.nexters.hytime.gitit.network.di.networkModule
import com.nexters.hytime.gitit.notification.createLearningNotificationChannel
import java.lang.ref.WeakReference
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

/** Android 프로세스의 로깅, DI, FCM 기기 등록을 초기화한다. */
class GitItApplication : Application() {
    /** Google 로그인 UI를 띄울 현재 Activity를 추적한다. */
    private val currentActivityTracker = CurrentActivityTracker()

    /** 앱 의존성을 구성한 뒤 현재 Firebase 앱 설치 등록을 요청한다. */
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(currentActivityTracker)
        initLogger(BuildConfig.DEBUG)
        createLearningNotificationChannel()
        val networkLogger = gitItLogger(tag = "🌐 Network")
        val registrationLogger = gitItLogger(tag = "FCM")
        val requestRegistration = { requestFirebaseRegistration(registrationLogger) }
        val sessionStorage =
            RegistrationRequestingLoginSessionStorage(
                delegate = AndroidLoginSessionStorage(this),
                requestRegistration = requestRegistration,
            )
        startKoin {
            androidContext(this@GitItApplication)
            modules(
                appModules +
                    onboardingModule +
                    dataModule +
                    platformModule(sessionStorage, currentActivityTracker::current) +
                    networkModule(
                        networkLogger = { message -> networkLogger.d { message } },
                        baseUrl = BuildConfig.BACKEND_BASE_URL,
                        accessTokenProvider = {
                            sessionStorage.load()?.accessToken?.also { accessToken ->
                                if (BuildConfig.DEBUG) networkLogger.d { "Access token: $accessToken" }
                            }
                        },
                    ),
            )
        }
        requestRegistration()
    }
}

/**
 * Android 인증 의존성을 등록한다.
 *
 * @param sessionStorage 네트워크 인증과 로그인에서 공유할 세션 저장소
 * @param activityProvider Google 계정 선택 UI를 띄울 현재 Activity를 제공하는 함수
 * @return Android 인증 의존성이 등록된 Koin 모듈
 */
private fun platformModule(
    sessionStorage: LoginSessionStorage,
    activityProvider: () -> Activity?,
) = module {
    single<GoogleAuthenticator> {
        AndroidGoogleAuthenticator(
            activityProvider = activityProvider,
            serverClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID,
        )
    }
    single<AuthTokenProvider> { GoogleAuthTokenProvider(get()) }
    single<LoginSessionStorage> { sessionStorage }
}

/**
 * 현재 앱 설치를 FCM에 등록하고 비동기 실패만 기록한다.
 *
 * @param logger 등록 요청 실패를 기록할 로거
 */
private fun requestFirebaseRegistration(logger: AppLogger) {
    runCatching {
        FirebaseMessaging
            .getInstance()
            .register()
            .addOnFailureListener { error -> logger.w(throwable = error) { "FCM 등록 요청 실패" } }
    }.onFailure { error -> logger.w(throwable = error) { "FCM 등록 요청 실패" } }
}

/**
 * 로그인 세션 저장 성공 뒤 FCM 등록을 다시 요청하는 저장소 decorator다.
 *
 * @property delegate 실제 세션을 안전하게 저장하는 플랫폼 저장소
 * @property requestRegistration 현재 앱 설치의 FCM 등록을 요청하는 함수
 */
internal class RegistrationRequestingLoginSessionStorage(
    private val delegate: LoginSessionStorage,
    private val requestRegistration: () -> Unit,
) : LoginSessionStorage {
    override suspend fun save(session: LoginSession) {
        delegate.save(session)
        requestRegistration()
    }

    override suspend fun load(): LoginSession? = delegate.load()

    override suspend fun clear() = delegate.clear()
}

/**
 * 현재 화면에 보이는 Activity를 약한 참조로 보관한다.
 *
 * Credential Manager는 Application context로는 계정 선택 UI를 띄우지 못하고 콜백도 돌려주지 않는다.
 */
internal class CurrentActivityTracker : Application.ActivityLifecycleCallbacks {
    private var activityRef: WeakReference<Activity>? = null

    /**
     * @return 마지막으로 재개된 Activity. 앱이 백그라운드이거나 이미 소멸했으면 null
     */
    fun current(): Activity? = activityRef?.get()

    override fun onActivityResumed(activity: Activity) {
        activityRef = WeakReference(activity)
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (activityRef?.get() === activity) {
            activityRef = null
        }
    }

    override fun onActivityCreated(
        activity: Activity,
        savedInstanceState: Bundle?,
    ) = Unit

    override fun onActivityStarted(activity: Activity) = Unit

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(
        activity: Activity,
        outState: Bundle,
    ) = Unit
}

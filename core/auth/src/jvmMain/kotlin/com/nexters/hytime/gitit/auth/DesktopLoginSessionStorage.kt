package com.nexters.hytime.gitit.auth

import com.nexters.hytime.gitit.domain.auth.LoginSessionStorage
import com.nexters.hytime.gitit.domain.model.LoginSession
import java.util.prefs.Preferences

/** 운영체제 사용자 전용 Java Preferences에 데스크톱 로그인 세션을 보관한다. */
class DesktopLoginSessionStorage : LoginSessionStorage {
    private val preferences = Preferences.userNodeForPackage(DesktopLoginSessionStorage::class.java)

    override fun save(session: LoginSession) {
        preferences.put(KEY_ACCESS_TOKEN, session.accessToken)
        preferences.put(KEY_REFRESH_TOKEN, session.refreshToken)
        preferences.putBoolean(KEY_NEEDS_CURATION, session.needsCuration)
    }

    override fun load(): LoginSession? {
        val accessToken = preferences.get(KEY_ACCESS_TOKEN, null) ?: return null
        val refreshToken = preferences.get(KEY_REFRESH_TOKEN, null) ?: return null
        return LoginSession(
            accessToken = accessToken,
            refreshToken = refreshToken,
            needsCuration = preferences.getBoolean(KEY_NEEDS_CURATION, false),
        )
    }

    override fun clear() {
        preferences.remove(KEY_ACCESS_TOKEN)
        preferences.remove(KEY_REFRESH_TOKEN)
        preferences.remove(KEY_NEEDS_CURATION)
    }

    private companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_NEEDS_CURATION = "needs_curation"
    }
}

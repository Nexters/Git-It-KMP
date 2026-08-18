package com.nexters.hytime.gitit

import android.content.Context
import com.nexters.hytime.gitit.feature.quiz.create.session.PendingQuizCreationStorage

/**
 * Android 앱 재시작 뒤에도 생성 중 프로젝트를 복원하는 Preferences 저장소다.
 *
 * @param context 앱 전용 SharedPreferences를 제공할 컨텍스트
 */
internal class PendingQuizCreationPreferences(
    context: Context,
) : PendingQuizCreationStorage {
    private val preferences = context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override var projectId: String?
        get() = preferences.getString(PROJECT_ID_KEY, null)
        set(value) {
            preferences.edit().apply {
                if (value == null) remove(PROJECT_ID_KEY) else putString(PROJECT_ID_KEY, value)
            }.commit()
        }

    private companion object {
        /** 문제 생성 세션 전용 Preferences 이름이다. */
        private const val PREFERENCES_NAME = "quiz_creation"

        /** 생성 결과를 기다리는 프로젝트 식별자 키다. */
        private const val PROJECT_ID_KEY = "pending_project_id"
    }
}

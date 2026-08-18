package com.nexters.hytime.gitit

import com.nexters.hytime.gitit.feature.quiz.create.session.PendingQuizCreationStorage
import java.util.prefs.Preferences

/** Desktop 앱 재시작 뒤에도 생성 중 프로젝트를 복원하는 JDK Preferences 저장소다. */
internal class PendingQuizCreationPreferences : PendingQuizCreationStorage {
    private val preferences = Preferences.userNodeForPackage(PendingQuizCreationPreferences::class.java)

    override var projectId: String?
        get() = preferences.get(PROJECT_ID_KEY, null)
        set(value) {
            if (value == null) preferences.remove(PROJECT_ID_KEY) else preferences.put(PROJECT_ID_KEY, value)
        }

    private companion object {
        /** 생성 결과를 기다리는 프로젝트 식별자 키다. */
        private const val PROJECT_ID_KEY = "pending_project_id"
    }
}

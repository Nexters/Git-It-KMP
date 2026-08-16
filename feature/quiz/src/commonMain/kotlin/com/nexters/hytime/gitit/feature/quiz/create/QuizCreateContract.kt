package com.nexters.hytime.gitit.feature.quiz.create

/** 문제 생성 설정 화면의 단계다. */
enum class QuizCreateStage {
    /** 사용자의 프로젝트 이해도를 선택한다. */
    Knowledge,

    /** 생성에 앞서 예상 소요 시간을 안내한다. */
    Ready,

    /** 학습 세트를 생성하고 있다. */
    Create,
}

/** 프로젝트에 대한 사용자의 사전 이해도다. */
enum class QuizKnowledgeLevel {
    /** 기술 개념은 알고 있으나 코드 경험은 적다. */
    Concepts,

    /** 프로젝트 코드를 일부 살펴봤다. */
    SomeCode,

    /** 유사 프로젝트 구현 경험이 있다. */
    Experienced,
}

/** 학습 세트 생성 화면에 표시할 진행 단계다. */
enum class QuizCreateStep {
    /** 프로젝트 메타데이터를 확인한다. */
    ProjectInfo,

    /** 저장소의 코드 구조를 분석한다. */
    CodeStructure,

    /** 학습할 개념을 구성한다. */
    LearningConcepts,

    /** 학습 문제를 생성한다. */
    Questions,

    /** 생성된 세트를 검증한다. */
    Validation,
}

/**
 * 문제 생성 설정과 진행 상태를 한 곳에서 관리한다.
 *
 * @property repositoryUrl 서버에 등록할 GitHub 저장소 URL
 * @property projectId 등록 성공 후 FCM 결과를 식별할 서버 프로젝트 ID
 * @property stage 현재 표시할 생성 단계
 * @property knowledgeLevel 선택한 프로젝트 이해도
 * @property createStep 현재 화면에 표시할 생성 단계
 * @property progressPercent 시뮬레이터가 계산한 생성 진행률. 0..100 범위다
 * @property showReminderPrompt 홈 이동 전에 리마인드 알림 안내를 표시할지 여부
 * @property isRegistering 프로젝트 등록 API 응답을 기다리는지 여부
 */
data class QuizCreateUiState(
    val repositoryUrl: String,
    val projectId: String? = null,
    val stage: QuizCreateStage = QuizCreateStage.Knowledge,
    val knowledgeLevel: QuizKnowledgeLevel? = null,
    val createStep: QuizCreateStep = QuizCreateStep.ProjectInfo,
    val progressPercent: Int = 0,
    val showReminderPrompt: Boolean = false,
    val isRegistering: Boolean = false,
) {
    /** 현재 단계의 다음 버튼을 활성화할 수 있는지 여부다. */
    val canProceed: Boolean
        get() =
            when (stage) {
                QuizCreateStage.Knowledge -> knowledgeLevel != null
                QuizCreateStage.Ready -> !isRegistering
                else -> false
            }
}

/** 문제 생성 설정 화면에서 발생하는 사용자 의도다. */
sealed interface QuizCreateIntent {
    /** 현재 단계에서 이전 단계로 이동한다. */
    data object BackClick : QuizCreateIntent

    /** 선택한 이해도를 확정하고 생성 안내 단계로 이동한다. */
    data object NextClick : QuizCreateIntent

    /** 생성 요청을 시작한다. */
    data object StartCreate : QuizCreateIntent

    /** 생성 중 홈에서 기다리기 안내를 연다. */
    data object WaitAtHome : QuizCreateIntent

    /** 리마인드 알림을 사용하고 홈으로 이동한다. */
    data object EnableReminder : QuizCreateIntent

    /** 리마인드 알림을 건너뛰고 홈으로 이동한다. */
    data object DismissReminder : QuizCreateIntent

    /** 홈으로 이동하지 않고 리마인드 알림 안내만 닫는다. */
    data object CloseReminder : QuizCreateIntent

    /**
     * 프로젝트 이해도를 선택한다.
     *
     * @property level 선택한 이해도
     */
    data class SelectKnowledge(
        val level: QuizKnowledgeLevel,
    ) : QuizCreateIntent

    /**
     * UI 진행 시뮬레이터가 계산한 상태를 반영한다.
     *
     * @property step 현재 처리 단계
     * @property progressPercent 전체 진행률. 범위를 벗어난 값은 0..100으로 보정한다
     */
    data class CreateProgressChanged(
        val step: QuizCreateStep,
        val progressPercent: Int,
    ) : QuizCreateIntent

    /** 생성 서비스가 성공 결과를 전달했다. */
    data object CreateSucceeded : QuizCreateIntent

    /** 생성 서비스가 실패 결과를 전달했다. */
    data object CreateFailed : QuizCreateIntent
}

/** 문제 생성 화면 밖에서 한 번만 처리할 이벤트다. */
sealed interface QuizCreateEvent {
    /** 현재 생성 플로우를 닫고 이전 화면으로 이동한다. */
    data object NavigateBack : QuizCreateEvent

    /** 생성은 유지한 채 홈으로 이동한다. */
    data object NavigateHome : QuizCreateEvent

    /** 알림 권한을 요청한 뒤 홈으로 이동한다. */
    data object EnableReminderAndNavigateHome : QuizCreateEvent
}

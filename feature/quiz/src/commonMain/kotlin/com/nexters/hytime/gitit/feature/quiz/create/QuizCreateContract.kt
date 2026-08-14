package com.nexters.hytime.gitit.feature.quiz.create

/** 문제 생성 설정 화면의 단계다. */
enum class QuizCreateStage {
    /** 사용자의 프로젝트 이해도를 선택한다. */
    Knowledge,

    /** 문제로 다룰 주제를 선택한다. */
    Topics,

    /** 생성에 앞서 예상 소요 시간을 안내한다. */
    Ready,

    /** 학습 세트를 생성하고 있다. */
    Generating,
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

/** 생성할 문제에서 중점적으로 다룰 주제다. */
enum class QuizCreateTopic {
    /** 폴더와 모듈의 역할을 학습한다. */
    ProjectStructure,

    /** 입력부터 결과까지 기능 실행 경로를 학습한다. */
    FeatureFlow,

    /** 프레임워크와 라이브러리 사용 방식을 학습한다. */
    CoreConcepts,

    /** 특정 구현 방식을 선택한 이유를 학습한다. */
    CodeIntent,

    /** 코드 변경 시 영향을 받는 범위를 학습한다. */
    ChangeImpact,
}

/** 학습 세트 생성 화면에 표시할 진행 단계다. */
enum class QuizGenerationStep {
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
 * @property projectId 문제를 생성할 프로젝트 식별자
 * @property stage 현재 표시할 생성 단계
 * @property knowledgeLevel 선택한 프로젝트 이해도
 * @property topics 사용자가 선택한 문제 주제
 * @property generationStep 현재 화면에 표시할 생성 단계
 * @property progressPercent 시뮬레이터가 계산한 생성 진행률. 0..100 범위다
 * @property showReminderPrompt 홈 이동 전에 리마인드 알림 안내를 표시할지 여부
 */
data class QuizCreateUiState(
    val projectId: String,
    val stage: QuizCreateStage = QuizCreateStage.Knowledge,
    val knowledgeLevel: QuizKnowledgeLevel? = null,
    val topics: Set<QuizCreateTopic> = emptySet(),
    val generationStep: QuizGenerationStep = QuizGenerationStep.ProjectInfo,
    val progressPercent: Int = 0,
    val showReminderPrompt: Boolean = false,
) {
    /** 현재 단계의 다음 버튼을 활성화할 수 있는지 여부다. */
    val canProceed: Boolean
        get() =
            when (stage) {
                QuizCreateStage.Knowledge -> knowledgeLevel != null
                QuizCreateStage.Topics -> topics.isNotEmpty()
                QuizCreateStage.Ready -> true
                else -> false
            }
}

/** 문제 생성 설정 화면에서 발생하는 사용자 의도다. */
sealed interface QuizCreateIntent {
    /** 현재 단계에서 이전 단계로 이동한다. */
    data object BackClick : QuizCreateIntent

    /** 현재 선택을 확정하고 다음 단계로 이동한다. */
    data object NextClick : QuizCreateIntent

    /** 생성 요청을 시작한다. */
    data object StartGeneration : QuizCreateIntent

    /** 생성 중 홈에서 기다리기 안내를 연다. */
    data object WaitAtHome : QuizCreateIntent

    /** 리마인드 알림을 사용하고 홈으로 이동한다. */
    data object EnableReminder : QuizCreateIntent

    /** 리마인드 알림을 건너뛰고 홈으로 이동한다. */
    data object DismissReminder : QuizCreateIntent

    /**
     * 프로젝트 이해도를 선택한다.
     *
     * @property level 선택한 이해도
     */
    data class SelectKnowledge(
        val level: QuizKnowledgeLevel,
    ) : QuizCreateIntent

    /**
     * 문제 주제의 선택 상태를 전환한다.
     *
     * @property topic 선택 상태를 바꿀 주제
     */
    data class ToggleTopic(
        val topic: QuizCreateTopic,
    ) : QuizCreateIntent

    /**
     * UI 진행 시뮬레이터가 계산한 상태를 반영한다.
     *
     * @property step 현재 처리 단계
     * @property progressPercent 전체 진행률. 범위를 벗어난 값은 0..100으로 보정한다
     */
    data class GenerationProgressChanged(
        val step: QuizGenerationStep,
        val progressPercent: Int,
    ) : QuizCreateIntent

    /** 생성 서비스가 성공 결과를 전달했다. */
    data object GenerationSucceeded : QuizCreateIntent

    /** 생성 서비스가 실패 결과를 전달했다. */
    data object GenerationFailed : QuizCreateIntent
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

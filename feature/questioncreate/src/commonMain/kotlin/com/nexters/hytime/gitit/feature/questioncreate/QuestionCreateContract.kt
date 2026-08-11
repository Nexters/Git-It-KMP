package com.nexters.hytime.gitit.feature.questioncreate

import com.nexters.hytime.gitit.domain.model.GitHubRepository

/**
 * 질문 생성용 저장소 확인 화면의 단일 상태다.
 *
 * @property repositoryUrl 사용자가 입력한 GitHub 저장소 URL
 * @property repository 검증된 저장소. null이면 링크 입력 단계를 표시한다
 * @property isLoading 저장소 정보를 조회 중인지 여부
 * @property error 입력 필드 아래에 표시할 오류 종류
 */
data class QuestionCreateUiState(
    val repositoryUrl: String = "",
    val repository: GitHubRepository? = null,
    val isLoading: Boolean = false,
    val error: QuestionCreateError? = null,
)

/** 저장소 링크 입력 단계에서 표시할 오류 종류다. */
enum class QuestionCreateError {
    /** GitHub 저장소 URL 형식이 올바르지 않다. */
    InvalidUrl,

    /** 저장소 조회 요청에 실패했다. */
    LoadFailed,
}

/** 질문 생성용 저장소 화면에서 발생하는 사용자 의도다. */
sealed interface QuestionCreateIntent {
    /**
     * 저장소 링크 입력값을 변경한다.
     *
     * @property value 새 입력값
     */
    data class RepositoryUrlChanged(
        val value: String,
    ) : QuestionCreateIntent

    /** 저장소 링크 입력값을 비운다. */
    data object ClearRepositoryUrl : QuestionCreateIntent

    /** 입력한 저장소 링크를 검증하고 조회한다. */
    data object LoadRepository : QuestionCreateIntent

    /** 현재 단계에서 뒤로 이동한다. */
    data object BackClick : QuestionCreateIntent

    /** 확인된 저장소가 아니므로 입력 단계로 돌아간다. */
    data object RejectRepository : QuestionCreateIntent

    /** 확인된 저장소로 학습 설정을 진행한다. */
    data object ConfirmRepository : QuestionCreateIntent
}

/** 화면 밖에서 한 번만 처리할 질문 생성 이벤트다. */
sealed interface QuestionCreateEvent {
    /** 질문 생성 화면을 닫는다. */
    data object NavigateBack : QuestionCreateEvent

    /**
     * 확인한 저장소로 학습 설정을 시작한다.
     *
     * @property repository 사용자가 확인한 GitHub 저장소
     */
    data class RepositoryConfirmed(
        val repository: GitHubRepository,
    ) : QuestionCreateEvent
}

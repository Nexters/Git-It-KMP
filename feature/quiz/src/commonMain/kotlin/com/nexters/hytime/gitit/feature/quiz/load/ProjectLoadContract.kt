package com.nexters.hytime.gitit.feature.quiz.load

import com.nexters.hytime.gitit.domain.model.GitHubRepository

/**
 * 프로젝트로 등록할 저장소를 확인하는 화면의 단일 상태다.
 *
 * @property repositoryUrl 사용자가 입력한 GitHub 저장소 URL
 * @property repository 검증된 저장소. null이면 링크 입력 단계를 표시한다
 * @property isLoading 저장소 정보를 조회 중인지 여부
 * @property error 입력 필드 아래에 표시할 오류 종류
 */
data class ProjectLoadUiState(
    val repositoryUrl: String = "",
    val repository: GitHubRepository? = null,
    val isLoading: Boolean = false,
    val error: ProjectLoadError? = null,
)

/** 저장소 링크 입력 단계에서 표시할 오류 종류다. */
enum class ProjectLoadError {
    /** GitHub 저장소 URL 형식이 올바르지 않다. */
    InvalidUrl,
}

/** 프로젝트 저장소 확인 화면에서 발생하는 사용자 의도다. */
sealed interface ProjectLoadIntent {
    /**
     * 저장소 링크 입력값을 변경한다.
     *
     * @property value 새 입력값
     */
    data class RepositoryUrlChanged(
        val value: String,
    ) : ProjectLoadIntent

    /** 저장소 링크 입력값을 비운다. */
    data object ClearRepositoryUrl : ProjectLoadIntent

    /** 입력한 저장소 링크를 검증하고 조회한다. */
    data object LoadRepository : ProjectLoadIntent

    /** 현재 단계에서 뒤로 이동한다. */
    data object BackClick : ProjectLoadIntent

    /** 확인된 저장소가 아니므로 입력 단계로 돌아간다. */
    data object RejectRepository : ProjectLoadIntent

    /** 확인된 저장소로 학습 설정을 진행한다. */
    data object ConfirmRepository : ProjectLoadIntent
}

/** 화면 밖에서 한 번만 처리할 프로젝트 불러오기 이벤트다. */
sealed interface ProjectLoadEvent {
    /** 프로젝트 불러오기 화면을 닫는다. */
    data object NavigateBack : ProjectLoadEvent

    /**
     * 확인한 저장소로 학습 설정을 시작한다.
     *
     * @property repository 사용자가 확인한 GitHub 저장소
     */
    data class RepositoryConfirmed(
        val repository: GitHubRepository,
    ) : ProjectLoadEvent
}

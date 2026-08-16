package com.nexters.hytime.gitit.domain.model

/** 프로젝트 문제의 학습 깊이다. */
enum class ProjectQuizLevel {
    /** 코드 흐름을 중심으로 학습하는 입문 깊이다. */
    L1,

    /** 구현 의도와 연결 영향을 포함하는 중간 깊이다. */
    L2,

    /** 심화 문제와 서술형을 포함하는 높은 깊이다. */
    L3,
}

/** 서버가 반환하는 프로젝트의 문제 생성 상태다. */
enum class ProjectGenerationStatus {
    /** 프로젝트가 등록되어 문제 생성을 시작할 준비가 됐다. */
    Ready,

    /** 개념과 코드 앵커가 확정됐으며 문제를 생성하고 있다. */
    Anchored,

    /** 문제 생성 중 일시적인 오류가 발생했다. */
    Failed,

    /** 문제 생성이 완료되어 학습할 수 있다. */
    Completed,
}

/**
 * 백엔드에 등록된 학습 프로젝트다.
 *
 * @property projectId 프로젝트 상세와 FCM 결과를 식별하는 서버 ID
 * @property status 등록 시점의 문제 생성 상태
 */
data class ProjectRegistration(
    val projectId: String,
    val status: ProjectGenerationStatus,
)

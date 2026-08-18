package com.nexters.hytime.gitit.feature.quiz.create.session

/** 생성 결과를 기다리는 프로젝트 식별자를 프로세스 종료 뒤에도 복원하는 저장소다. */
interface PendingQuizCreationStorage {
    /** 생성 상태를 다시 조회할 프로젝트 식별자. 대기 중인 작업이 없으면 `null`이다. */
    var projectId: String?
}

/** 테스트와 플랫폼 저장소가 없는 실행 환경에서 식별자를 메모리에 보관한다. */
class InMemoryPendingQuizCreationStorage : PendingQuizCreationStorage {
    override var projectId: String? = null
}

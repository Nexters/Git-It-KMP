package com.nexters.hytime.gitit.data.di

import com.nexters.hytime.gitit.data.fake.FakeProjectRepository
import com.nexters.hytime.gitit.domain.repository.ProjectRepository
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * 프로젝트 API를 메모리 더미로 대체하는 임시 모듈이다.
 *
 * composition root에서 [dataModule] **뒤에** 등록해 [ProjectRepository] 바인딩을 덮어쓴다.
 * 서버 연동이 끝나면 등록을 지우고 이 파일과 `data.fake` 패키지를 함께 삭제한다.
 */
val fakeProjectModule: Module =
    module {
        single<ProjectRepository> { FakeProjectRepository() }
    }

package com.nexters.hytime.gitit.data.repository

import com.nexters.hytime.gitit.data.dto.ApiResponse
import com.nexters.hytime.gitit.data.dto.BookmarkQuestionRequest
import com.nexters.hytime.gitit.data.dto.BookmarkQuestionResponse
import com.nexters.hytime.gitit.data.dto.BookmarkedQuestionListResponse
import com.nexters.hytime.gitit.data.dto.EmptyApiResponse
import com.nexters.hytime.gitit.data.dto.LearningSetResponse
import com.nexters.hytime.gitit.data.dto.ProjectDetailResponse
import com.nexters.hytime.gitit.data.dto.ProjectListResponse
import com.nexters.hytime.gitit.data.dto.RegisterProjectRequest
import com.nexters.hytime.gitit.data.dto.RegisterProjectResponse
import com.nexters.hytime.gitit.data.dto.SubmitChoiceAnswerRequest
import com.nexters.hytime.gitit.data.dto.SubmitChoiceAnswerResponse
import com.nexters.hytime.gitit.data.dto.SubmitEssayAnswerRequest
import com.nexters.hytime.gitit.data.dto.SubmitEssayAnswerResponse
import com.nexters.hytime.gitit.data.mapping.toDomain
import com.nexters.hytime.gitit.domain.model.BookmarkedQuestions
import com.nexters.hytime.gitit.domain.model.ChoiceAnswerResult
import com.nexters.hytime.gitit.domain.model.EssayAnswerResult
import com.nexters.hytime.gitit.domain.model.LearningSet
import com.nexters.hytime.gitit.domain.model.ProjectDetail
import com.nexters.hytime.gitit.domain.model.ProjectPage
import com.nexters.hytime.gitit.domain.model.ProjectQuizLevel
import com.nexters.hytime.gitit.domain.model.ProjectRegistration
import com.nexters.hytime.gitit.domain.repository.ProjectRepository
import com.nexters.hytime.gitit.domain.util.runCatchingResult
import com.nexters.hytime.gitit.network.api.NetworkClient
import com.nexters.hytime.gitit.network.api.delete
import com.nexters.hytime.gitit.network.api.get
import com.nexters.hytime.gitit.network.api.post

/**
 * 백엔드 Project API를 호출하는 저장소 구현체다.
 *
 * @property networkClient HTTP 구현을 숨긴 네트워크 클라이언트
 */
class ProjectRepositoryImpl(
    private val networkClient: NetworkClient,
) : ProjectRepository {
    override suspend fun registerProject(
        githubRepoUrl: String,
        quizLevel: ProjectQuizLevel,
    ): Result<ProjectRegistration> =
        runCatchingResult {
            networkClient
                .post<RegisterProjectRequest, ApiResponse<RegisterProjectResponse>>(
                    PATH_PROJECTS,
                    RegisterProjectRequest(
                        githubRepoUrl = githubRepoUrl,
                        quizLevel = quizLevel.name,
                    ),
                ).requireData("프로젝트 등록 응답이 올바르지 않습니다.")
                .toDomain()
        }

    override suspend fun getProjects(
        page: Int,
        size: Int,
    ): Result<ProjectPage> =
        runCatchingResult {
            require(page >= 0) { "페이지 번호는 0 이상이어야 합니다." }
            require(size > 0) { "페이지 크기는 1 이상이어야 합니다." }
            networkClient
                .get<ApiResponse<ProjectListResponse>>(
                    PATH_PROJECTS,
                    queryParameters = mapOf("page" to page.toString(), "size" to size.toString()),
                ).requireData("프로젝트 목록 조회 응답이 올바르지 않습니다.")
                .toDomain()
        }

    override suspend fun getProjectDetail(projectId: String): Result<ProjectDetail> =
        runCatchingResult {
            requireProjectId(projectId)
            networkClient
                .get<ApiResponse<ProjectDetailResponse>>("$PATH_PROJECTS/$projectId")
                .requireData("프로젝트 상세 조회 응답이 올바르지 않습니다.")
                .toDomain()
        }

    override suspend fun deleteProject(projectId: String): Result<Unit> =
        runCatchingResult {
            requireProjectId(projectId)
            networkClient
                .delete<EmptyApiResponse>("$PATH_PROJECTS/$projectId")
                .requireSuccess("프로젝트 삭제 응답이 올바르지 않습니다.")
        }

    override suspend fun getLearningSet(
        projectId: String,
        setId: String,
    ): Result<LearningSet> =
        runCatchingResult {
            requireProjectId(projectId)
            require(setId.isNotBlank()) { "학습 세트 식별자가 비어 있습니다." }
            networkClient
                .get<ApiResponse<LearningSetResponse>>("$PATH_PROJECTS/$projectId/sets/$setId")
                .requireData("학습 세트 조회 응답이 올바르지 않습니다.")
                .toDomain()
        }

    override suspend fun submitChoiceAnswer(
        projectId: String,
        questionId: String,
        selectedIndex: Int,
    ): Result<ChoiceAnswerResult> =
        runCatchingResult {
            requireQuestion(projectId, questionId)
            require(selectedIndex >= 0) { "선택지 번호는 0 이상이어야 합니다." }
            networkClient
                .post<SubmitChoiceAnswerRequest, ApiResponse<SubmitChoiceAnswerResponse>>(
                    "${questionPath(projectId, questionId)}/answers/choice",
                    SubmitChoiceAnswerRequest(selectedIndex),
                ).requireData("4지선다 답변 제출 응답이 올바르지 않습니다.")
                .toDomain()
        }

    override suspend fun submitEssayAnswer(
        projectId: String,
        questionId: String,
        text: String,
    ): Result<EssayAnswerResult> =
        runCatchingResult {
            requireQuestion(projectId, questionId)
            require(text.length <= ProjectRepository.MAX_ESSAY_TEXT_LENGTH) {
                "답안은 ${ProjectRepository.MAX_ESSAY_TEXT_LENGTH}자를 넘을 수 없습니다."
            }
            networkClient
                .post<SubmitEssayAnswerRequest, ApiResponse<SubmitEssayAnswerResponse>>(
                    "${questionPath(projectId, questionId)}/answers/essay",
                    SubmitEssayAnswerRequest(text),
                ).requireData("서술형 답변 제출 응답이 올바르지 않습니다.")
                .toDomain()
        }

    override suspend fun bookmarkQuestion(
        projectId: String,
        questionId: String,
        bookmarked: Boolean,
    ): Result<Boolean> =
        runCatchingResult {
            requireQuestion(projectId, questionId)
            networkClient
                .post<BookmarkQuestionRequest, ApiResponse<BookmarkQuestionResponse>>(
                    "${questionPath(projectId, questionId)}/bookmark",
                    BookmarkQuestionRequest(bookmarked),
                ).requireData("문제 북마크 응답이 올바르지 않습니다.")
                .bookmarked
        }

    override suspend fun getBookmarkedQuestions(projectId: String?): Result<BookmarkedQuestions> =
        runCatchingResult {
            networkClient
                .get<ApiResponse<BookmarkedQuestionListResponse>>(
                    "$PATH_PROJECTS/bookmarks",
                    queryParameters = projectId?.let { mapOf("projectId" to it) } ?: emptyMap(),
                ).requireData("북마크 목록 조회 응답이 올바르지 않습니다.")
                .toDomain()
        }

    /**
     * 프로젝트 식별자가 경로에 넣을 수 있는 값인지 확인한다.
     *
     * @param projectId 검사할 프로젝트 식별자
     * @throws IllegalArgumentException 값이 비어 있는 경우
     */
    private fun requireProjectId(projectId: String) {
        require(projectId.isNotBlank()) { "프로젝트 식별자가 비어 있습니다." }
    }

    /**
     * 문제 관련 요청에 필요한 식별자가 모두 채워졌는지 확인한다.
     *
     * @param projectId 문제가 속한 프로젝트 식별자
     * @param questionId 문제 식별자
     * @throws IllegalArgumentException 둘 중 하나라도 비어 있는 경우
     */
    private fun requireQuestion(
        projectId: String,
        questionId: String,
    ) {
        requireProjectId(projectId)
        require(questionId.isNotBlank()) { "문제 식별자가 비어 있습니다." }
    }

    /**
     * 문제 하나를 가리키는 요청 경로를 만든다.
     *
     * @param projectId 문제가 속한 프로젝트 식별자
     * @param questionId 문제 식별자
     * @return 답변 제출·북마크 경로의 공통 앞부분
     */
    private fun questionPath(
        projectId: String,
        questionId: String,
    ): String = "$PATH_PROJECTS/$projectId/questions/$questionId"

    private companion object {
        private const val PATH_PROJECTS = "/api/v1/projects"
    }
}

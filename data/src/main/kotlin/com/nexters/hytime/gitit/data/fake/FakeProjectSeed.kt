package com.nexters.hytime.gitit.data.fake

import com.nexters.hytime.gitit.domain.model.ProjectQuizLevel
import com.nexters.hytime.gitit.domain.model.Question
import com.nexters.hytime.gitit.domain.model.QuestionFormat
import com.nexters.hytime.gitit.domain.model.QuestionSource
import com.nexters.hytime.gitit.domain.model.Rubric
import com.nexters.hytime.gitit.domain.model.RubricCriterion

/**
 * [FakeProjectRepository]가 처음 들고 시작할 더미 프로젝트 목록이다.
 *
 * 진행률 0%·중간·100% 카드가 한 화면에 모두 보이도록 세 프로젝트를 서로 다른 상태로 둔다.
 *
 * @return 목록 순서 그대로의 더미 프로젝트
 */
internal fun fakeProjectSeed(): List<FakeProject> =
    listOf(
        nowInAndroidProject(),
        ktorProject(),
        coilProject(),
    )

/**
 * 문제 등록 화면에서 저장소를 새로 등록했을 때 만들어 줄 더미 프로젝트다.
 *
 * @param githubRepoUrl 사용자가 입력한 저장소 URL
 * @param quizLevel 사용자가 고른 학습 깊이
 * @param ordinal 이번 세션에서 몇 번째로 등록한 프로젝트인지. 식별자 충돌을 막는 데 쓴다
 * @return 문제 두 개짜리 세트 하나를 가진 새 프로젝트
 */
internal fun newFakeProject(
    githubRepoUrl: String,
    quizLevel: ProjectQuizLevel,
    ordinal: Int,
): FakeProject {
    val trimmed = githubRepoUrl.trimEnd('/')
    val name = trimmed.substringAfterLast('/').ifBlank { "new-project" }
    val projectId = "dummy-registered-$ordinal"
    val setId = "$projectId-set-1"
    return FakeProject(
        projectId = projectId,
        repositoryName = name,
        repositoryUrl = trimmed.ifBlank { "https://github.com" },
        repositoryImageUrl = "https://avatars.githubusercontent.com/u/9919?s=200&v=4",
        starCount = 1_284,
        techStack = listOf("Kotlin", "Gradle"),
        presolvedCount = 0,
        sets =
            listOf(
                FakeSet(
                    setId = setId,
                    label = "Set 1",
                    title = "저장소 첫인상 잡기",
                    description = "$name 저장소를 처음 열었을 때 어디부터 읽어야 할지 짚는 세트입니다.",
                    orientation = "README와 최상위 디렉터리 구조를 먼저 훑고 문제를 풀어 보세요.",
                    level = quizLevel,
                    questions =
                        listOf(
                            choiceQuestion(
                                questionId = "$setId-q1",
                                text =
                                    "$name 저장소를 처음 열었습니다. 어떤 모듈이 실제로 빌드에 포함되는지 확인하려면 " +
                                        "무엇을 가장 먼저 봐야 하나요? 디렉터리 목록만 훑고 판단할 때 어떤 착각이 생길 수 있는지도 " +
                                        "함께 생각해 보세요.",
                                choices =
                                    listOf(
                                        "settings.gradle.kts의 include 목록. 소스 없이 build 폴더만 남은 디렉터리에 속지 않는다",
                                        "README.md의 프로젝트 소개 문단. 모듈 구조가 보통 가장 먼저 설명되어 있다",
                                        "최상위 디렉터리 목록. 폴더 하나가 곧 모듈 하나이므로 가장 빠르게 파악된다",
                                        ".gitignore. 빌드에서 제외되는 모듈이 이 파일에 정리되어 있다",
                                    ),
                                answerIndex = 0,
                                explanation =
                                    "모듈의 실체는 settings.gradle.kts의 include 목록입니다. " +
                                        "빌드 산출물만 남은 디렉터리는 모듈처럼 보이지만 빌드에 들어가지 않으므로, 폴더만 보고 세면 실제와 어긋납니다.",
                            ),
                            essayQuestion(
                                questionId = "$setId-q2",
                                text =
                                    "$name 저장소의 디렉터리 구조를 훑어보고 어떤 레이어로 나뉘어 있는지 설명해 보세요. " +
                                        "폴더 이름이 아니라 모듈 사이의 의존 방향을 근거로 삼아야 하고, " +
                                        "어느 모듈이 어느 모듈을 참조하는지 한 쌍 이상 들어 주세요.",
                                explanation =
                                    "레이어를 가르는 기준은 이름이 아니라 화살표의 방향입니다. " +
                                        "안쪽 레이어가 바깥을 참조하지 않는지 확인하면, 폴더 구조가 달라도 같은 규칙을 읽어 낼 수 있습니다.",
                            ),
                        ),
                ),
            ),
    )
}

/**
 * 서술형 문제의 자가채점 기준이다. 더미라 문제마다 다르게 두지 않고 하나를 공유한다.
 *
 * @return 기준·핵심·예시 답안이 채워진 채점표
 */
internal fun fakeRubric(): Rubric =
    Rubric(
        criteria =
            listOf(
                RubricCriterion(text = "실제 파일명과 클래스명을 들어 설명했는가", points = 40),
                RubricCriterion(text = "데이터가 흐르는 순서를 빠뜨리지 않았는가", points = 40),
                RubricCriterion(text = "그렇게 설계한 이유를 함께 적었는가", points = 20),
            ),
        keyPoints =
            listOf(
                "어느 파일의 어느 함수에서 시작하는지 짚기",
                "호출 순서를 순서대로 나열하기",
                "다른 선택지 대신 이 구조를 고른 이유 적기",
            ),
        fullMarkExample = "진입점 파일명을 밝히고, 호출이 거쳐 가는 순서를 차례대로 적은 뒤, 이 구조가 테스트를 쉽게 만든다는 이유까지 덧붙인 답안입니다.",
        partialExample = "동작 순서는 맞게 적었지만 근거가 되는 파일명을 들지 않아 어디를 읽고 답했는지 확인할 수 없는 답안입니다.",
        zeroExample = "\"의존성 주입을 사용합니다\"처럼 저장소를 읽지 않아도 쓸 수 있는 일반론만 적은 답안입니다.",
    )

/**
 * 4지선다 더미 문제를 만든다.
 *
 * @param questionId 문제 식별자
 * @param text 문제 본문
 * @param choices 선택지
 * @param answerIndex 정답 선택지 번호(0부터)
 * @param explanation 제출 뒤 보여줄 해설
 * @param sources 문제가 인용한 코드 위치
 * @return 정답과 해설이 붙은 더미 문제
 */
private fun choiceQuestion(
    questionId: String,
    text: String,
    choices: List<String>,
    answerIndex: Int,
    explanation: String,
    sources: List<QuestionSource> = emptyList(),
): FakeQuestion =
    FakeQuestion(
        question =
            Question(
                questionId = questionId,
                format = QuestionFormat.MULTIPLE_CHOICE,
                text = text,
                choices = choices,
                sources = sources,
                myAnswer = null,
            ),
        answerIndex = answerIndex,
        explanation = explanation,
    )

/**
 * 서술형 더미 문제를 만든다.
 *
 * @param questionId 문제 식별자
 * @param text 문제 본문
 * @param explanation 제출 뒤 보여줄 해설
 * @param sources 문제가 인용한 코드 위치
 * @return 선택지가 비어 있는 더미 문제
 */
private fun essayQuestion(
    questionId: String,
    text: String,
    explanation: String,
    sources: List<QuestionSource> = emptyList(),
): FakeQuestion =
    FakeQuestion(
        question =
            Question(
                questionId = questionId,
                format = QuestionFormat.ESSAY,
                text = text,
                choices = emptyList(),
                sources = sources,
                myAnswer = null,
            ),
        answerIndex = -1,
        explanation = explanation,
    )

/**
 * 코드 인용 위치를 만든다.
 *
 * @param repositoryUrl 저장소 링크
 * @param file 저장소 루트 기준 상대 경로
 * @param startLine 인용한 첫 줄
 * @param endLine 인용한 마지막 줄
 * @param symbol 그 범위의 식별자
 * @param summary 이 자리가 무엇을 하는 곳인지
 * @return GitHub 링크가 채워진 인용 위치
 */
private fun source(
    repositoryUrl: String,
    file: String,
    startLine: Int,
    endLine: Int,
    symbol: String,
    summary: String,
): QuestionSource =
    QuestionSource(
        file = file,
        startLine = startLine,
        endLine = endLine,
        symbol = symbol,
        summary = summary,
        url = "$repositoryUrl/blob/main/$file#L$startLine-L$endLine",
    )

/** Now in Android 더미 프로젝트다. 문제 여덟 개 중 다섯 개를 푼 중간 진행 상태로 둔다. */
private fun nowInAndroidProject(): FakeProject {
    val url = "https://github.com/android/nowinandroid"
    return FakeProject(
        projectId = "dummy-nowinandroid",
        repositoryName = "Now in Android",
        repositoryUrl = url,
        repositoryImageUrl = "https://avatars.githubusercontent.com/u/32689599?s=200&v=4",
        starCount = 17_842,
        techStack = listOf("Kotlin", "Compose", "Hilt", "Coroutines"),
        presolvedCount = 5,
        sets =
            listOf(
                FakeSet(
                    setId = "dummy-nia-set-1",
                    label = "Set 1",
                    title = "모듈 구조 파악하기",
                    description = "app, feature, core로 나뉜 모듈이 서로를 어떻게 참조하는지 따라갑니다.",
                    orientation = "settings.gradle.kts의 include 목록부터 열어 모듈 이름을 먼저 눈에 익혀 두세요.",
                    level = ProjectQuizLevel.L1,
                    questions =
                        listOf(
                            choiceQuestion(
                                questionId = "dummy-nia-q1",
                                text =
                                    "nowinandroid는 :app, :feature:*, :core:* 세 층으로 모듈을 나눠 두었습니다. " +
                                        "이 중 :core:data 모듈이 맡고 있는 책임을 가장 정확하게 설명한 것은 무엇인가요? " +
                                        "모듈 이름이 아니라 그 안에 실제로 어떤 클래스가 들어 있는지를 기준으로 골라 보세요.",
                                choices =
                                    listOf(
                                        "Repository 구현체를 두고, Room DAO와 네트워크 응답을 하나의 Flow로 합쳐 상위 레이어에 내려보낸다",
                                        "화면에 그릴 Composable과 그 화면만 쓰는 ViewModel을 모아 두고 다른 feature 모듈이 재사용하게 한다",
                                        "Hilt 모듈만 선언해 두고, 실제 구현체는 :app 모듈이 런타임에 찾아 연결하도록 맡긴다",
                                        "compileSdk와 jvmTarget 같은 빌드 설정을 모아 둔 Gradle 컨벤션 플러그인을 정의한다",
                                    ),
                                answerIndex = 0,
                                explanation =
                                    "core:data에는 OfflineFirstTopicsRepository처럼 로컬 DB와 네트워크를 합치는 구현체가 삽니다. " +
                                        "Composable은 feature 모듈에, 컨벤션 플러그인은 build-logic에 있으므로 나머지 선택지는 다른 모듈의 설명입니다.",
                                sources =
                                    listOf(
                                        source(
                                            repositoryUrl = url,
                                            file =
                                                "core/data/src/main/kotlin/com/google/samples/apps/nowinandroid/" +
                                                    "core/data/repository/OfflineFirstTopicsRepository.kt",
                                            startLine = 34,
                                            endLine = 62,
                                            symbol = "OfflineFirstTopicsRepository",
                                            summary = "로컬 DB를 단일 진실 공급원으로 두고 네트워크 변경분을 동기화하는 Repository입니다.",
                                        ),
                                    ),
                            ),
                            choiceQuestion(
                                questionId = "dummy-nia-q2",
                                text =
                                    "feature 모듈끼리 서로를 직접 참조하지 못하도록 빌드 스크립트에서 막아 두었습니다. " +
                                        "화면 A에서 화면 B로 이동해야 하는 상황이 분명히 있는데도 이런 제약을 건 이유는 무엇인가요?",
                                choices =
                                    listOf(
                                        "Compose 컴파일러가 모듈 간 순환 참조를 만나면 재구성 범위를 계산하지 못하기 때문",
                                        "화면 단위로 따로 빌드하고 테스트하도록 결합을 끊고, 화면 이동은 :app의 네비게이션 그래프가 중재하게 하려고",
                                        "Hilt가 서로 다른 feature 모듈 사이의 의존성 주입을 문법적으로 금지하기 때문",
                                        "R8이 참조되지 않는 feature 모듈을 릴리스 빌드에서 통째로 제거해 버리기 때문",
                                    ),
                                answerIndex = 1,
                                explanation =
                                    "feature 사이에 공유할 코드가 생기면 core로 내리고, 이동 경로는 app 모듈이 알고 있습니다. " +
                                        "덕분에 한 화면만 떼어 빌드하고 테스트할 수 있으며, 화면이 늘어도 서로의 빌드 시간을 끌어당기지 않습니다.",
                            ),
                            essayQuestion(
                                questionId = "dummy-nia-q3",
                                text =
                                    "NiaAppState는 현재 선택된 탭, 네비게이션 컨트롤러, 네트워크 연결 상태를 한 객체에 모아 들고 있습니다. " +
                                        "이 값들을 각 화면 Composable이 remember로 따로 들고 있을 때 생기는 문제와 비교해서, " +
                                        "한곳에 모아 둔 이유를 설명해 보세요.",
                                explanation =
                                    "여러 화면이 함께 보는 값을 Composable마다 나눠 들면 같은 의미의 상태가 두 벌이 되어 서로 어긋납니다. " +
                                        "소유권을 한 곳으로 모으면 어긋날 자리가 사라집니다.",
                            ),
                        ),
                ),
                FakeSet(
                    setId = "dummy-nia-set-2",
                    label = "Set 2",
                    title = "데이터 흐름 따라가기",
                    description = "네트워크 응답이 화면까지 도달하는 경로를 순서대로 짚습니다.",
                    orientation = "Repository → UseCase → ViewModel 순으로 함수 하나씩 따라 읽어 보세요.",
                    level = ProjectQuizLevel.L2,
                    questions =
                        listOf(
                            choiceQuestion(
                                questionId = "dummy-nia-q4",
                                text =
                                    "offline-first 구조에서는 네트워크와 로컬 DB 두 곳에 같은 데이터가 있습니다. " +
                                        "화면이 실제로 구독해서 값이 바뀔 때마다 다시 그리는 대상은 둘 중 어느 쪽이고, " +
                                        "나머지 하나는 어떤 역할을 맡나요?",
                                choices =
                                    listOf(
                                        "네트워크 응답 Flow를 직접 구독하고, 로컬 DB는 오프라인일 때만 쓰는 대체 수단으로 둔다",
                                        "메모리 캐시 Map을 구독하고, DB와 네트워크는 앱을 처음 켤 때 한 번씩만 읽는다",
                                        "Room DAO가 내보내는 Flow를 구독하고, 네트워크는 그 DB를 갱신하는 쪽으로만 동작한다",
                                        "DataStore의 Preferences를 구독하고, DB에는 이미지처럼 큰 데이터만 따로 담는다",
                                    ),
                                answerIndex = 2,
                                explanation =
                                    "화면은 언제나 DB를 봅니다. 네트워크는 DB에 값을 써 넣는 쪽이라 응답이 늦거나 실패해도 화면이 비지 않고, " +
                                        "오프라인에서는 마지막으로 받아 둔 데이터가 그대로 보입니다.",
                            ),
                            choiceQuestion(
                                questionId = "dummy-nia-q5",
                                text =
                                    "Syncable 인터페이스의 syncWith는 서버와 로컬을 맞추는 진입점입니다. " +
                                        "이 함수가 전체 데이터를 매번 다시 내려받지 않고도 동기화를 끝낼 수 있는 이유는 무엇인가요?",
                                choices =
                                    listOf(
                                        "changeListVersion 토큰을 들고 다니며, 그 이후에 바뀐 항목의 목록만 받아 반영하기 때문",
                                        "로컬 DB를 통째로 비운 뒤 서버 응답으로 다시 채우는 편이 언제나 더 빠르기 때문",
                                        "WorkManager가 변경된 항목을 대신 추적해 두었다가 앱에 알려 주기 때문",
                                        "네트워크 연결 상태만 확인하고 실제 데이터 비교는 서버가 대신 처리해 주기 때문",
                                    ),
                                answerIndex = 0,
                                explanation =
                                    "마지막으로 반영한 버전 번호를 들고 다니며 그 뒤의 변경 목록만 요청합니다. " +
                                        "전체를 다시 받는 방식은 데이터가 늘수록 비용이 선형으로 커지지만, 변경분 방식은 실제로 바뀐 만큼만 듭니다.",
                            ),
                            essayQuestion(
                                questionId = "dummy-nia-q6",
                                text =
                                    "동기화 도중에 앱이 강제로 종료되어도 다음 실행에서 데이터가 어긋나지 않습니다. " +
                                        "버전 토큰을 갱신하는 시점이 데이터 반영보다 앞인지 뒤인지 짚고, " +
                                        "그 순서 때문에 중간에 끊겼을 때 무슨 일이 벌어지는지 코드 근거를 들어 설명해 보세요.",
                                explanation =
                                    "토큰은 데이터를 다 반영한 뒤에 갱신됩니다. 중간에 끊기면 토큰이 예전 값 그대로 남아, " +
                                        "다음 동기화가 같은 구간을 다시 가져옵니다. 같은 데이터를 두 번 써도 결과가 같으므로 안전합니다.",
                            ),
                        ),
                ),
                FakeSet(
                    setId = "dummy-nia-set-3",
                    label = "Set 3",
                    title = "Compose UI 상태 관리",
                    description = "ViewModel의 UiState가 화면에 그려지기까지를 확인합니다.",
                    orientation = "stateIn에 넘긴 SharingStarted 값을 눈여겨보세요.",
                    level = ProjectQuizLevel.L3,
                    questions =
                        listOf(
                            choiceQuestion(
                                questionId = "dummy-nia-q7",
                                text =
                                    "ViewModel에서 Flow를 StateFlow로 바꿀 때 " +
                                        "stateIn(scope, SharingStarted.WhileSubscribed(5_000), initialValue) 형태를 씁니다. " +
                                        "두 번째 인자에 5초라는 값을 넘긴 의도는 무엇인가요?",
                                choices =
                                    listOf(
                                        "5초마다 업스트림 Flow를 다시 실행해 최신 데이터를 주기적으로 받아 오려고",
                                        "화면 회전처럼 구독이 잠깐 끊겼다 이어지는 동안 업스트림을 살려 두어 데이터를 다시 받지 않으려고",
                                        "업스트림 Flow가 5초 안에 값을 내보내지 못하면 타임아웃 예외를 던지게 하려고",
                                        "첫 화면에 진입할 때 로딩 애니메이션을 최소 5초 동안 보여 주려고",
                                    ),
                                answerIndex = 1,
                                explanation =
                                    "구성 변경으로 구독이 잠깐 사라졌다고 데이터를 처음부터 다시 받으면 낭비입니다. " +
                                        "5초는 그 사이를 견디는 유예 구간이고, 진짜로 화면을 떠났다면 그 뒤에 업스트림이 정리됩니다.",
                            ),
                            essayQuestion(
                                questionId = "dummy-nia-q8",
                                text =
                                    "nowinandroid는 화면 상태를 data class 하나가 아니라 Loading·Success·Error를 갖는 " +
                                        "sealed interface로 표현합니다. 화면 코드에서 이 선택이 어떤 차이를 만드는지, " +
                                        "when으로 상태를 분기하는 상황을 예로 들어 설명해 보세요.",
                                explanation =
                                    "sealed로 두면 when이 모든 갈래를 강제로 다루게 되어 로딩과 에러 처리를 빠뜨릴 수 없습니다. " +
                                        "상태가 하나 늘면 컴파일이 먼저 알려 주므로, 화면이 빈 채로 남는 사고를 컴파일 단계에서 막습니다.",
                            ),
                        ),
                ),
            ),
    )
}

/** Ktor 더미 프로젝트다. 이제 막 시작한 낮은 진행률 상태로 둔다. */
private fun ktorProject(): FakeProject {
    val url = "https://github.com/ktorio/ktor"
    return FakeProject(
        projectId = "dummy-ktor",
        repositoryName = "ktor",
        repositoryUrl = url,
        repositoryImageUrl = "https://avatars.githubusercontent.com/u/878437?s=200&v=4",
        starCount = 13_240,
        techStack = listOf("Kotlin", "Coroutines", "Netty"),
        presolvedCount = 1,
        sets =
            listOf(
                FakeSet(
                    setId = "dummy-ktor-set-1",
                    label = "Set 1",
                    title = "요청 파이프라인 이해하기",
                    description = "요청 하나가 인터셉터를 거쳐 응답이 되기까지를 따라갑니다.",
                    orientation = "ApplicationCallPipeline의 phase 목록을 먼저 확인해 보세요.",
                    level = ProjectQuizLevel.L2,
                    questions =
                        listOf(
                            choiceQuestion(
                                questionId = "dummy-ktor-q1",
                                text =
                                    "Ktor 서버는 요청 하나를 처리할 때 ApplicationCallPipeline을 따라 인터셉터를 차례로 실행합니다. " +
                                        "이 파이프라인이 Setup, Monitoring, Call처럼 이름 붙은 phase로 나뉘어 있는 이유는 무엇인가요?",
                                choices =
                                    listOf(
                                        "인터셉터의 실행 순서를 이름 붙은 구간으로 고정해, 플러그인이 원하는 위치에 자기 인터셉터를 끼워 넣게 하려고",
                                        "phase 개수만큼 스레드 풀을 나누어 요청을 병렬로 처리하려고",
                                        "라우팅 경로를 phase별 정규식으로 나누어 매칭 속도를 높이려고",
                                        "응답 본문을 phase마다 한 번씩 압축해 전송량을 줄이려고",
                                    ),
                                answerIndex = 0,
                                explanation =
                                    "플러그인은 자기가 어느 phase에서 동작할지만 정하면 됩니다. " +
                                        "설치 순서가 뒤바뀌어도 실행 순서는 phase가 보장하므로, 서로를 모르는 플러그인끼리도 예측 가능하게 겹쳐 쌓입니다.",
                                sources =
                                    listOf(
                                        source(
                                            repositoryUrl = url,
                                            file =
                                                "ktor-server/ktor-server-core/common/src/io/ktor/server/" +
                                                    "application/ApplicationCallPipeline.kt",
                                            startLine = 20,
                                            endLine = 58,
                                            symbol = "ApplicationCallPipeline",
                                            summary = "요청 처리 구간을 phase로 나누어 인터셉터 실행 순서를 정하는 파이프라인입니다.",
                                        ),
                                    ),
                            ),
                            choiceQuestion(
                                questionId = "dummy-ktor-q2",
                                text =
                                    "서버 설정에 install(ContentNegotiation) { json() } 한 줄을 적으면 요청 본문이 알아서 객체로 바뀝니다. " +
                                        "이 한 줄이 실제로 하는 일에 가장 가까운 설명은 무엇인가요?",
                                choices =
                                    listOf(
                                        "새 서버 엔진 인스턴스를 하나 더 띄워 JSON 요청만 따로 받아 처리하게 한다",
                                        "파이프라인의 특정 phase에 직렬화·역직렬화 인터셉터를 등록해 둔다",
                                        "라우팅 트리를 다시 만들어 모든 경로에 Content-Type 검사를 붙인다",
                                        "코루틴 디스패처를 JSON 파싱 전용 스레드 풀로 교체한다",
                                    ),
                                answerIndex = 1,
                                explanation =
                                    "플러그인 설치는 결국 파이프라인의 특정 지점에 인터셉터를 더하는 일입니다. " +
                                        "그래서 install 순서를 바꿔도 동작이 크게 흔들리지 않고, 설치하지 않으면 그 인터셉터만 빠집니다.",
                            ),
                            essayQuestion(
                                questionId = "dummy-ktor-q3",
                                text =
                                    "Ktor는 기능을 더할 때 클래스를 상속하는 대신 install로 플러그인을 설치합니다. " +
                                        "로깅·인증·압축을 각각 상속으로 조합해야 한다면 클래스가 어떻게 불어나는지 떠올려 보고, " +
                                        "설치 방식이 그 문제를 어떻게 피하는지 설명해 보세요.",
                                explanation =
                                    "상속으로 기능을 조합하면 기능 수에 따라 조합 클래스가 곱으로 늘어납니다. " +
                                        "설치 방식은 필요한 기능만 골라 쌓는 덧셈이라, 새 기능이 기존 조합을 건드리지 않습니다.",
                            ),
                        ),
                ),
                FakeSet(
                    setId = "dummy-ktor-set-2",
                    label = "Set 2",
                    title = "클라이언트 엔진 갈아 끼우기",
                    description = "HttpClient가 엔진에 의존하지 않도록 만든 경계를 확인합니다.",
                    orientation = "HttpClientEngine 인터페이스가 어디까지 노출되는지 보세요.",
                    level = ProjectQuizLevel.L2,
                    questions =
                        listOf(
                            choiceQuestion(
                                questionId = "dummy-ktor-q4",
                                text =
                                    "HttpClient는 CIO, OkHttp, Darwin 같은 엔진 중 하나를 골라 만듭니다. " +
                                        "클라이언트가 특정 엔진에 묶이지 않도록 인터페이스로 갈라 둔 설계가 " +
                                        "KMP 프로젝트에서 만들어 내는 이점은 무엇인가요?",
                                choices =
                                    listOf(
                                        "플랫폼마다 다른 엔진을 쓰면서도 요청을 만드는 코드는 공통 소스셋에 그대로 둘 수 있다",
                                        "엔진을 바꿀 때마다 네트워크 왕복 시간이 눈에 띄게 줄어든다",
                                        "응답을 객체로 바꾸는 직렬화 코드를 아예 쓰지 않아도 된다",
                                        "TLS 인증서 검증을 엔진이 알아서 생략해 주어 설정이 필요 없어진다",
                                    ),
                                answerIndex = 0,
                                explanation =
                                    "Android는 OkHttp, Desktop은 CIO를 쓰더라도 요청을 조립하는 코드는 commonMain에 한 벌만 둡니다. " +
                                        "엔진은 각 플랫폼 진입점에서만 정해지므로 교체 비용이 그 지점에 갇힙니다.",
                            ),
                            essayQuestion(
                                questionId = "dummy-ktor-q5",
                                text =
                                    "우리 프로젝트의 core:network도 Ktor를 아는 유일한 모듈로 두고 바깥에는 Ktor 타입을 노출하지 않습니다. " +
                                        "만약 공개 API에 HttpResponse가 그대로 새어 나간다면 어떤 비용이 생기는지, " +
                                        "경계에서 무엇을 무엇으로 바꿔 줘야 하는지 적어 보세요.",
                                explanation =
                                    "타입이 새면 그 타입을 쓰는 모든 모듈이 Ktor에 묶여, 교체 비용이 바깥까지 번집니다. " +
                                        "경계에서 Ktor 예외와 응답을 자체 네트워크 오류·도메인 타입으로 바꿔야 안쪽이 라이브러리를 모르는 상태로 남습니다.",
                            ),
                        ),
                ),
            ),
    )
}

/** Coil 더미 프로젝트다. 모든 문제를 푼 완료 상태로 둔다. */
private fun coilProject(): FakeProject {
    val url = "https://github.com/coil-kt/coil"
    return FakeProject(
        projectId = "dummy-coil",
        repositoryName = "coil",
        repositoryUrl = url,
        repositoryImageUrl = "https://avatars.githubusercontent.com/u/52722434?s=200&v=4",
        starCount = 10_930,
        techStack = listOf("Kotlin", "Compose", "OkHttp"),
        presolvedCount = 5,
        sets =
            listOf(
                FakeSet(
                    setId = "dummy-coil-set-1",
                    label = "Set 1",
                    title = "이미지 로딩 파이프라인",
                    description = "ImageRequest 하나가 화면에 그려지기까지의 단계를 짚습니다.",
                    orientation = "Interceptor 체인의 마지막에 무엇이 오는지 확인해 보세요.",
                    level = ProjectQuizLevel.L2,
                    questions =
                        listOf(
                            choiceQuestion(
                                questionId = "dummy-coil-q1",
                                text =
                                    "Coil은 ImageRequest 하나를 처리할 때 Interceptor 체인을 순서대로 통과시킵니다. " +
                                        "앞선 인터셉터들이 모두 요청을 통과시켰을 때, 체인의 마지막에서 실제로 무슨 일이 일어나나요?",
                                choices =
                                    listOf(
                                        "메모리 캐시를 한 번 더 확인한 뒤, 값이 없으면 요청을 조용히 취소한다",
                                        "EngineInterceptor가 Fetcher로 바이트를 가져오고 Decoder로 비트맵을 만든다",
                                        "Compose 렌더러가 직접 네트워크를 호출해 이미지를 그린다",
                                        "요청 로그만 남기고 실제 로딩은 다음 프레임으로 미룬다",
                                    ),
                                answerIndex = 1,
                                explanation =
                                    "캐시 조회처럼 값싼 판단은 앞쪽 인터셉터가 맡고, 실제로 비용이 드는 fetch와 decode는 체인 끝에서 일어납니다. " +
                                        "그래서 캐시에 값이 있으면 뒤쪽 단계까지 내려가지 않습니다.",
                            ),
                            choiceQuestion(
                                questionId = "dummy-coil-q2",
                                text =
                                    "AsyncImage를 쓴 리스트에서 스크롤 때문에 재구성이 수십 번 일어나도 같은 이미지를 다시 내려받지는 않습니다. " +
                                        "이런 동작이 가능한 가장 직접적인 이유는 무엇인가요?",
                                choices =
                                    listOf(
                                        "요청 키가 같으면 메모리 캐시에서 바로 꺼내 쓰므로 디스크나 네트워크까지 내려가지 않는다",
                                        "Compose가 재구성 중에는 네트워크 호출 자체를 막아 두기 때문이다",
                                        "이미지가 빌드 시점에 리소스로 컴파일되어 앱에 포함되기 때문이다",
                                        "재구성이 일어나도 Composable 함수 본문은 다시 실행되지 않기 때문이다",
                                    ),
                                answerIndex = 0,
                                explanation =
                                    "MemoryCache.Key가 같으면 이미 만들어 둔 비트맵을 그대로 씁니다. " +
                                        "키에는 URL뿐 아니라 크기와 변환도 들어가므로, 같은 URL이라도 표시 크기가 다르면 다른 항목이 됩니다.",
                            ),
                            essayQuestion(
                                questionId = "dummy-coil-q3",
                                text =
                                    "리스트를 빠르게 스크롤하면 화면 밖으로 밀려난 항목의 이미지 요청이 그대로 남습니다. " +
                                        "이미지 로딩 라이브러리가 요청 취소를 지원하지 않는다면 무엇이 낭비되는지, " +
                                        "그 낭비가 사용자에게 어떤 모습으로 보일지 함께 설명해 보세요.",
                                explanation =
                                    "이미 안 보이는 이미지에 대역폭과 디코딩 시간을 쓰게 됩니다. " +
                                        "정작 지금 화면에 필요한 요청이 뒤로 밀려, 스크롤을 멈춰도 한동안 빈 칸이 남는 모습으로 드러납니다.",
                            ),
                        ),
                ),
                FakeSet(
                    setId = "dummy-coil-set-2",
                    label = "Set 2",
                    title = "메모리와 디스크 캐시",
                    description = "두 단계 캐시가 각각 무엇을 책임지는지 구분합니다.",
                    orientation = "캐시 키가 어떻게 만들어지는지부터 보세요.",
                    level = ProjectQuizLevel.L3,
                    questions =
                        listOf(
                            choiceQuestion(
                                questionId = "dummy-coil-q4",
                                text =
                                    "Coil은 메모리 캐시와 디스크 캐시의 키를 서로 다르게 만듭니다. " +
                                        "같은 이미지 URL로 요청했는데도 두 캐시가 다른 키를 쓰는 이유는 무엇인가요?",
                                choices =
                                    listOf(
                                        "메모리에는 크기 조정과 변환까지 끝난 비트맵이, 디스크에는 내려받은 원본 바이트가 담기기 때문",
                                        "디스크 캐시는 문자열 키를 지원하지 않아 해시 값을 대신 쓰기 때문",
                                        "두 캐시의 만료 시간을 반드시 같게 맞춰야 하기 때문",
                                        "메모리 캐시는 키 없이 최근 항목을 순서대로만 보관하기 때문",
                                    ),
                                answerIndex = 0,
                                explanation =
                                    "같은 원본이라도 표시 크기와 변환이 다르면 서로 다른 비트맵입니다. " +
                                        "원본 한 벌만 담는 디스크와 키를 공유하면, 크기가 다른 요청이 서로의 결과를 덮어쓰게 됩니다.",
                            ),
                            essayQuestion(
                                questionId = "dummy-coil-q5",
                                text =
                                    "캐시를 메모리와 디스크 두 단계로 나눈 설계를 우리 앱의 프로젝트 목록 화면에 적용한다면, " +
                                        "어떤 값을 어느 쪽에 담을지 정하고 그렇게 나눈 기준을 설명해 보세요. " +
                                        "값의 수명과 다시 만드는 비용을 함께 따져 보면 좋습니다.",
                                explanation =
                                    "다시 받기 비싼 응답은 디스크에, 화면 전환 사이에만 필요한 값은 메모리에 둡니다. " +
                                        "기준은 저장 위치가 아니라 값의 수명입니다.",
                            ),
                        ),
                ),
            ),
    )
}

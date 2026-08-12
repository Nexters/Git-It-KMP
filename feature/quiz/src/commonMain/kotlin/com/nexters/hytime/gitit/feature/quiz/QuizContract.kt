package com.nexters.hytime.gitit.feature.quiz

/**
 * 문제 풀이 세트 소개 정보다.
 *
 * @property label 세트 순서를 나타내는 짧은 이름
 * @property title 세트에서 학습할 주제
 * @property description 세트의 학습 범위 설명
 */
data class QuizSetInfo(
    val label: String,
    val title: String,
    val description: String,
)

/**
 * 객관식 답안 하나다.
 *
 * @property id 선택과 채점에 사용하는 식별자
 * @property label 화면에 표시할 알파벳 기호
 * @property text 답안 내용
 */
data class QuizAnswer(
    val id: String,
    val label: String,
    val text: String,
)

/**
 * 문제 출처 바텀시트에 표시할 정보다.
 *
 * @property description 출처 코드가 문제와 연결되는 맥락
 * @property label 출처 링크에 표시할 파일과 라인
 * @property url 외부에서 열 원본 코드 주소
 */
data class QuizSource(
    val description: String,
    val label: String,
    val url: String,
)

/**
 * 로컬 샘플 객관식 문제다.
 *
 * @property number 세트 안에서 표시할 문제 순서
 * @property text 질문 본문
 * @property answers 사용자가 선택할 답안 목록
 * @property correctAnswerId 정답으로 판정할 답안 식별자
 * @property explanation 채점 후 표시할 해설
 * @property source 문제와 연결된 원본 코드 정보
 */
data class QuizQuestion(
    val number: Int,
    val text: String,
    val answers: List<QuizAnswer>,
    val correctAnswerId: String,
    val explanation: String,
    val source: QuizSource,
)

/**
 * 로컬 샘플 서술형 문제다.
 *
 * @property number 세트 안에서 표시할 문제 순서
 * @property text 질문 본문
 * @property modelAnswer 채점 후 비교할 AI 모범 답안
 * @property source 추후 출처 버튼에서 사용할 원본 코드 정보
 */
data class EssayQuestion(
    val number: Int,
    val text: String,
    val modelAnswer: String,
    val source: QuizSource,
)

/** 문제 풀이 플로우의 현재 단계를 나타낸다. */
enum class QuizStep {
    /** 문제 풀이 전 세트 소개 단계다. */
    Intro,

    /** 객관식 문제 풀이 단계다. */
    MultipleChoice,

    /** 서술형 문제 풀이 단계다. */
    Essay,

    /** 세트의 모든 문제를 완료한 단계다. */
    Completed,
}

/**
 * 문제 풀이 화면의 단일 상태다.
 *
 * @property setInfo 시작 화면에 표시할 세트 정보
 * @property multipleChoiceQuestion 객관식 샘플 문제
 * @property essayQuestion 서술형 샘플 문제
 * @property step 현재 표시할 문제 풀이 단계
 * @property selectedAnswerId 채점 전에 선택한 답안 식별자
 * @property isMultipleChoiceSubmitted 객관식 정답 확인을 완료했는지 여부
 * @property expandedAnswerIds 채점 후 내용을 펼쳐 표시할 답안 식별자 집합
 * @property essayAnswer 사용자가 작성 중인 서술형 답안
 * @property isEssaySubmitted 서술형 답안 확인을 완료했는지 여부
 * @property bookmarkedQuestionNumbers 저장한 문제 번호 집합
 */
data class QuizUiState(
    val setInfo: QuizSetInfo = sampleQuizSetInfo,
    val multipleChoiceQuestion: QuizQuestion = sampleQuizQuestion,
    val essayQuestion: EssayQuestion = sampleEssayQuestion,
    val step: QuizStep = QuizStep.Intro,
    val selectedAnswerId: String? = null,
    val isMultipleChoiceSubmitted: Boolean = false,
    val expandedAnswerIds: Set<String> = emptySet(),
    val essayAnswer: String = "",
    val isEssaySubmitted: Boolean = false,
    val bookmarkedQuestionNumbers: Set<Int> = emptySet(),
)

/** 문제 풀이 화면에서 발생하는 사용자 의도다. */
sealed interface QuizIntent {
    /** 세트 문제 풀이를 시작한다. */
    data object Start : QuizIntent

    /** 이전 화면으로 이동한다. */
    data object BackClick : QuizIntent

    /** 현재 선택한 답안을 채점한다. */
    data object Submit : QuizIntent

    /** 채점 결과에서 다음 단계로 이동한다. */
    data object Next : QuizIntent

    /** 현재 문제의 임시 저장 상태를 전환한다. */
    data object BookmarkClick : QuizIntent

    /** 문제 출처 URL을 연다. */
    data object OpenSource : QuizIntent

    /**
     * 서술형 답안 입력을 갱신한다.
     *
     * @property answer 최대 글자 수 적용 전 사용자가 입력한 값
     */
    data class EssayAnswerChange(
        val answer: String,
    ) : QuizIntent

    /**
     * 답안을 선택하거나 채점 후 펼침 상태를 전환한다.
     *
     * @property answerId 대상 답안 식별자
     */
    data class AnswerClick(
        val answerId: String,
    ) : QuizIntent
}

/** 문제 풀이 화면에서 한 번만 처리할 이벤트다. */
sealed interface QuizSideEffect {
    /** 이전 화면으로 이동한다. */
    data object NavigateBack : QuizSideEffect

    /**
     * 외부 URL을 연다.
     *
     * @property url 시스템 URL 처리기로 전달할 주소
     */
    data class OpenUrl(
        val url: String,
    ) : QuizSideEffect
}

/** API 연동 전 화면 동작을 확인하는 Android 입문 세트다. */
private val sampleQuizSetInfo =
    QuizSetInfo(
        label = "Set 1",
        title = "Android 앱 진입점 확인하기",
        description = "Android 앱이 시작되고 Compose UI가 화면에 표시되는 기본 흐름을 실제 코드와 함께 확인하는 학습 세트",
    )

/** 현재 저장소의 Android 진입점을 출처로 사용하는 쉬운 샘플 문제다. */
private val sampleQuizQuestion =
    QuizQuestion(
        number = 1,
        text = "MainActivity에서 Compose UI를 화면에 표시하기 위해 호출하는 함수는 무엇일까요?",
        answers =
            listOf(
                QuizAnswer("set-content", "A", "setContent"),
                QuizAnswer("set-state", "B", "setState"),
                QuizAnswer("set-view", "C", "setView"),
                QuizAnswer("render", "D", "render"),
            ),
        correctAnswerId = "set-content",
        explanation = "ComponentActivity의 setContent 블록이 Compose UI 트리를 만들며, 이 프로젝트는 그 안에서 App 컴포저블을 호출합니다.",
        source =
            QuizSource(
                description = "MainActivity.onCreate()에서 setContent { App() }을 호출해 공유 Compose UI를 화면에 설정합니다.",
                label = "Git-It-KMP · MainActivity.kt:L12–L18",
                url =
                    "https://github.com/Nexters/Git-It-KMP/blob/main/androidApp/src/main/kotlin/com/nexters/hytime/gitit/MainActivity.kt#L12-L18",
            ),
    )

/** 공유 UI가 Android와 Desktop에서 재사용되는 구조를 설명하는 서술형 샘플 문제다. */
private val sampleEssayQuestion =
    EssayQuestion(
        number = 2,
        text = "Git-It-KMP가 Android와 Desktop에서 같은 Compose UI를 사용할 수 있는 구조를 설명해 보세요.",
        modelAnswer =
            "공유 UI와 화면 로직은 shared 모듈의 commonMain에 두고, Android와 Desktop 앱은 각각의 진입점에서 같은 App 컴포저블을 호출합니다. 플랫폼 API가 필요한 부분만 플랫폼별 소스셋으로 분리합니다.",
        source =
            QuizSource(
                description = "Android와 Desktop 진입점은 shared 모듈의 공통 App 컴포저블을 호출해 같은 UI를 표시합니다.",
                label = "Git-It-KMP · App.kt",
                url = "https://github.com/Nexters/Git-It-KMP/blob/main/shared/src/commonMain/kotlin/com/nexters/hytime/gitit/App.kt",
            ),
    )

/** 서술형 답안의 최대 글자 수다. */
const val ESSAY_ANSWER_MAX_LENGTH = 300

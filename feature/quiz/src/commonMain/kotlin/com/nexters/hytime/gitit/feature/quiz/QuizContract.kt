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
 * 로컬 샘플 객관식 문제다.
 *
 * @property number 세트 안에서 표시할 문제 순서
 * @property text 질문 본문
 * @property answers 사용자가 선택할 답안 목록
 * @property correctAnswerId 정답으로 판정할 답안 식별자
 * @property explanation 채점 후 표시할 해설
 * @property sourceDescription 출처 바텀시트에 표시할 코드 설명
 * @property sourceLabel 출처 링크 카드에 표시할 파일과 라인
 * @property sourceUrl 원본 코드의 GitHub 라인 링크
 */
data class QuizQuestion(
    val number: Int,
    val text: String,
    val answers: List<QuizAnswer>,
    val correctAnswerId: String,
    val explanation: String,
    val sourceDescription: String,
    val sourceLabel: String,
    val sourceUrl: String,
)

/**
 * 문제 풀이 화면의 단일 상태다.
 *
 * @property setInfo 시작 화면에 표시할 세트 정보
 * @property question 현재 풀이할 로컬 샘플 문제
 * @property isStarted 시작하기 버튼을 눌러 문제 화면에 진입했는지 여부
 * @property selectedAnswerId 채점 전에 선택한 답안 식별자
 * @property isSubmitted 정답 확인을 완료했는지 여부
 * @property expandedAnswerIds 채점 후 내용을 펼쳐 표시할 답안 식별자 집합
 * @property isBookmarked 현재 문제의 임시 저장 상태
 */
data class QuizUiState(
    val setInfo: QuizSetInfo = sampleQuizSetInfo,
    val question: QuizQuestion = sampleQuizQuestion,
    val isStarted: Boolean = false,
    val selectedAnswerId: String? = null,
    val isSubmitted: Boolean = false,
    val expandedAnswerIds: Set<String> = emptySet(),
    val isBookmarked: Boolean = false,
)

/** 문제 풀이 화면에서 발생하는 사용자 의도다. */
sealed interface QuizIntent {
    /** 세트 문제 풀이를 시작한다. */
    data object Start : QuizIntent

    /** 이전 화면으로 이동한다. */
    data object BackClick : QuizIntent

    /** 현재 선택한 답안을 채점한다. */
    data object Submit : QuizIntent

    /** 현재 문제의 임시 저장 상태를 전환한다. */
    data object BookmarkClick : QuizIntent

    /** 문제 출처 URL을 연다. */
    data object OpenSource : QuizIntent

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
        sourceDescription = "MainActivity.onCreate()에서 setContent { App() }을 호출해 공유 Compose UI를 화면에 설정합니다.",
        sourceLabel = "Git-It-KMP · MainActivity.kt:L12–L18",
        sourceUrl =
            "https://github.com/Nexters/Git-It-KMP/blob/main/androidApp/src/main/kotlin/com/nexters/hytime/gitit/MainActivity.kt#L12-L18",
    )

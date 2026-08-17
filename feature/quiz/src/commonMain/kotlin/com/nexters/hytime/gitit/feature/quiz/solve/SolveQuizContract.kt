package com.nexters.hytime.gitit.feature.quiz.solve

/**
 * 문제 풀이 세트 소개 정보다.
 *
 * @property label 세트 순서를 나타내는 짧은 이름
 * @property title 세트에서 학습할 주제
 * @property description 세트의 학습 범위 설명
 */
data class QuizSetInfo(
    val label: String = "",
    val title: String = "",
    val description: String = "",
)

/**
 * 객관식 답안 하나다.
 *
 * @property id 선택과 채점에 사용하는 식별자. 선택지 순서를 담은 0부터 시작하는 번호다
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
    val description: String = "",
    val label: String = "",
    val url: String = "",
)

/**
 * 객관식 문제다.
 *
 * @property id 답 제출에 사용하는 문제 식별자
 * @property number 세트 안에서 표시할 문제 순서
 * @property text 질문 본문
 * @property answers 사용자가 선택할 답안 목록
 * @property correctAnswerId 정답 답안 식별자. 서버 채점 전에는 null
 * @property explanation 채점 후 표시할 해설. 서버 채점 전에는 빈 문자열
 * @property source 문제와 연결된 원본 코드 정보
 */
data class QuizQuestion(
    val id: String = "",
    val number: Int = 0,
    val text: String = "",
    val answers: List<QuizAnswer> = emptyList(),
    val correctAnswerId: String? = null,
    val explanation: String = "",
    val source: QuizSource = QuizSource(),
)

/**
 * 서술형 문제다.
 *
 * @property id 답 제출에 사용하는 문제 식별자
 * @property number 세트 안에서 표시할 문제 순서
 * @property text 질문 본문
 * @property modelAnswer 채점 후 비교할 모범 답안. 서버 채점 전에는 빈 문자열
 * @property source 문제와 연결된 원본 코드 정보
 */
data class EssayQuestion(
    val id: String = "",
    val number: Int = 0,
    val text: String = "",
    val modelAnswer: String = "",
    val source: QuizSource = QuizSource(),
)

/**
 * 학습 세트에 담긴 문제 하나를 형식에 따라 담는다.
 */
sealed interface SolveQuizQuestionItem {
    /** 답 제출에 사용하는 문제 식별자다. */
    val questionId: String

    /** 세트 안에서 표시할 문제 순서다. */
    val number: Int

    /**
     * 객관식 문제 항목이다.
     *
     * @property question 화면에 표시할 객관식 문제
     */
    data class MultipleChoice(
        val question: QuizQuestion,
    ) : SolveQuizQuestionItem {
        override val questionId: String get() = question.id
        override val number: Int get() = question.number
    }

    /**
     * 서술형 문제 항목이다.
     *
     * @property question 화면에 표시할 서술형 문제
     */
    data class Essay(
        val question: EssayQuestion,
    ) : SolveQuizQuestionItem {
        override val questionId: String get() = question.id
        override val number: Int get() = question.number
    }
}

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
 * 세트의 문제 목록을 순서대로 진행하며, 현재 문제는 [currentIndex]가 가리킨다.
 *
 * @property setInfo 시작 화면에 표시할 세트 정보
 * @property questions 세트에 담긴 문제 목록. 형식에 따라 객관식·서술형 화면이 표시된다
 * @property currentIndex 현재 풀고 있는 문제의 위치
 * @property step 현재 표시할 문제 풀이 단계
 * @property selectedAnswerId 채점 전에 선택한 답안 식별자
 * @property isMultipleChoiceSubmitted 객관식 정답 확인을 완료했는지 여부
 * @property expandedAnswerIds 채점 후 내용을 펼쳐 표시할 답안 식별자 집합
 * @property essayAnswer 사용자가 작성 중인 서술형 답안
 * @property isEssaySubmitted 서술형 답안 확인을 완료했는지 여부
 * @property bookmarkedQuestionNumbers 저장한 문제 번호 집합
 */
data class SolveQuizUiState(
    val setInfo: QuizSetInfo = QuizSetInfo(),
    val questions: List<SolveQuizQuestionItem> = emptyList(),
    val currentIndex: Int = 0,
    val step: QuizStep = QuizStep.Intro,
    val selectedAnswerId: String? = null,
    val isMultipleChoiceSubmitted: Boolean = false,
    val expandedAnswerIds: Set<String> = emptySet(),
    val essayAnswer: String = "",
    val isEssaySubmitted: Boolean = false,
    val bookmarkedQuestionNumbers: Set<Int> = emptySet(),
) {
    /** 현재 문제가 객관식일 때 화면에 표시할 문제다. 아니면 빈 문제를 돌려준다. */
    val multipleChoiceQuestion: QuizQuestion
        get() = (questions.getOrNull(currentIndex) as? SolveQuizQuestionItem.MultipleChoice)?.question ?: QuizQuestion()

    /** 현재 문제가 서술형일 때 화면에 표시할 문제다. 아니면 빈 문제를 돌려준다. */
    val essayQuestion: EssayQuestion
        get() = (questions.getOrNull(currentIndex) as? SolveQuizQuestionItem.Essay)?.question ?: EssayQuestion()
}

/** 문제 풀이 화면에서 발생하는 사용자 의도다. */
sealed interface SolveQuizIntent {
    /** 세트 문제 풀이를 시작한다. */
    data object Start : SolveQuizIntent

    /** 이전 화면으로 이동한다. */
    data object BackClick : SolveQuizIntent

    /** 현재 선택한 답안을 채점한다. */
    data object Submit : SolveQuizIntent

    /** 채점 결과에서 다음 단계로 이동한다. */
    data object Next : SolveQuizIntent

    /** 현재 문제의 임시 저장 상태를 전환한다. */
    data object BookmarkClick : SolveQuizIntent

    /** 문제 출처 URL을 연다. */
    data object OpenSource : SolveQuizIntent

    /**
     * 서술형 답안 입력을 갱신한다.
     *
     * @property answer 최대 글자 수 적용 전 사용자가 입력한 값
     */
    data class EssayAnswerChange(
        val answer: String,
    ) : SolveQuizIntent

    /**
     * 답안을 선택하거나 채점 후 펼침 상태를 전환한다.
     *
     * @property answerId 대상 답안 식별자
     */
    data class AnswerClick(
        val answerId: String,
    ) : SolveQuizIntent
}

/** 문제 풀이 화면에서 한 번만 처리할 이벤트다. */
sealed interface SolveQuizSideEffect {
    /** 이전 화면으로 이동한다. */
    data object NavigateBack : SolveQuizSideEffect

    /**
     * 외부 URL을 연다.
     *
     * @property url 시스템 URL 처리기로 전달할 주소
     */
    data class OpenUrl(
        val url: String,
    ) : SolveQuizSideEffect
}

/** 서술형 답안의 최대 글자 수다. */
const val ESSAY_ANSWER_MAX_LENGTH = 300

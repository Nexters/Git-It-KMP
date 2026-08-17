package com.nexters.hytime.gitit.feature.quiz.solve

import com.nexters.hytime.gitit.domain.model.LearningSet
import com.nexters.hytime.gitit.domain.model.Question
import com.nexters.hytime.gitit.domain.model.QuestionFormat
import com.nexters.hytime.gitit.domain.model.QuestionSource

/**
 * 학습 세트 도메인 모델을 문제 풀이 소개 정보로 변환한다.
 *
 * @param label 프로젝트 상세가 알려준 세트 라벨 (예: `"Set 1"`)
 * @return 시작 화면에 표시할 세트 정보
 */
internal fun LearningSet.toSetInfo(label: String): QuizSetInfo =
    QuizSetInfo(
        label = label,
        title = title,
        description = description,
    )

/**
 * 학습 세트의 문제들을 화면 문제 목록으로 변환한다.
 *
 * 클라이언트가 모르는 형식의 문제는 표시할 화면이 없으므로 걸러낸다.
 *
 * @param repositoryName 출처 라벨에 표시할 저장소 이름
 * @return 형식이 확인된 문제 목록. 번호는 걸러낸 뒤의 순서로 다시 매긴다
 */
internal fun LearningSet.toQuestionItems(repositoryName: String): List<SolveQuizQuestionItem> {
    var number = 0
    return questions.mapNotNull { question ->
        when (question.format) {
            QuestionFormat.MULTIPLE_CHOICE -> {
                number += 1
                SolveQuizQuestionItem.MultipleChoice(question.toQuizQuestion(number, repositoryName))
            }

            QuestionFormat.ESSAY -> {
                number += 1
                SolveQuizQuestionItem.Essay(question.toEssayQuestion(number, repositoryName))
            }

            null -> null
        }
    }
}

/**
 * 도메인 문제를 객관식 화면 모델로 변환한다.
 *
 * 정답과 해설은 제출 응답에만 담겨 있으므로 여기서는 비워 둔다.
 *
 * @param number 세트 안에서 표시할 문제 순서
 * @param repositoryName 출처 라벨에 표시할 저장소 이름
 * @return 객관식 화면 모델
 */
private fun Question.toQuizQuestion(
    number: Int,
    repositoryName: String,
): QuizQuestion =
    QuizQuestion(
        id = questionId,
        number = number,
        text = text,
        answers =
            choices.mapIndexed { index, choice ->
                QuizAnswer(
                    id = index.toString(),
                    label = ('A' + index).toString(),
                    text = choice,
                )
            },
        source = sources.firstOrNull().toQuizSource(repositoryName),
    )

/**
 * 도메인 문제를 서술형 화면 모델로 변환한다.
 *
 * 모범 답안은 제출 응답의 채점 기준에서 오므로 여기서는 비워 둔다.
 *
 * @param number 세트 안에서 표시할 문제 순서
 * @param repositoryName 출처 라벨에 표시할 저장소 이름
 * @return 서술형 화면 모델
 */
private fun Question.toEssayQuestion(
    number: Int,
    repositoryName: String,
): EssayQuestion =
    EssayQuestion(
        id = questionId,
        number = number,
        text = text,
        source = sources.firstOrNull().toQuizSource(repositoryName),
    )

/**
 * 문제가 인용한 코드 위치를 출처 표시 모델로 변환한다.
 *
 * @param repositoryName 출처 라벨에 표시할 저장소 이름
 * @return 출처 정보. 인용한 코드가 없으면 빈 모델
 */
private fun QuestionSource?.toQuizSource(repositoryName: String): QuizSource {
    if (this == null) return QuizSource()
    return QuizSource(
        description = summary.orEmpty(),
        label = "$repositoryName · ${file.substringAfterLast('/')}:L$startLine–L$endLine",
        url = url,
    )
}

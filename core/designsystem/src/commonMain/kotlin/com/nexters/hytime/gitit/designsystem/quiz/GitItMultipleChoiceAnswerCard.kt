package com.nexters.hytime.gitit.designsystem.quiz

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme

/** 객관식 답안 카드의 표시 상태다. */
enum class GitItMultipleChoiceAnswerState {
    /** 선택 결과를 표시하지 않는 펼친 상태다. */
    Default,

    /** 답안 내용을 숨긴 접힌 상태다. */
    Folded,

    /** 선택한 답안이 오답임을 표시하는 상태다. */
    Incorrect,

    /** 선택한 답안이 정답임을 표시하는 상태다. */
    Correct,
}

/** Figma 오답 카드의 배경색이다. */
private val IncorrectBackground = Color(0xFFFF5656)

/** Figma 정답 카드의 배경색이다. */
private val CorrectBackground = Color(0xFF3E85FF)

/** 상태에 대응하는 카드 배경색이다. */
internal val GitItMultipleChoiceAnswerState.backgroundColor: Color
    get() =
        when (this) {
            GitItMultipleChoiceAnswerState.Default,
            GitItMultipleChoiceAnswerState.Folded,
            -> GitItTheme.colors.grey600

            GitItMultipleChoiceAnswerState.Incorrect -> IncorrectBackground
            GitItMultipleChoiceAnswerState.Correct -> CorrectBackground
        }

/** 상태에 대응하는 답안 기호 색상이다. */
internal val GitItMultipleChoiceAnswerState.labelColor: Color
    get() =
        when (this) {
            GitItMultipleChoiceAnswerState.Default,
            GitItMultipleChoiceAnswerState.Folded,
            -> GitItTheme.colors.blue200

            GitItMultipleChoiceAnswerState.Incorrect,
            GitItMultipleChoiceAnswerState.Correct,
            -> GitItTheme.colors.grey100
        }

/** 답안 내용 표시 여부다. */
internal val GitItMultipleChoiceAnswerState.showsAnswer: Boolean
    get() = this != GitItMultipleChoiceAnswerState.Folded

/** Chevron 방향이다. null이면 아이콘을 표시하지 않는다. */
internal val GitItMultipleChoiceAnswerState.chevronExpanded: Boolean?
    get() =
        when (this) {
            GitItMultipleChoiceAnswerState.Default -> null
            GitItMultipleChoiceAnswerState.Folded -> false
            GitItMultipleChoiceAnswerState.Incorrect,
            GitItMultipleChoiceAnswerState.Correct,
            -> true
        }

/**
 * Figma의 객관식 문항 답안 카드를 렌더링한다.
 *
 * @param label 답안을 구분하는 짧은 기호. 예: A, B
 * @param answer 펼친 상태에서 표시할 답안 내용
 * @param modifier 카드의 외부 크기와 배치를 지정할 수식자
 * @param state 답안의 펼침·정오답 표시 상태
 * @param onClick 카드를 눌렀을 때 실행할 동작. null이면 클릭 동작을 적용하지 않는다
 */
@Composable
fun GitItMultipleChoiceAnswerCard(
    label: String,
    answer: String,
    modifier: Modifier = Modifier,
    state: GitItMultipleChoiceAnswerState = GitItMultipleChoiceAnswerState.Default,
    onClick: (() -> Unit)? = null,
) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(12.dp))
                .background(state.backgroundColor)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(role = Role.Button, onClick = onClick)
                    } else {
                        Modifier
                    },
                ).padding(
                    start = 18.dp,
                    top = 14.dp,
                    end = 18.dp,
                    bottom = 18.dp,
                ),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                color = state.labelColor,
                style = GitItTheme.typography.subtitle2,
            )
            state.chevronExpanded?.let { expanded ->
                GitItMultipleChoiceChevron(
                    expanded = expanded,
                    color = if (expanded) GitItTheme.colors.grey100 else GitItTheme.colors.blue100,
                )
            }
        }

        if (state.showsAnswer) {
            Text(
                text = answer,
                modifier = Modifier.fillMaxWidth(),
                color = GitItTheme.colors.grey100,
                style = GitItTheme.typography.subtitle3,
            )
        }
    }
}

/**
 * 답안 카드의 펼침 상태를 나타내는 chevron 아이콘을 그린다.
 *
 * @param expanded 위쪽 방향 표시 여부
 * @param color 아이콘 선 색상
 * @param modifier 아이콘의 외부 배치와 추가 수식자
 */
@Composable
private fun GitItMultipleChoiceChevron(
    expanded: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(12.dp)) {
        val top = if (expanded) size.height * 0.6f else size.height * 0.4f
        val bottom = if (expanded) size.height * 0.4f else size.height * 0.6f
        val path =
            Path().apply {
                moveTo(size.width * 0.25f, top)
                lineTo(size.width * 0.5f, bottom)
                lineTo(size.width * 0.75f, top)
            }

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

@Preview(name = "객관식 문항")
@Composable
private fun GitItMultipleChoiceAnswerCardPreview() {
    GitItTheme {
        Row(
            modifier = Modifier.background(Color(0xFFF5F5F5)).padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(27.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                GitItMultipleChoiceAnswerCard(
                    label = "A",
                    answer = "Activity가 사용자와 상호작용을 시작하기 직전에 호출되는 메서드입니다.",
                    modifier = Modifier.width(320.dp),
                    state = GitItMultipleChoiceAnswerState.Default
                )
                GitItMultipleChoiceAnswerCard(
                    label = "A",
                    answer = "",
                    modifier = Modifier.width(320.dp),
                    state = GitItMultipleChoiceAnswerState.Folded,
                )
                GitItMultipleChoiceAnswerCard(
                    label = "A",
                    answer = "Activity가 사용자와 상호작용을 시작하기 직전에 호출되는 메서드입니다.",
                    modifier = Modifier.width(320.dp),
                    state = GitItMultipleChoiceAnswerState.Incorrect,
                )
                GitItMultipleChoiceAnswerCard(
                    label = "A",
                    answer = "Activity가 사용자와 상호작용을 시작하기 직전에 호출되는 메서드입니다.",
                    modifier = Modifier.width(320.dp),
                    state = GitItMultipleChoiceAnswerState.Correct,
                )
            }
        }
    }
}

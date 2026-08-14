package com.nexters.hytime.gitit.designsystem.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import git_it_kmp.core.designsystem.generated.resources.Res
import git_it_kmp.core.designsystem.generated.resources.ic_chevron_right
import org.jetbrains.compose.resources.painterResource

/** 객관식 답안 카드의 표시 상태다. */
enum class GitItMultipleChoiceAnswerState {
    /** 선택 결과를 표시하지 않는 펼친 상태다. */
    Default,

    /** 채점 전 사용자가 선택한 답안을 테두리로 강조하는 상태다. */
    Selected,

    /** 답안 내용을 숨긴 접힌 상태다. */
    Folded,

    /** 선택 결과 없이 답안과 위쪽 chevron을 표시하는 펼친 토글 상태다. */
    Expanded,

    /** 선택한 답안이 오답임을 표시하는 상태다. */
    Incorrect,

    /** 오답 배경을 유지하면서 답안 내용을 숨긴 접힌 상태다. */
    IncorrectFolded,

    /** 선택한 답안이 정답임을 표시하는 상태다. */
    Correct,

    /** 정답 배경을 유지하면서 답안 내용을 숨긴 접힌 상태다. */
    CorrectFolded,
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
            GitItMultipleChoiceAnswerState.Selected,
            GitItMultipleChoiceAnswerState.Folded,
            GitItMultipleChoiceAnswerState.Expanded,
            -> GitItTheme.colors.grey600

            GitItMultipleChoiceAnswerState.Incorrect,
            GitItMultipleChoiceAnswerState.IncorrectFolded,
            -> IncorrectBackground

            GitItMultipleChoiceAnswerState.Correct,
            GitItMultipleChoiceAnswerState.CorrectFolded,
            -> CorrectBackground
        }

/** 상태에 대응하는 답안 기호 색상이다. */
internal val GitItMultipleChoiceAnswerState.labelColor: Color
    get() =
        when (this) {
            GitItMultipleChoiceAnswerState.Default,
            GitItMultipleChoiceAnswerState.Selected,
            GitItMultipleChoiceAnswerState.Folded,
            GitItMultipleChoiceAnswerState.Expanded,
            -> GitItTheme.colors.blue200

            GitItMultipleChoiceAnswerState.Incorrect,
            GitItMultipleChoiceAnswerState.IncorrectFolded,
            GitItMultipleChoiceAnswerState.Correct,
            GitItMultipleChoiceAnswerState.CorrectFolded,
            -> GitItTheme.colors.grey100
        }

/** 답안 내용 표시 여부다. */
internal val GitItMultipleChoiceAnswerState.showsAnswer: Boolean
    get() =
        this != GitItMultipleChoiceAnswerState.Folded &&
            this != GitItMultipleChoiceAnswerState.IncorrectFolded &&
            this != GitItMultipleChoiceAnswerState.CorrectFolded

/** 접힘·펼침 애니메이션을 사용하는 상태인지 여부다. */
internal val GitItMultipleChoiceAnswerState.isToggleState: Boolean
    get() = this != GitItMultipleChoiceAnswerState.Default && this != GitItMultipleChoiceAnswerState.Selected

/** Chevron 방향이다. null이면 아이콘을 표시하지 않는다. */
internal val GitItMultipleChoiceAnswerState.chevronExpanded: Boolean?
    get() =
        when (this) {
            GitItMultipleChoiceAnswerState.Default,
            GitItMultipleChoiceAnswerState.Selected,
            -> null
            GitItMultipleChoiceAnswerState.Folded,
            GitItMultipleChoiceAnswerState.IncorrectFolded,
            GitItMultipleChoiceAnswerState.CorrectFolded,
            -> false

            GitItMultipleChoiceAnswerState.Expanded,
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
 * @param state 답안의 기본·토글·정오답 표시 상태
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
                    if (state == GitItMultipleChoiceAnswerState.Selected) {
                        Modifier.border(1.dp, GitItTheme.colors.blue100, RoundedCornerShape(12.dp))
                    } else {
                        Modifier
                    },
                ).then(
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

        if (state.isToggleState) {
            AnimatedVisibility(
                visible = state.showsAnswer,
                enter =
                    expandVertically(
                        animationSpec = tween(ANSWER_CARD_ANIMATION_DURATION, easing = FastOutSlowInEasing),
                        expandFrom = Alignment.Top,
                    ) + fadeIn(animationSpec = tween(ANSWER_CARD_ANIMATION_DURATION)),
                exit =
                    shrinkVertically(
                        animationSpec = tween(ANSWER_CARD_ANIMATION_DURATION, easing = FastOutSlowInEasing),
                        shrinkTowards = Alignment.Top,
                    ) + fadeOut(animationSpec = tween(ANSWER_CARD_ANIMATION_DURATION)),
            ) {
                Text(
                    text = answer,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    color = GitItTheme.colors.grey100,
                    style = GitItTheme.typography.subtitle3,
                )
            }
        } else if (state.showsAnswer) {
            Text(
                text = answer,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
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
    val rotation by
        animateFloatAsState(
            targetValue = if (expanded) 180f else 0f,
            animationSpec = tween(durationMillis = ANSWER_CARD_ANIMATION_DURATION, easing = FastOutSlowInEasing),
            label = "answerCardChevron",
        )

    Icon(
        painter = painterResource(Res.drawable.ic_chevron_right),
        contentDescription = null,
        modifier = modifier.size(width = 5.6.dp, height = 9.6.dp).rotate(rotation + 90f),
        tint = color,
    )
}

/** 객관식 답안 카드 상태 전환 시간이다. */
private const val ANSWER_CARD_ANIMATION_DURATION = 300

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
                    state = GitItMultipleChoiceAnswerState.Default,
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
                    state = GitItMultipleChoiceAnswerState.Expanded,
                )
                GitItMultipleChoiceAnswerCard(
                    label = "A",
                    answer = "Activity가 사용자와 상호작용을 시작하기 직전에 호출되는 메서드입니다.",
                    modifier = Modifier.width(320.dp),
                    state = GitItMultipleChoiceAnswerState.Incorrect,
                )
                GitItMultipleChoiceAnswerCard(
                    label = "A",
                    answer = "",
                    modifier = Modifier.width(320.dp),
                    state = GitItMultipleChoiceAnswerState.IncorrectFolded,
                )
                GitItMultipleChoiceAnswerCard(
                    label = "A",
                    answer = "Activity가 사용자와 상호작용을 시작하기 직전에 호출되는 메서드입니다.",
                    modifier = Modifier.width(320.dp),
                    state = GitItMultipleChoiceAnswerState.Correct,
                )
                GitItMultipleChoiceAnswerCard(
                    label = "A",
                    answer = "",
                    modifier = Modifier.width(320.dp),
                    state = GitItMultipleChoiceAnswerState.CorrectFolded,
                )
            }
        }
    }
}

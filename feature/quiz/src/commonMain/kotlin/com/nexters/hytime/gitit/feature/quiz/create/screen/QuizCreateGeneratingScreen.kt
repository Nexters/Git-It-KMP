package com.nexters.hytime.gitit.feature.quiz.create.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.button.GitItButton
import com.nexters.hytime.gitit.designsystem.button.GitItButtonStyle
import com.nexters.hytime.gitit.feature.quiz.create.QuizGenerationStep
import com.nexters.hytime.gitit.feature.quiz.create.component.QuizCreateImagePlaceholder
import git_it_kmp.feature.quiz.generated.resources.Res
import git_it_kmp.feature.quiz.generated.resources.ic_quiz_generation_check
import git_it_kmp.feature.quiz.generated.resources.quiz_create_generating_description
import git_it_kmp.feature.quiz.generated.resources.quiz_create_generating_title
import git_it_kmp.feature.quiz.generated.resources.quiz_create_step_code_structure
import git_it_kmp.feature.quiz.generated.resources.quiz_create_step_learning_concepts
import git_it_kmp.feature.quiz.generated.resources.quiz_create_step_project_info
import git_it_kmp.feature.quiz.generated.resources.quiz_create_step_questions
import git_it_kmp.feature.quiz.generated.resources.quiz_create_step_validation
import git_it_kmp.feature.quiz.generated.resources.quiz_create_wait_at_home
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * 서버 진행률과 독립적으로 재생되는 문제 생성 단계와 홈 이동 액션을 표시한다.
 *
 * @param step 현재 생성 처리 단계
 * @param onWaitAtHomeClick 홈에서 기다리기 콜백
 */
@Composable
internal fun QuizCreateGeneratingScreen(
    step: QuizGenerationStep,
    onWaitAtHomeClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(0.56f to GitItTheme.colors.grey700, 1f to GitItTheme.colors.blue400),
                    ),
                ).statusBarsPadding()
                .navigationBarsPadding(),
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center).padding(horizontal = 64.dp).padding(bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            QuizCreateImagePlaceholder(
                modifier = Modifier.size(128.dp),
                cornerRadius = 24.dp,
            )
            Spacer(Modifier.height(44.dp))
            Text(
                text = stringResource(Res.string.quiz_create_generating_title),
                color = GitItTheme.colors.grey100,
                style = GitItTheme.typography.title1,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.quiz_create_generating_description),
                color = GitItTheme.colors.grey400,
                style = GitItTheme.typography.body2,
            )
            Spacer(Modifier.height(48.dp))
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(19.dp)) {
                QuizGenerationStep.entries.forEach { item ->
                    GenerationStepRow(
                        label = stringResource(item.titleResource()),
                        isCompleted = item.ordinal < step.ordinal,
                        isCurrent = item == step,
                    )
                }
            }
        }
        GitItButton(
            text = stringResource(Res.string.quiz_create_wait_at_home),
            onClick = onWaitAtHomeClick,
            style = GitItButtonStyle.PrimaryText,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
        )
    }
}

/**
 * 생성 과정 한 단계의 완료, 현재, 대기 상태를 표시한다.
 *
 * @param label 사용자에게 표시할 생성 단계 이름
 * @param isCompleted 현재 단계보다 앞서 완료되었는지 여부
 * @param isCurrent 지금 처리 중인 단계인지 여부
 */
@Composable
private fun GenerationStepRow(
    label: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
) {
    val color = if (isCompleted || isCurrent) GitItTheme.colors.grey100 else GitItTheme.colors.grey400
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isCurrent) {
            QuizGenerationLoadingIndicator()
        } else if (isCompleted) {
            Image(
                painter = painterResource(Res.drawable.ic_quiz_generation_check),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
        } else {
            Canvas(modifier = Modifier.size(24.dp)) {
                drawCircle(
                    color = color,
                    style = Stroke(width = 4.dp.toPx()),
                )
            }
        }
        Text(text = label, color = color, style = GitItTheme.typography.body2)
    }
}

/** Figma의 어두운 꼬리가 회전하는 24dp 생성 로딩 링을 표시한다. */
@Composable
private fun QuizGenerationLoadingIndicator() {
    val transition = rememberInfiniteTransition(label = "quiz-generation-loading")
    val rotation by
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = 900, easing = LinearEasing),
                ),
            label = "quiz-generation-loading-rotation",
        )
    val color = GitItTheme.colors.blue100

    Canvas(modifier = Modifier.size(24.dp)) {
        rotate(rotation) {
            drawCircle(
                brush =
                    Brush.sweepGradient(
                        colorStops =
                            arrayOf(
                                0f to color,
                                0.68f to color,
                                0.82f to color.copy(alpha = 0.28f),
                                0.94f to color,
                                1f to color,
                            ),
                    ),
                style = Stroke(width = 4.dp.toPx()),
            )
        }
    }
}

/**
 * 생성 진행 목록에 표시할 단계명 리소스를 반환한다.
 *
 * @return 현재 생성 단계에 대응하는 문자열 리소스
 */
private fun QuizGenerationStep.titleResource(): StringResource =
    when (this) {
        QuizGenerationStep.ProjectInfo -> Res.string.quiz_create_step_project_info
        QuizGenerationStep.CodeStructure -> Res.string.quiz_create_step_code_structure
        QuizGenerationStep.LearningConcepts -> Res.string.quiz_create_step_learning_concepts
        QuizGenerationStep.Questions -> Res.string.quiz_create_step_questions
        QuizGenerationStep.Validation -> Res.string.quiz_create_step_validation
    }

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun QuizCreateGeneratingPreview() {
    GitItTheme {
        QuizCreateGeneratingScreen(
            step = QuizGenerationStep.LearningConcepts,
            onWaitAtHomeClick = {},
        )
    }
}

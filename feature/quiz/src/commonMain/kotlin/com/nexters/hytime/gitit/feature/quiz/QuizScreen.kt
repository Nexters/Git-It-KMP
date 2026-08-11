package com.nexters.hytime.gitit.feature.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.button.GitItButton
import com.nexters.hytime.gitit.designsystem.button.GitItButtonSize
import com.nexters.hytime.gitit.designsystem.button.GitItButtonStyle
import com.nexters.hytime.gitit.designsystem.navigation.GitItBookmarkIcon
import com.nexters.hytime.gitit.designsystem.quiz.GitItMultipleChoiceAnswerCard
import com.nexters.hytime.gitit.designsystem.quiz.GitItMultipleChoiceAnswerState
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBar
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBarType
import com.skydoves.cloudy.Sky
import com.skydoves.cloudy.cloudy
import com.skydoves.cloudy.rememberSky
import com.skydoves.cloudy.sky

/**
 * 세트 소개와 객관식 문제 풀이 화면을 현재 상태에 맞춰 표시한다.
 *
 * @param uiState 화면에 표시할 문제 풀이 상태
 * @param onIntent 사용자 입력을 ViewModel로 올리는 콜백
 * @param modifier 화면의 크기와 배치를 지정할 수식자
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    uiState: QuizUiState,
    onIntent: (QuizIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showSource by rememberSaveable { mutableStateOf(false) }

    if (uiState.isStarted) {
        QuizQuestionScreen(
            uiState = uiState,
            onIntent = onIntent,
            onSourceClick = { showSource = true },
            modifier = modifier,
        )
    } else {
        QuizIntroScreen(
            setInfo = uiState.setInfo,
            onBackClick = { onIntent(QuizIntent.BackClick) },
            onStartClick = { onIntent(QuizIntent.Start) },
            modifier = modifier,
        )
    }

    if (showSource) {
        QuizSourceSheet(
            question = uiState.question,
            onDismiss = { showSource = false },
            onOpenSource = {
                showSource = false
                onIntent(QuizIntent.OpenSource)
            },
        )
    }
}

/**
 * 문제 풀이 전 세트 주제와 설명을 표시한다.
 *
 * @param setInfo 소개할 세트 정보
 * @param onBackClick 이전 화면으로 이동하는 콜백
 * @param onStartClick 문제 풀이를 시작하는 콜백
 * @param modifier 화면의 크기와 배치를 지정할 수식자
 */
@Composable
private fun QuizIntroScreen(
    setInfo: QuizSetInfo,
    onBackClick: () -> Unit,
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops =
                            arrayOf(
                                0.55f to GitItTheme.colors.grey700,
                                1f to GitItTheme.colors.blue400,
                            ),
                    ),
                ),
    ) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            GitItTopBar(
                type = GitItTopBarType.Default,
                modifier = Modifier.padding(top = 8.dp),
                onBackClick = onBackClick,
            )
            Spacer(Modifier.weight(1f))
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = setInfo.label,
                    color = GitItTheme.colors.blue100,
                    style = GitItTheme.typography.subtitle3,
                )
                Text(
                    text = setInfo.title,
                    color = GitItTheme.colors.grey100,
                    style = GitItTheme.typography.subtitle1,
                )
                Text(
                    text = setInfo.description,
                    color = GitItTheme.colors.grey400,
                    style = GitItTheme.typography.body2,
                )
            }
            Spacer(Modifier.weight(1f))
            GitItButton(
                text = "시작하기",
                onClick = onStartClick,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            )
            Spacer(Modifier.navigationBarsPadding().height(24.dp))
        }
    }
}

/**
 * 고정 질문 영역 아래에서 답안과 해설을 스크롤할 수 있는 문제 화면을 표시한다.
 *
 * @param uiState 현재 문제와 선택·채점 상태
 * @param onIntent 사용자 입력을 전달하는 콜백
 * @param onSourceClick 출처 바텀시트를 여는 콜백
 * @param modifier 화면의 크기와 배치를 지정할 수식자
 */
@Composable
private fun QuizQuestionScreen(
    uiState: QuizUiState,
    onIntent: (QuizIntent) -> Unit,
    onSourceClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sky = if (LocalInspectionMode.current) null else rememberSky()

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(GitItTheme.colors.grey700),
    ) {
        QuizAnswerList(
            uiState = uiState,
            onIntent = onIntent,
            onSourceClick = onSourceClick,
            modifier = Modifier.fillMaxSize().captureSky(sky),
        )
        QuizQuestionHeader(
            question = uiState.question,
            onBackClick = { onIntent(QuizIntent.BackClick) },
            sky = sky,
            modifier = Modifier.align(Alignment.TopCenter),
        )
        QuizBottomBar(
            isBookmarked = uiState.isBookmarked,
            onBookmarkClick = { onIntent(QuizIntent.BookmarkClick) },
            onSubmitClick = { onIntent(QuizIntent.Submit) },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

/**
 * 답안 카드와 채점 결과 해설을 세로 목록으로 표시한다.
 *
 * @param uiState 답안 표시 상태를 계산할 화면 상태
 * @param onIntent 답안 클릭을 전달하는 콜백
 * @param onSourceClick 출처 바텀시트를 여는 콜백
 * @param modifier 목록의 크기와 배치를 지정할 수식자
 */
@Composable
private fun QuizAnswerList(
    uiState: QuizUiState,
    onIntent: (QuizIntent) -> Unit,
    onSourceClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 20.dp, top = 265.dp, end = 20.dp, bottom = 164.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(uiState.question.answers, key = { it.id }) { answer ->
            GitItMultipleChoiceAnswerCard(
                label = answer.label,
                answer = answer.text,
                modifier = Modifier.fillMaxWidth(),
                state = uiState.answerCardState(answer.id),
                onClick = { onIntent(QuizIntent.AnswerClick(answer.id)) },
            )
        }
        if (uiState.isSubmitted) {
            item { QuizExplanation(text = uiState.question.explanation) }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                GitItButton(
                    text = "출처 ›",
                    onClick = onSourceClick,
                    size = GitItButtonSize.Small,
                    style = GitItButtonStyle.Secondary,
                )
            }
        }
    }
}

/**
 * 스크롤 콘텐츠 위에 고정되는 뒤로가기와 질문 영역을 표시한다.
 *
 * @param question 화면 상단에 표시할 문제
 * @param onBackClick 이전 화면으로 이동하는 콜백
 * @param sky 스크롤 콘텐츠를 흐림 배경으로 읽을 Cloudy 상태
 * @param modifier 헤더의 크기와 배치를 지정할 수식자
 */
@Composable
private fun QuizQuestionHeader(
    question: QuizQuestion,
    onBackClick: () -> Unit,
    sky: Sky?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().statusBarsPadding()) {
        GitItTopBar(
            type = GitItTopBarType.Default,
            modifier = Modifier.padding(top = 8.dp),
            sky = sky,
            onBackClick = onBackClick,
        )
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(162.dp)
                    .questionBackdrop(sky)
                    .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "문제 ${question.number}",
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(GitItTheme.colors.blue400)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                color = GitItTheme.colors.blue100,
                style = GitItTheme.typography.body2,
            )
            Text(
                text = question.text,
                color = GitItTheme.colors.grey100,
                style = GitItTheme.typography.subtitle3,
            )
        }
    }
}

/**
 * 정답 확인 버튼 위에 고정되는 북마크와 하단 그라디언트를 표시한다.
 *
 * @param isBookmarked 현재 문제의 저장 상태
 * @param onBookmarkClick 저장 상태를 전환하는 콜백
 * @param onSubmitClick 현재 선택 답안을 채점하는 콜백
 * @param modifier 하단 바의 크기와 배치를 지정할 수식자
 */
@Composable
private fun QuizBottomBar(
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, GitItTheme.colors.grey700),
                    ),
                ).navigationBarsPadding()
                .padding(start = 20.dp, top = 28.dp, end = 20.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GitItTheme.colors.grey500)
                    .clickable(role = Role.Button, onClick = onBookmarkClick)
                    .semantics {
                        contentDescription = if (isBookmarked) "문제 저장 해제" else "문제 저장"
                    },
            contentAlignment = Alignment.Center,
        ) {
            CompositionLocalProvider(
                LocalContentColor provides if (isBookmarked) GitItTheme.colors.blue100 else GitItTheme.colors.grey100,
            ) {
                GitItBookmarkIcon(filled = isBookmarked)
            }
        }
        GitItButton(
            text = "정답 확인",
            onClick = onSubmitClick,
            modifier = Modifier.weight(1f),
        )
    }
}

/**
 * 채점 결과 아래에 AI 해설을 강조 카드로 표시한다.
 *
 * @param text 현재 문제의 해설 본문
 */
@Composable
private fun QuizExplanation(text: String) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 18.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(GitItTheme.colors.blue500)
                .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "AI 해설",
            color = GitItTheme.colors.blue100,
            style = GitItTheme.typography.body2,
        )
        Text(
            text = text,
            color = GitItTheme.colors.grey100,
            style = GitItTheme.typography.body2,
        )
    }
}

/**
 * 문제의 코드 출처와 GitHub 이동 버튼을 바텀시트로 표시한다.
 *
 * @param question 출처 설명과 URL을 제공하는 문제
 * @param onDismiss 바텀시트를 닫는 콜백
 * @param onOpenSource GitHub 원본을 여는 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizSourceSheet(
    question: QuizQuestion,
    onDismiss: () -> Unit,
    onOpenSource: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = GitItTheme.colors.grey700,
        contentColor = GitItTheme.colors.grey100,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = "출처",
                color = GitItTheme.colors.grey100,
                style = GitItTheme.typography.subtitle2,
            )
            Text(
                text = question.sourceDescription,
                color = GitItTheme.colors.grey200,
                style = GitItTheme.typography.body2,
            )
            GitItButton(
                text = "GitHub에서 보기",
                onClick = onOpenSource,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * 현재 선택·채점 상태를 디자인 시스템 답안 카드 상태로 변환한다.
 *
 * @param answerId 상태를 계산할 답안 식별자
 * @return 선택 전·정답·오답·접힘 여부를 반영한 카드 상태
 */
internal fun QuizUiState.answerCardState(answerId: String): GitItMultipleChoiceAnswerState {
    if (!isSubmitted) {
        return if (selectedAnswerId == answerId) {
            GitItMultipleChoiceAnswerState.Selected
        } else {
            GitItMultipleChoiceAnswerState.Default
        }
    }

    val expanded = answerId in expandedAnswerIds
    return when {
        answerId == question.correctAnswerId && expanded -> GitItMultipleChoiceAnswerState.Correct
        answerId == question.correctAnswerId -> GitItMultipleChoiceAnswerState.CorrectFolded
        answerId == selectedAnswerId && expanded -> GitItMultipleChoiceAnswerState.Incorrect
        answerId == selectedAnswerId -> GitItMultipleChoiceAnswerState.IncorrectFolded
        expanded -> GitItMultipleChoiceAnswerState.Expanded
        else -> GitItMultipleChoiceAnswerState.Folded
    }
}

/**
 * 스크롤 목록을 Cloudy 캡처 대상으로 등록한다.
 *
 * @param sky 캡처 상태. Preview에서는 null
 * @return Cloudy 캡처가 조건부로 적용된 수식자
 */
@Composable
private fun Modifier.captureSky(sky: Sky?): Modifier = if (sky == null) this else sky(sky)

/**
 * 고정 질문 영역에 스크롤 콘텐츠의 Sky 블러를 적용한다.
 *
 * @param sky 흐림 배경을 읽을 캡처 상태. Preview에서는 null
 * @return 20px 블러 또는 정적 배경이 적용된 수식자
 */
@Composable
private fun Modifier.questionBackdrop(sky: Sky?): Modifier =
    if (sky == null) {
        background(GitItTheme.colors.grey700)
    } else {
        cloudy(
            sky = sky,
            radius = 20,
            tint = GitItTheme.colors.grey700.copy(alpha = 0.55f),
            shape = RectangleShape,
        )
    }

@Preview
@Composable
private fun QuizIntroScreenPreview() {
    GitItTheme {
        QuizScreen(uiState = QuizUiState(), onIntent = {})
    }
}

@Preview
@Composable
private fun QuizResultScreenPreview() {
    GitItTheme {
        QuizScreen(
            uiState =
                QuizUiState(
                    isStarted = true,
                    selectedAnswerId = "render",
                    isSubmitted = true,
                    expandedAnswerIds = setOf("render", "set-content"),
                ),
            onIntent = {},
        )
    }
}

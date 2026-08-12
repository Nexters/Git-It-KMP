package com.nexters.hytime.gitit.feature.quiz

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.button.GitItButton
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

    when (uiState.step) {
        QuizStep.Intro ->
            QuizIntroScreen(
                setInfo = uiState.setInfo,
                onBackClick = { onIntent(QuizIntent.BackClick) },
                onStartClick = { onIntent(QuizIntent.Start) },
                modifier = modifier,
            )
        QuizStep.MultipleChoice ->
            QuizQuestionScreen(
                uiState = uiState,
                onIntent = onIntent,
                onSourceClick = { showSource = true },
                modifier = modifier,
            )
        QuizStep.Essay ->
            QuizEssayScreen(
                uiState = uiState,
                onIntent = onIntent,
                modifier = modifier,
            )
        QuizStep.Completed ->
            QuizCompletionScreen(
                onCloseClick = { onIntent(QuizIntent.BackClick) },
                onNextClick = { onIntent(QuizIntent.BackClick) },
                modifier = modifier,
            )
    }

    if (showSource) {
        QuizSourceSheet(
            questionNumber = uiState.multipleChoiceQuestion.number,
            source = uiState.multipleChoiceQuestion.source,
            onDismiss = { showSource = false },
            onOpenSource = {
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
    var headerHeightPx by remember { mutableIntStateOf(0) }
    val headerHeight = with(LocalDensity.current) { headerHeightPx.toDp() }

    LaunchedEffect(sky, headerHeightPx) {
        if (headerHeightPx > 0) sky?.invalidate()
    }

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
            topPadding = headerHeight + 20.dp,
            modifier = Modifier.fillMaxSize().captureSky(sky),
        )
        QuizQuestionHeader(
            questionNumber = uiState.multipleChoiceQuestion.number,
            questionText = uiState.multipleChoiceQuestion.text,
            onBackClick = { onIntent(QuizIntent.BackClick) },
            sky = sky,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .onSizeChanged { headerHeightPx = it.height },
        )
        QuizBottomBar(
            isBookmarked = uiState.multipleChoiceQuestion.number in uiState.bookmarkedQuestionNumbers,
            buttonText = if (uiState.isMultipleChoiceSubmitted) "다음" else "정답 확인",
            onBookmarkClick = { onIntent(QuizIntent.BookmarkClick) },
            onButtonClick = {
                onIntent(if (uiState.isMultipleChoiceSubmitted) QuizIntent.Next else QuizIntent.Submit)
            },
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
 * @param topPadding 실제 질문 헤더 높이에 맞춘 목록 상단 여백
 * @param modifier 목록의 크기와 배치를 지정할 수식자
 */
@Composable
private fun QuizAnswerList(
    uiState: QuizUiState,
    onIntent: (QuizIntent) -> Unit,
    onSourceClick: () -> Unit,
    topPadding: Dp,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 20.dp, top = topPadding, end = 20.dp, bottom = 164.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(uiState.multipleChoiceQuestion.answers, key = { it.id }) { answer ->
            GitItMultipleChoiceAnswerCard(
                label = answer.label,
                answer = answer.text,
                modifier = Modifier.fillMaxWidth(),
                state = uiState.answerCardState(answer.id),
                onClick = { onIntent(QuizIntent.AnswerClick(answer.id)) },
            )
        }
        if (uiState.isMultipleChoiceSubmitted) {
            item { QuizExplanation(text = uiState.multipleChoiceQuestion.explanation) }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                QuizSourceButton(onClick = onSourceClick)
            }
        }
    }
}

/**
 * Figma의 grey600 배경과 blue100 콘텐츠를 사용하는 출처 버튼이다.
 *
 * @param onClick 출처 바텀시트를 여는 콜백
 */
@Composable
private fun QuizSourceButton(onClick: () -> Unit) {
    Row(
        modifier =
            Modifier
                .height(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(GitItTheme.colors.grey600)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(start = 16.dp, end = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "출처",
            color = GitItTheme.colors.blue100,
            style = GitItTheme.typography.body2,
        )
        QuizSourceChevron()
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
    questionNumber: Int,
    questionText: String,
    onBackClick: () -> Unit,
    sky: Sky?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().questionBackdrop(sky).statusBarsPadding()) {
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
                    .padding(start = 20.dp, top = 20.dp, end = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "문제 $questionNumber",
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(GitItTheme.colors.blue400)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                color = GitItTheme.colors.blue100,
                style = GitItTheme.typography.body2,
            )
            Text(
                text = questionText,
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
    buttonText: String,
    onBookmarkClick: () -> Unit,
    onButtonClick: () -> Unit,
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
            text = buttonText,
            onClick = onButtonClick,
            modifier = Modifier.weight(1f),
        )
    }
}

/**
 * 서술형 답안 입력과 AI 답안 비교 결과를 표시한다.
 *
 * @param uiState 현재 서술형 문제와 입력·제출 상태
 * @param onIntent 사용자 입력을 전달하는 콜백
 * @param modifier 화면의 크기와 배치를 지정할 수식자
 */
@Composable
private fun QuizEssayScreen(
    uiState: QuizUiState,
    onIntent: (QuizIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sky = if (LocalInspectionMode.current) null else rememberSky()
    var headerHeightPx by remember { mutableIntStateOf(0) }
    val headerHeight = with(LocalDensity.current) { headerHeightPx.toDp() }

    LaunchedEffect(sky, headerHeightPx) {
        if (headerHeightPx > 0) sky?.invalidate()
    }

    Box(modifier = modifier.fillMaxSize().background(GitItTheme.colors.grey700)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().captureSky(sky),
            contentPadding = PaddingValues(start = 20.dp, top = headerHeight + 20.dp, end = 20.dp, bottom = 164.dp),
        ) {
            item {
                if (uiState.isEssaySubmitted) {
                    QuizEssayResult(
                        answer = uiState.essayAnswer,
                        modelAnswer = uiState.essayQuestion.modelAnswer,
                    )
                } else {
                    QuizEssayInput(
                        answer = uiState.essayAnswer,
                        onAnswerChange = { onIntent(QuizIntent.EssayAnswerChange(it)) },
                    )
                }
            }
        }
        QuizQuestionHeader(
            questionNumber = uiState.essayQuestion.number,
            questionText = uiState.essayQuestion.text,
            onBackClick = { onIntent(QuizIntent.BackClick) },
            sky = sky,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .onSizeChanged { headerHeightPx = it.height },
        )
        QuizBottomBar(
            isBookmarked = uiState.essayQuestion.number in uiState.bookmarkedQuestionNumbers,
            buttonText = if (uiState.isEssaySubmitted) "다음" else "정답 확인",
            onBookmarkClick = { onIntent(QuizIntent.BookmarkClick) },
            onButtonClick = { onIntent(if (uiState.isEssaySubmitted) QuizIntent.Next else QuizIntent.Submit) },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

/**
 * 최대 300자의 서술형 답안을 입력하는 카드를 표시한다.
 *
 * @param answer 현재 입력된 답안
 * @param onAnswerChange 답안이 바뀔 때 호출할 콜백
 */
@Composable
private fun QuizEssayInput(
    answer: String,
    onAnswerChange: (String) -> Unit,
) {
    BasicTextField(
        value = answer,
        onValueChange = onAnswerChange,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(262.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(GitItTheme.colors.grey500)
                .padding(horizontal = 22.dp, vertical = 14.dp),
        textStyle = GitItTheme.typography.body2.copy(color = GitItTheme.colors.grey100),
        cursorBrush = SolidColor(GitItTheme.colors.blue200),
        decorationBox = { innerTextField ->
            Box(Modifier.fillMaxSize()) {
                if (answer.isEmpty()) {
                    Text(
                        text = "생각한 답을 자유롭게 적어보세요.",
                        color = GitItTheme.colors.grey400,
                        style = GitItTheme.typography.body2,
                    )
                }
                innerTextField()
                Text(
                    text = "${answer.length} / $ESSAY_ANSWER_MAX_LENGTH",
                    modifier = Modifier.align(Alignment.BottomEnd),
                    color = GitItTheme.colors.grey400,
                    style = GitItTheme.typography.body2,
                )
            }
        },
    )
}

/**
 * 서술형 제출 후 사용자의 답안과 AI 모범 답안을 비교해 표시한다.
 *
 * @param answer 사용자가 제출한 답안
 * @param modelAnswer 비교할 AI 모범 답안
 */
@Composable
private fun QuizEssayResult(
    answer: String,
    modelAnswer: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        QuizEssayResultCard(
            title = "나의 답안",
            text = answer.ifEmpty { "작성한 답안 없음" },
            containerColor = GitItTheme.colors.grey500,
            textColor = GitItTheme.colors.grey400,
        )
        QuizEssayResultCard(
            title = "AI의 답안",
            text = modelAnswer,
            containerColor = GitItTheme.colors.blue500,
            textColor = GitItTheme.colors.grey100,
        )
    }
}

/**
 * 서술형 결과의 제목과 본문을 하나의 카드로 표시한다.
 *
 * @param title 답안 작성 주체를 나타내는 제목
 * @param text 표시할 답안 본문
 * @param containerColor 카드 배경색
 * @param textColor 답안 본문 색상
 */
@Composable
private fun QuizEssayResultCard(
    title: String,
    text: String,
    containerColor: Color,
    textColor: Color,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(containerColor)
                .padding(horizontal = 22.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = title, color = GitItTheme.colors.grey200, style = GitItTheme.typography.body2)
        Text(text = text, color = textColor, style = GitItTheme.typography.body2)
    }
}

/**
 * 세트의 모든 문제를 푼 뒤 완료 수치와 축하 일러스트를 표시한다.
 *
 * @param onCloseClick 완료 화면을 닫는 콜백
 * @param onNextClick 다음 버튼으로 문제 풀이를 종료하는 콜백
 * @param modifier 화면의 크기와 배치를 지정할 수식자
 */
@Composable
private fun QuizCompletionScreen(
    onCloseClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize().background(GitItTheme.colors.grey700)) {
        QuizCloseButton(
            onClick = onCloseClick,
            modifier = Modifier.statusBarsPadding().padding(start = 20.dp, top = 8.dp),
        )
        Column(
            modifier = Modifier.align(Alignment.Center).padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "학습 완료", color = GitItTheme.colors.grey100, style = GitItTheme.typography.subtitle1)
            Spacer(Modifier.height(20.dp))
            Spacer(Modifier.size(145.dp))
            Spacer(Modifier.height(40.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "2", color = GitItTheme.colors.blue200, style = GitItTheme.typography.subtitle1)
                Text(text = "/", color = GitItTheme.colors.grey400, style = GitItTheme.typography.subtitle1)
                Text(text = "2", color = GitItTheme.colors.grey400, style = GitItTheme.typography.subtitle1)
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = "세트의 모든 문제를 다 풀었어요!",
                color = GitItTheme.colors.grey400,
                style = GitItTheme.typography.body1,
            )
        }
        GitItButton(
            text = "다음",
            onClick = onNextClick,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
            style = com.nexters.hytime.gitit.designsystem.button.GitItButtonStyle.Secondary,
        )
    }
}

/**
 * 학습 완료 화면의 닫기 버튼을 벡터 리소스 없이 그린다.
 *
 * @param onClick 완료 화면을 닫는 콜백
 * @param modifier 버튼의 크기와 배치를 지정할 수식자
 */
@Composable
private fun QuizCloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier =
            modifier
                .size(40.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(GitItTheme.colors.white15)
                .clickable(role = Role.Button, onClick = onClick)
                .semantics { contentDescription = "학습 완료 닫기" }
                .padding(11.dp),
    ) {
        val strokeWidth = 1.7.dp.toPx()
        drawLine(GitItTheme.colors.grey100, Offset.Zero, Offset(size.width, size.height), strokeWidth, StrokeCap.Round)
        drawLine(
            GitItTheme.colors.grey100,
            Offset(size.width, 0f),
            Offset(0f, size.height),
            strokeWidth,
            StrokeCap.Round,
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
    questionNumber: Int,
    source: QuizSource,
    onDismiss: () -> Unit,
    onOpenSource: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = GitItTheme.colors.grey600,
        contentColor = GitItTheme.colors.grey100,
        scrimColor = GitItTheme.colors.black70,
        tonalElevation = 0.dp,
        dragHandle = { QuizSourceSheetDragHandle() },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp),
        ) {
            Spacer(Modifier.height(12.dp))
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "문제 $questionNumber 출처",
                    color = GitItTheme.colors.grey100,
                    style = GitItTheme.typography.subtitle1.copy(fontSize = 22.sp, lineHeight = 32.56.sp),
                )
                Text(
                    text = source.description,
                    color = GitItTheme.colors.grey100,
                    style = GitItTheme.typography.body2,
                )
            }
            Spacer(Modifier.height(27.dp))
            QuizSourceLink(
                label = source.label,
                onClick = onOpenSource,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(24.dp))
            GitItButton(
                text = "닫기",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            )
        }
    }
}

/** Figma 출처 바텀시트 상단의 58×4dp grabber를 표시한다. */
@Composable
private fun QuizSourceSheetDragHandle() {
    Box(
        modifier = Modifier.fillMaxWidth().height(16.dp).padding(top = 5.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            modifier =
                Modifier
                    .size(width = 58.dp, height = 4.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(GitItTheme.colors.grey400),
        )
    }
}

/**
 * GitHub 파일 위치와 외부 링크 아이콘을 표시하는 출처 링크 카드다.
 *
 * @param label 파일과 라인 위치를 나타내는 문구
 * @param onClick GitHub 원본을 여는 콜백
 * @param modifier 카드의 크기와 배치를 지정할 수식자
 */
@Composable
private fun QuizSourceLink(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .height(54.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(GitItTheme.colors.grey500)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = GitItTheme.colors.white70,
            style = GitItTheme.typography.body1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        QuizExternalLinkIcon()
    }
}

/** 출처 버튼의 오른쪽 방향 chevron을 그린다. */
@Composable
private fun QuizSourceChevron() {
    Canvas(modifier = Modifier.size(16.dp)) {
        val path =
            Path().apply {
                moveTo(size.width * 0.35f, size.height * 0.25f)
                lineTo(size.width * 0.6f, size.height * 0.5f)
                lineTo(size.width * 0.35f, size.height * 0.75f)
            }
        drawPath(
            path = path,
            color = GitItTheme.colors.blue100,
            style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

/** GitHub 링크 카드의 외부 링크 아이콘을 그린다. */
@Composable
private fun QuizExternalLinkIcon() {
    Canvas(modifier = Modifier.size(20.dp)) {
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val box =
            Path().apply {
                moveTo(size.width * 0.45f, size.height * 0.2f)
                lineTo(size.width * 0.2f, size.height * 0.2f)
                lineTo(size.width * 0.2f, size.height * 0.8f)
                lineTo(size.width * 0.8f, size.height * 0.8f)
                lineTo(size.width * 0.8f, size.height * 0.55f)
            }
        drawPath(box, color = Color.White, style = stroke)
        drawLine(
            color = Color.White,
            start = center,
            end =
                androidx.compose.ui.geometry
                    .Offset(size.width * 0.85f, size.height * 0.15f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = Color.White,
            start =
                androidx.compose.ui.geometry
                    .Offset(size.width * 0.55f, size.height * 0.15f),
            end =
                androidx.compose.ui.geometry
                    .Offset(size.width * 0.85f, size.height * 0.15f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = Color.White,
            start =
                androidx.compose.ui.geometry
                    .Offset(size.width * 0.85f, size.height * 0.15f),
            end =
                androidx.compose.ui.geometry
                    .Offset(size.width * 0.85f, size.height * 0.45f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

/**
 * 현재 선택·채점 상태를 디자인 시스템 답안 카드 상태로 변환한다.
 *
 * @param answerId 상태를 계산할 답안 식별자
 * @return 선택 전·정답·오답·접힘 여부를 반영한 카드 상태
 */
internal fun QuizUiState.answerCardState(answerId: String): GitItMultipleChoiceAnswerState {
    if (!isMultipleChoiceSubmitted) {
        return if (selectedAnswerId == answerId) {
            GitItMultipleChoiceAnswerState.Selected
        } else {
            GitItMultipleChoiceAnswerState.Default
        }
    }

    val expanded = answerId in expandedAnswerIds
    return when {
        answerId == multipleChoiceQuestion.correctAnswerId && expanded -> GitItMultipleChoiceAnswerState.Correct
        answerId == multipleChoiceQuestion.correctAnswerId -> GitItMultipleChoiceAnswerState.CorrectFolded
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
                    step = QuizStep.MultipleChoice,
                    selectedAnswerId = "render",
                    isMultipleChoiceSubmitted = true,
                    expandedAnswerIds = setOf("render", "set-content"),
                ),
            onIntent = {},
        )
    }
}

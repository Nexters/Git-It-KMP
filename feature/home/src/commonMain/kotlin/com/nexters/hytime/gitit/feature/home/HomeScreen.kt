package com.nexters.hytime.gitit.feature.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.button.GitItButton
import com.nexters.hytime.gitit.designsystem.button.GitItButtonSize
import com.nexters.hytime.gitit.designsystem.button.GitItButtonState
import com.nexters.hytime.gitit.designsystem.card.GitItLearningCard
import com.nexters.hytime.gitit.designsystem.navigation.GitItMainNavBar
import com.nexters.hytime.gitit.designsystem.navigation.GitItMainNavDestination
import com.nexters.hytime.gitit.designsystem.navigation.gitItMainNavSky
import com.nexters.hytime.gitit.designsystem.navigation.rememberGitItMainNavSky
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBar
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBarType
import org.jetbrains.compose.resources.stringResource
import kotlin.math.absoluteValue

/**
 * Figma 홈 화면을 프로젝트 유무에 맞춰 표시한다.
 *
 * @param uiState 화면에 표시할 홈 UI 상태
 * @param isQuizCreating 문제 생성 중 버튼을 비활성 로딩 상태로 표시할지 여부
 * @param onIntent 사용자 입력을 ViewModel로 올리는 콜백
 * @param modifier 홈 화면의 크기와 배치를 지정할 수식자
 */
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    isQuizCreating: Boolean = false,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sky = rememberGitItMainNavSky()

    BoxWithConstraints(
        modifier =
            modifier
                .fillMaxSize()
                .background(GitItTheme.colors.grey700),
    ) {
        val viewportWidth = maxWidth

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .gitItMainNavSky(sky)
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .padding(top = 23.dp),
        ) {
            HomeProfile(
                uiState = uiState,
                onClick = { onIntent(HomeIntent.MyTabClick) },
            )
            Spacer(Modifier.height(18.dp))
            HomeTitle()
            Spacer(Modifier.height(22.dp))
            ProjectImport(
                isQuizCreating = isQuizCreating,
                onClick = { onIntent(HomeIntent.LoadProjectClick) },
            )
            Spacer(Modifier.height(40.dp))
            LearningSection(
                uiState = uiState,
                viewportWidth = viewportWidth,
                onIntent = onIntent,
            )
        }

        GitItMainNavBar(
            selectedDestination = GitItMainNavDestination.Home,
            onDestinationClick = { destination ->
                onIntent(
                    when (destination) {
                        GitItMainNavDestination.Home -> HomeIntent.HomeTabClick
                        GitItMainNavDestination.Project -> HomeIntent.ProjectTabClick
                        GitItMainNavDestination.Saved -> HomeIntent.SavedTabClick
                        GitItMainNavDestination.My -> HomeIntent.MyTabClick
                    },
                )
            },
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 27.dp, end = 27.dp, bottom = 29.dp),
            sky = sky,
        )
    }
}

/**
 * 사용자 프로필을 Figma 인라인 툴바 규격으로 표시한다.
 *
 * @param uiState 사용자 이름과 역할을 포함한 홈 상태
 * @param onClick 프로필 선택 시 마이 화면 이동 의도를 전달할 동작
 */
@Composable
private fun HomeProfile(
    uiState: HomeUiState,
    onClick: () -> Unit,
) {
    GitItTopBar(
        type = GitItTopBarType.InlineUser,
        userName = uiState.userName,
        userSubtitle = uiState.careerLevel?.let { stringResource(it.toRoleLabelResource()) }.orEmpty(),
        userAvatar = {
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable(role = Role.Button, onClick = onClick)
                        .background(GitItTheme.colors.grey300),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = uiState.userName.take(1),
                    color = GitItTheme.colors.grey700,
                    style = GitItTheme.typography.subtitle3,
                )
            }
        },
    )
}

/** 홈의 영문 인사말을 표시한다. */
@Composable
private fun HomeTitle() {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "Hello World",
            color = GitItTheme.colors.grey400,
            style = GitItTheme.typography.headline1.copy(letterSpacing = (-0.6).sp),
        )
        Text(
            text = "Let’s Git -it-!",
            color = GitItTheme.colors.grey100,
            style = GitItTheme.typography.headline1.copy(letterSpacing = (-0.6).sp),
        )
    }
}

/**
 * 프로젝트 불러오기 안내와 진입 버튼을 표시한다.
 *
 * @param isQuizCreating 문제 생성 중 버튼을 로딩 상태로 표시할지 여부
 * @param onClick 프로젝트 불러오기 화면으로 이동할 동작
 */
@Composable
private fun ProjectImport(
    isQuizCreating: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .height(201.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(GitItTheme.colors.grey600),
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, top = 13.dp)) {
            Text(
                text = "프로젝트 퀴즈 생성",
                color = GitItTheme.colors.grey400,
                style = GitItTheme.typography.caption1,
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = "오픈소스를 불러오고\n문제로 익혀보세요",
                color = GitItTheme.colors.grey100,
                style = GitItTheme.typography.subtitle3,
            )
        }
        GitItButton(
            text = if (isQuizCreating) "문제 생성 중" else "지금 불러오기",
            onClick = onClick,
            size = GitItButtonSize.Small,
            state = if (isQuizCreating) GitItButtonState.Disabled else GitItButtonState.Default,
            leadingIcon = if (isQuizCreating) ({ QuizCreateLoadingIndicator() }) else null,
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 12.dp)
                    .then(if (isQuizCreating) Modifier else Modifier.width(104.dp)),
        )
    }
}

/** Figma의 생성 중 버튼에 16dp 회전 로딩 링을 표시한다. */
@Composable
private fun QuizCreateLoadingIndicator() {
    val transition = rememberInfiniteTransition(label = "home-quiz-create-loading")
    val rotation by
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(animation = tween(durationMillis = 900, easing = LinearEasing)),
            label = "home-quiz-create-loading-rotation",
        )
    val color = GitItTheme.colors.blue100

    Canvas(modifier = Modifier.size(16.dp)) {
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
 * 학습 중인 레포지토리 제목과 카드 또는 빈 상태를 표시한다.
 *
 * @param uiState 학습 카드 목록을 포함한 홈 상태
 * @param viewportWidth 카드가 사용할 수 있는 화면 너비
 * @param onIntent 카드와 전체 보기 입력을 전달할 콜백
 */
@Composable
private fun LearningSection(
    uiState: HomeUiState,
    viewportWidth: Dp,
    onIntent: (HomeIntent) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "학습 중인 레포지토리",
            color = GitItTheme.colors.grey100,
            style = GitItTheme.typography.subtitle3,
        )
        Row(
            modifier =
                Modifier.clickable(role = Role.Button) {
                    onIntent(HomeIntent.ViewAllProjectsClick)
                },
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "전체 보기",
                color = GitItTheme.colors.blue100,
                style = GitItTheme.typography.caption1,
            )
            Text(
                text = "›",
                color = GitItTheme.colors.blue100,
                style = GitItTheme.typography.subtitle3,
            )
        }
    }

    if (uiState.learningProjects.isEmpty()) {
        EmptyLearningProjects()
    } else {
        Box {
            if (uiState.learningProjects.size < 3) {
                EmptyLearningProjects(showMessage = false)
            }
            LearningProjectPager(
                projects = uiState.learningProjects,
                viewportWidth = viewportWidth,
                onIntent = onIntent,
            )
        }
    }
}

/**
 * 비어 있는 프로젝트 카드 윤곽을 표시한다.
 *
 * @param showMessage 등록된 프로젝트가 없다는 안내 문구를 함께 표시할지 여부
 */
@Composable
private fun EmptyLearningProjects(showMessage: Boolean = true) {
    BoxWithConstraints(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(237.dp),
        contentAlignment = Alignment.Center,
    ) {
        val fillColor = GitItTheme.colors.blue500.copy(alpha = 0.3f)
        val outlineColor = GitItTheme.colors.blue300.copy(alpha = 0.3f)
        val cardCount = emptyLearningProjectCardCount(maxWidth)
        Canvas(Modifier.fillMaxSize()) {
            val cardSize = Size(154.dp.toPx(), 192.dp.toPx())
            val cornerRadius = CornerRadius(12.dp.toPx())

            repeat(cardCount) { index ->
                val left = (20 + index * 154 + if (index % 2 == 1) 2 else 0).dp.toPx()
                val topLeft = Offset(left, 27.dp.toPx())
                rotate(
                    degrees = learningCardAngle(index, index.coerceAtMost(1).toFloat()),
                    pivot = Offset(left + 77.dp.toPx(), 123.dp.toPx()),
                ) {
                    drawRoundRect(
                        color = fillColor,
                        topLeft = topLeft,
                        size = cardSize,
                        cornerRadius = cornerRadius,
                    )
                    drawRoundRect(
                        color = outlineColor,
                        topLeft = topLeft,
                        size = cardSize,
                        cornerRadius = cornerRadius,
                        style = Stroke(1.dp.toPx()),
                    )
                }
            }
        }
        if (showMessage) {
            Text(
                text = "아직 등록된 프로젝트가 없어요.",
                color = GitItTheme.colors.purple200,
                textAlign = TextAlign.Center,
                style = GitItTheme.typography.body2,
            )
        }
    }
}

/**
 * 빈 카드 영역이 화면 오른쪽까지 이어지도록 필요한 카드 수를 계산한다.
 *
 * @param availableWidth 빈 카드 영역에 사용할 수 있는 화면 너비
 * @return 화면 끝을 넘어 그려질 최소 카드 수
 */
internal fun emptyLearningProjectCardCount(availableWidth: Dp): Int = (((availableWidth.value - 20f) / 154f).toInt() + 1).coerceAtLeast(1)

/**
 * 카드를 수평으로 넘기며 현재 카드가 정면을 향하도록 회전시킨다.
 *
 * @param projects 표시할 학습 프로젝트 목록
 * @param viewportWidth 마지막 페이지 여백을 계산할 화면 너비
 * @param onIntent 카드 입력을 전달할 콜백
 */
@Composable
private fun LearningProjectPager(
    projects: List<HomeLearningProject>,
    viewportWidth: Dp,
    onIntent: (HomeIntent) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = projects::size)
    val cardSize = learningCardSize()
    // 실제 데이터의 개수와 무관하게 카드 순서대로 세 색상을 반복한다.
    val backgroundColors =
        listOf(
            GitItTheme.colors.purple300,
            GitItTheme.colors.blue100,
            GitItTheme.colors.blue500,
        )

    HorizontalPager(
        state = pagerState,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(cardSize.height + 45.dp),
        contentPadding =
            PaddingValues(
                start = 20.dp,
                end = (viewportWidth - 20.dp - cardSize.width).coerceAtLeast(20.dp),
            ),
        pageSize = PageSize.Fixed(cardSize.width),
        beyondViewportPageCount = 2,
        userScrollEnabled = projects.size > 2,
        key = { projects[it].id },
    ) { page ->
        val project = projects[page]
        val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .zIndex(1f - pageOffset)
                    .padding(top = 27.dp),
        ) {
            GitItLearningCard(
                title = project.title,
                technologies = project.technologies,
                setLabel = project.setLabel,
                description = project.description,
                progress = project.progress,
                backgroundColor = backgroundColors[page % backgroundColors.size],
                onCardClick = { onIntent(HomeIntent.LearningCardClick(project.id)) },
                onPlayClick = { onIntent(HomeIntent.LearningPlayClick(project.id)) },
                modifier =
                    Modifier
                        .size(cardSize)
                        .graphicsLayer { rotationZ = learningCardAngle(page, pageOffset) },
            )
        }
    }
}

/**
 * 학습 카드를 화면 크기와 무관하게 고정 크기로 표시한다.
 *
 * @return 홈 화면에서 사용하는 154×192dp 카드 크기
 */
internal fun learningCardSize(): DpSize = DpSize(width = 154.dp, height = 192.dp)

/**
 * 페이지 거리에 따라 Figma의 교차 카드 각도를 계산한다.
 *
 * @param page 카드 인덱스
 * @param pageOffset 현재 페이지에서 떨어진 절대 거리
 * @return 현재 카드는 0도, 인접 카드는 방향이 교차하는 회전 각도
 */
internal fun learningCardAngle(
    page: Int,
    pageOffset: Float,
): Float {
    if (pageOffset == 0f) return 0f

    val restingAngle = if (page % 2 == 0) -12f else 16f
    return restingAngle * pageOffset.coerceIn(0f, 1f)
}

@Preview(name = "홈 - 빈 상태")
@Composable
private fun EmptyHomeScreenPreview() {
    GitItTheme {
        HomeScreen(uiState = HomeUiState(), onIntent = {})
    }
}

@Preview(name = "홈 - 문제 생성 중")
@Composable
private fun CreatingHomeScreenPreview() {
    GitItTheme {
        HomeScreen(
            uiState = HomeUiState(),
            isQuizCreating = true,
            onIntent = {},
        )
    }
}

@Preview(name = "홈 - 학습 카드")
@Composable
private fun LearningHomeScreenPreview() {
    GitItTheme {
        HomeScreen(
            uiState =
                HomeUiState(
                    learningProjects =
                        listOf(
                            HomeLearningProject(
                                id = "nexters",
                                title = "Nexters",
                                technologies = "Kotlin · Compose · Coroutines",
                                setLabel = "Set 1",
                                description = "Compose 핵심 개념",
                                progress = 0.21f,
                            ),
                            HomeLearningProject(
                                id = "now-in-android",
                                title = "Now in\nAndroid",
                                technologies = "Kotlin · Compose · Coroutines",
                                setLabel = "Set 1",
                                description = "Compose 핵심 개념",
                                progress = 0.21f,
                            ),
                            HomeLearningProject(
                                id = "compose-samples",
                                title = "Compose\nSamples",
                                technologies = "Kotlin · Compose",
                                setLabel = "Set 2",
                                description = "상태 관리 익히기",
                                progress = 0.42f,
                            ),
                        ),
                ),
            onIntent = {},
        )
    }
}

package com.nexters.hytime.gitit.feature.home

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.button.GitItButton
import com.nexters.hytime.gitit.designsystem.button.GitItButtonSize
import com.nexters.hytime.gitit.designsystem.card.GitItLearningCard
import com.nexters.hytime.gitit.designsystem.navigation.GitItMainNavBar
import com.nexters.hytime.gitit.designsystem.navigation.GitItMainNavDestination
import com.nexters.hytime.gitit.designsystem.navigation.gitItMainNavSky
import com.nexters.hytime.gitit.designsystem.navigation.rememberGitItMainNavSky
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBar
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBarType
import kotlin.math.absoluteValue

/**
 * Figma 홈 화면을 프로젝트 유무에 맞춰 표시한다.
 *
 * @param uiState 화면에 표시할 홈 UI 상태
 * @param onIntent 사용자 입력을 ViewModel로 올리는 콜백
 * @param modifier 홈 화면의 크기와 배치를 지정할 수식자
 */
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sky = rememberGitItMainNavSky()

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(GitItTheme.colors.grey700),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .gitItMainNavSky(sky)
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .padding(top = 23.dp),
        ) {
            HomeProfile(uiState = uiState)
            Spacer(Modifier.height(18.dp))
            HomeTitle()
            Spacer(Modifier.height(22.dp))
            ProjectImport(onClick = { onIntent(HomeIntent.LoadProjectClick) })
            Spacer(Modifier.height(40.dp))
            LearningSection(uiState = uiState, onIntent = onIntent)
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
 */
@Composable
private fun HomeProfile(uiState: HomeUiState) {
    GitItTopBar(
        type = GitItTopBarType.InlineUser,
        userName = uiState.userName,
        userSubtitle = uiState.userRole,
        userAvatar = {
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(CircleShape)
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
 * @param onClick 프로젝트 불러오기 화면으로 이동할 동작
 */
@Composable
private fun ProjectImport(onClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .height(133.dp)
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
                text = "오픈소스를 불러오고\n퀴즈로 익혀보세요",
                color = GitItTheme.colors.grey100,
                style = GitItTheme.typography.subtitle3,
            )
        }
        GitItButton(
            text = "지금 불러오기",
            onClick = onClick,
            size = GitItButtonSize.Small,
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 12.dp)
                    .width(104.dp),
        )
    }
}

/**
 * 학습 중인 레포지토리 제목과 카드 또는 빈 상태를 표시한다.
 *
 * @param uiState 학습 카드 목록을 포함한 홈 상태
 * @param onIntent 카드와 전체 보기 입력을 전달할 콜백
 */
@Composable
private fun LearningSection(
    uiState: HomeUiState,
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
            LearningProjectPager(projects = uiState.learningProjects, onIntent = onIntent)
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
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(237.dp),
        contentAlignment = Alignment.Center,
    ) {
        val outlineColor = GitItTheme.colors.blue300.copy(alpha = 0.26f)
        Canvas(Modifier.fillMaxSize()) {
            drawRoundRect(
                color = outlineColor,
                topLeft =
                    androidx.compose.ui.geometry
                        .Offset(20.dp.toPx(), 27.dp.toPx()),
                size =
                    androidx.compose.ui.geometry
                        .Size(154.dp.toPx(), 192.dp.toPx()),
                cornerRadius =
                    androidx.compose.ui.geometry
                        .CornerRadius(12.dp.toPx()),
                style = Stroke(1.dp.toPx()),
            )
            rotate(
                degrees = 16f,
                pivot =
                    androidx.compose.ui.geometry
                        .Offset(253.dp.toPx(), 123.dp.toPx()),
            ) {
                drawRoundRect(
                    color = outlineColor,
                    topLeft =
                        androidx.compose.ui.geometry
                            .Offset(176.dp.toPx(), 27.dp.toPx()),
                    size =
                        androidx.compose.ui.geometry
                            .Size(154.dp.toPx(), 192.dp.toPx()),
                    cornerRadius =
                        androidx.compose.ui.geometry
                            .CornerRadius(12.dp.toPx()),
                    style = Stroke(1.dp.toPx()),
                )
            }
            rotate(
                degrees = -12f,
                pivot =
                    androidx.compose.ui.geometry
                        .Offset(405.dp.toPx(), 123.dp.toPx()),
            ) {
                drawRoundRect(
                    color = outlineColor,
                    topLeft =
                        androidx.compose.ui.geometry
                            .Offset(328.dp.toPx(), 27.dp.toPx()),
                    size =
                        androidx.compose.ui.geometry
                            .Size(154.dp.toPx(), 192.dp.toPx()),
                    cornerRadius =
                        androidx.compose.ui.geometry
                            .CornerRadius(12.dp.toPx()),
                    style = Stroke(1.dp.toPx()),
                )
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
 * 카드를 수평으로 넘기며 현재 카드가 정면을 향하도록 회전시킨다.
 *
 * @param projects 표시할 학습 프로젝트 목록
 * @param onIntent 카드 입력을 전달할 콜백
 */
@Composable
private fun LearningProjectPager(
    projects: List<HomeLearningProject>,
    onIntent: (HomeIntent) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = projects::size)
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
                .height(237.dp),
        contentPadding = PaddingValues(start = 20.dp, end = 186.dp),
        pageSize = PageSize.Fixed(154.dp),
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
                modifier = Modifier.graphicsLayer { rotationZ = learningCardAngle(page, pageOffset) },
            )
        }
    }
}

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

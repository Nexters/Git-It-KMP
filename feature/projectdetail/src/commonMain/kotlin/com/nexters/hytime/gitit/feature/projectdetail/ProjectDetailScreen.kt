package com.nexters.hytime.gitit.feature.projectdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassDropdownMenu
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassDropdownMenuItem
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButton
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButtonSize
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButtonVariant
import com.nexters.hytime.gitit.designsystem.navigation.gitItMainNavSky
import com.nexters.hytime.gitit.designsystem.navigation.rememberGitItMainNavSky
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBar
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBarType
import com.skydoves.cloudy.Sky
import git_it_kmp.core.designsystem.generated.resources.Res
import git_it_kmp.core.designsystem.generated.resources.ic_menu
import git_it_kmp.core.designsystem.generated.resources.ic_play_circle
import git_it_kmp.core.designsystem.generated.resources.ic_play_project
import git_it_kmp.feature.projectdetail.generated.resources.ic_star
import org.jetbrains.compose.resources.painterResource
import git_it_kmp.feature.projectdetail.generated.resources.Res as ProjectDetailRes

/**
 * 프로젝트 상세 화면의 순수 UI 영역이다. 상태와 콜백만 주입받아 상태를 소유하지 않는다.
 *
 * @param uiState 단일 UI 상태
 * @param onBackClick 뒤로가기 콜백
 * @param onMoreMenuClick 더보기 메뉴 토글 콜백
 * @param onDismissMoreMenu 더보기 메뉴 닫기 콜백
 * @param onSavedQuestionsClick 저장한 문제 콜백
 * @param onQuestionSolvingClick 문제풀이 바로가기 콜백
 * @param onDeleteProjectClick 삭제 콜백
 * @param onLearningSetClick 학습 세트 진입 콜백
 * @param modifier 화면의 크기와 배치를 지정할 수식자
 */
@Composable
fun ProjectDetailScreen(
    uiState: ProjectDetailUiState,
    onBackClick: () -> Unit,
    onMoreMenuClick: () -> Unit,
    onDismissMoreMenu: () -> Unit,
    onSavedQuestionsClick: () -> Unit,
    onQuestionSolvingClick: () -> Unit,
    onDeleteProjectClick: () -> Unit,
    onLearningSetClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sky = rememberGitItMainNavSky()
    val dismissMenuInteractionSource = remember { MutableInteractionSource() }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(GitItTheme.colors.grey700),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(179.dp)
                    .drawWithCache {
                        val center = Offset(size.width * 0.71f, -size.height * 0.08f)
                        val brush =
                            Brush.radialGradient(
                                0f to Color(0xFF56718A),
                                0.5f to Color(0xFF485469),
                                1f to Color(0xFF3B3749),
                                center = center,
                                radius = size.width * 0.73f,
                            )

                        onDrawBehind {
                            scale(scaleX = 2f, scaleY = 1f, pivot = center) {
                                drawRect(brush = brush)
                            }
                        }
                    },
        )

        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            ProjectDetailTopBar(
                onBackClick = onBackClick,
                onMoreClick = onMoreMenuClick,
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .gitItMainNavSky(sky)
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
            ) {
                item {
                    ProjectHeader(uiState = uiState, onQuestionSolvingClick = onQuestionSolvingClick)
                    Spacer(Modifier.height(28.dp))
                }
                item {
                    ProjectTotalProgress(progress = uiState.totalProgress)
                    Spacer(Modifier.height(31.dp))
                }
                item {
                    Text(
                        text = "학습 세트",
                        color = GitItTheme.colors.grey100,
                        style = GitItTheme.typography.subtitle2,
                    )
                    Spacer(Modifier.height(16.dp))
                }
                items(uiState.learningSets, key = { it.id }) { set ->
                    LearningSetCard(item = set, onClick = { onLearningSetClick(set.id) })
                    Spacer(Modifier.height(12.dp))
                }
            }
        }

        if (uiState.showMoreMenu) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = dismissMenuInteractionSource,
                            indication = null,
                            onClick = onDismissMoreMenu,
                        ),
            )
            ProjectDetailMoreMenu(
                onSavedQuestionsClick = onSavedQuestionsClick,
                onDeleteProjectClick = onDeleteProjectClick,
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 102.dp, end = 20.dp)
                        .width(160.dp),
                sky = sky,
            )
        }
    }
}

/**
 * 상단 툴바. 좌측 뒤로가기 버튼과 우측 더보기 버튼을 배치한다.
 *
 * @param onBackClick 뒤로가기 콜백
 * @param onMoreClick 더보기 버튼 클릭 콜백
 */
@Composable
private fun ProjectDetailTopBar(
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit,
) {
    GitItTopBar(
        type = GitItTopBarType.Default,
        modifier = Modifier.padding(top = 8.dp),
        onBackClick = onBackClick,
        actions = {
            GitItLiquidGlassIconButton(
                onClick = onMoreClick,
                size = GitItLiquidGlassIconButtonSize.Md,
                variant = GitItLiquidGlassIconButtonVariant.Secondary,
            ) {
                MenuIcon()
            }
        },
    )
}

/**
 * 헤더 영역: 썸네일·제목·스타 수/기술스택·문제풀이 바로가기 버튼을 배치한다.
 *
 * @param uiState 단일 UI 상태
 * @param onQuestionSolvingClick 문제풀이 바로가기 콜백
 */
@Composable
private fun ProjectHeader(
    uiState: ProjectDetailUiState,
    onQuestionSolvingClick: () -> Unit,
) {
    val project = uiState.project ?: return

    Box(
        modifier =
            Modifier
                .size(99.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(GitItTheme.colors.grey600),
        contentAlignment = Alignment.Center,
    ) {
        // TODO: data 연동 후 AsyncImage로 교체한다.
        Text(
            text = project.name.take(1),
            color = GitItTheme.colors.grey100,
            style = GitItTheme.typography.headline2,
        )
    }

    Spacer(Modifier.height(31.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.padding(end = 8.dp)) {
            Text(
                text = project.name,
                color = GitItTheme.colors.grey100,
                style = GitItTheme.typography.headline2,
            )
            Spacer(Modifier.height(5.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                StarIcon()
                Spacer(Modifier.width(5.dp))
                Text(
                    text = project.starCount,
                    color = GitItTheme.colors.blue100,
                    style = GitItTheme.typography.caption1,
                )
                Spacer(Modifier.width(9.dp))
                Box(
                    modifier =
                        Modifier
                            .size(width = 1.dp, height = 16.dp)
                            .background(GitItTheme.colors.grey500),
                )
                Spacer(Modifier.width(9.dp))
                Text(
                    text = project.techStack,
                    color = GitItTheme.colors.blue100,
                    style = GitItTheme.typography.caption1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Image(
            painter = painterResource(Res.drawable.ic_play_project),
            contentDescription = null,
            modifier = Modifier.size(40.dp).clickable(onClick = onQuestionSolvingClick),
        )
    }
}

/**
 * 전체 진행률 영역: 라벨·퍼센트·프로그레스 바.
 *
 * @param progress 진행률(0..100)
 */
@Composable
private fun ProjectTotalProgress(progress: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "전체 진행률",
                color = GitItTheme.colors.grey400,
                style = GitItTheme.typography.caption1,
            )
            Text(
                text = "$progress%",
                color = GitItTheme.colors.blue100,
                style = GitItTheme.typography.caption1,
            )
        }
        Spacer(Modifier.height(10.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GitItTheme.colors.grey400),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(progress / 100f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GitItTheme.colors.blue200),
            )
        }
    }
}

/**
 * 학습 세트 카드. 제목·설명·7칸 프로그레스 바·우측 재생 아이콘을 배치한다.
 *
 * @param item 학습 세트 데이터
 * @param onClick 카드 클릭 콜백
 */
@Composable
private fun LearningSetCard(
    item: LearningSetItem,
    onClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(GitItTheme.colors.grey700)
                .border(1.dp, GitItTheme.colors.grey500, RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 18.dp, vertical = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column {
                Text(
                    text = item.title,
                    color = GitItTheme.colors.blue100,
                    style = GitItTheme.typography.subtitle3,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = item.description,
                    color = GitItTheme.colors.grey100,
                    style = GitItTheme.typography.body1,
                )
            }
            Image(
                painter = painterResource(Res.drawable.ic_play_circle),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
            )
        }
        Spacer(Modifier.height(25.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(item.totalSteps) { index ->
                val filledSteps = item.progress.coerceIn(0, 100) * item.totalSteps / 100
                val filled = index < filledSteps
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(10.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (filled) GitItTheme.colors.blue200 else GitItTheme.colors.grey500),
                )
            }
        }
    }
}

/**
 * 더보기 드롭다운 메뉴.
 *
 * @param onSavedQuestionsClick 저장한 문제 콜백
 * @param onDeleteProjectClick 삭제 콜백
 * @param modifier 메뉴의 크기와 화면 내 배치를 지정할 수식자
 * @param sky 뒤쪽 콘텐츠를 흐림 배경으로 읽을 Cloudy 상태
 */
@Composable
private fun ProjectDetailMoreMenu(
    onSavedQuestionsClick: () -> Unit,
    onDeleteProjectClick: () -> Unit,
    modifier: Modifier = Modifier,
    sky: Sky? = null,
) {
    GitItLiquidGlassDropdownMenu(
        modifier = modifier,
        sky = sky,
    ) {
        GitItLiquidGlassDropdownMenuItem(text = "저장한 문제", onClick = onSavedQuestionsClick)
        GitItLiquidGlassDropdownMenuItem(text = "GitHub에서 보기", onClick = {})
        GitItLiquidGlassDropdownMenuItem(
            text = "삭제하기",
            color = GitItTheme.colors.error,
            onClick = onDeleteProjectClick,
        )
    }
}

@Composable
private fun MenuIcon() {
    Icon(
        painter = painterResource(Res.drawable.ic_menu),
        contentDescription = null,
        modifier = Modifier.size(width = 17.dp, height = 12.dp),
        tint = Color.White,
    )
}

/** Figma 프로젝트 스타 아이콘을 16dp 영역 중앙에 표시한다. */
@Composable
private fun StarIcon() {
    Box(
        modifier = Modifier.size(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(ProjectDetailRes.drawable.ic_star),
            contentDescription = null,
            modifier = Modifier.size(14.dp),
        )
    }
}

@Preview
@Composable
private fun ProjectDetailScreenPreview() {
    GitItTheme {
        ProjectDetailScreen(
            uiState =
                ProjectDetailUiState(
                    project =
                        ProjectInfo(
                            name = "Nexters",
                            thumbnailUrl = "",
                            starCount = "3.6k",
                            techStack = "Kotlin · Compose · Coroutines",
                        ),
                    learningSets =
                        listOf(
                            LearningSetItem("1", "Set 1", "아이디어 PT 핵심 내용 확인하기", progress = 100),
                            LearningSetItem("2", "Set 2", "서비스 문제와 타깃 알아보기", progress = 57),
                            LearningSetItem("3", "Set 3", "아이디어별 해결 방식 비교하기"),
                        ),
                    totalProgress = 42,
                ),
            onBackClick = {},
            onMoreMenuClick = {},
            onDismissMoreMenu = {},
            onSavedQuestionsClick = {},
            onQuestionSolvingClick = {},
            onDeleteProjectClick = {},
            onLearningSetClick = {},
        )
    }
}

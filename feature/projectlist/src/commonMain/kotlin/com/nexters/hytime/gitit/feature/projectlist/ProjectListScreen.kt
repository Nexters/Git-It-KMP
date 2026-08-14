package com.nexters.hytime.gitit.feature.projectlist

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButton
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButtonSize
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButtonVariant
import com.nexters.hytime.gitit.designsystem.navigation.GitItMainNavBar
import com.nexters.hytime.gitit.designsystem.navigation.GitItMainNavDestination
import com.nexters.hytime.gitit.designsystem.navigation.gitItMainNavSky
import com.nexters.hytime.gitit.designsystem.navigation.rememberGitItMainNavSky
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBar
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBarType
import git_it_kmp.core.designsystem.generated.resources.ic_menu
import git_it_kmp.feature.projectlist.generated.resources.Res
import git_it_kmp.feature.projectlist.generated.resources.ic_play_project_list
import git_it_kmp.feature.projectlist.generated.resources.project_play_button_description
import git_it_kmp.feature.projectlist.generated.resources.projectlist_thumbnail_base
import git_it_kmp.feature.projectlist.generated.resources.projectlist_thumbnail_mark
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import git_it_kmp.core.designsystem.generated.resources.Res as DesignSystemRes

/**
 * 프로젝트 리스트 화면의 순수 UI 영역이다.
 *
 * @param uiState 화면에 표시할 프로젝트 리스트 상태
 * @param onIntent 사용자 입력을 ViewModel로 올리는 콜백
 * @param showBackButton 상단 뒤로가기 버튼 표시 여부
 * @param modifier 화면의 크기와 배치를 지정할 수식자
 */
@Composable
fun ProjectListScreen(
    uiState: ProjectListUiState,
    onIntent: (ProjectListIntent) -> Unit,
    showBackButton: Boolean,
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
                    .background(GitItTheme.colors.grey700)
                    .gitItMainNavSky(sky)
                    .statusBarsPadding(),
        ) {
            GitItTopBar(
                type = if (showBackButton) GitItTopBarType.LargeTitle else GitItTopBarType.InlineTitle,
                title = "프로젝트",
                modifier = Modifier.padding(top = 8.dp),
                onBackClick = if (showBackButton) ({ onIntent(ProjectListIntent.BackClick) }) else null,
                actions = {
                    GitItLiquidGlassIconButton(
                        onClick = {},
                        size = GitItLiquidGlassIconButtonSize.Md,
                        variant = GitItLiquidGlassIconButtonVariant.Secondary,
                    ) {
                        ProjectListMenuIcon()
                    }
                },
            )
            Spacer(Modifier.height(if (showBackButton) 21.dp else 9.dp))

            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(uiState.projects, key = { it.id }) { project ->
                    ProjectCard(
                        item = project,
                        onPlayClick = { onIntent(ProjectListIntent.PlayProjectClick(project.id)) },
                    )
                }
            }
        }

        GitItMainNavBar(
            selectedDestination = GitItMainNavDestination.Project,
            onDestinationClick = { destination ->
                when (destination) {
                    GitItMainNavDestination.Home -> onIntent(ProjectListIntent.HomeTabClick)
                    GitItMainNavDestination.Project -> onIntent(ProjectListIntent.ProjectTabClick)
                    GitItMainNavDestination.Saved -> onIntent(ProjectListIntent.SavedTabClick)
                    GitItMainNavDestination.My -> onIntent(ProjectListIntent.MyTabClick)
                }
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
 * 프로젝트 카드 한 개를 렌더링한다.
 *
 * @param item 카드에 표시할 프로젝트 정보
 * @param onPlayClick 문제풀이 버튼 클릭 콜백
 * @param modifier 카드의 외부 배치와 추가 수식자
 */
@Composable
private fun ProjectCard(
    item: ProjectListItem,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(12.dp))
                .background(GitItTheme.colors.grey600)
                .padding(horizontal = 18.dp, vertical = 16.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            ProjectThumbnail()
            Spacer(Modifier.width(14.dp))
            ProjectTitleBlock(
                item = item,
                modifier =
                    Modifier
                        .padding(top = 4.dp)
                        .weight(1f),
            )
            ProjectPlayButton(onClick = onPlayClick)
        }

        Spacer(Modifier.height(12.dp))
        ProjectProgressBar(progress = item.progress)
        Spacer(Modifier.height(12.dp))
        ProjectRecentSet(item = item)
    }
}

/**
 * 프로젝트 리스트 썸네일을 그린다.
 */
@Composable
fun ProjectThumbnail() {
    Box(
        modifier =
            Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black),
    ) {
        Image(
            painter = painterResource(Res.drawable.projectlist_thumbnail_base),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(brush = GitItTheme.colorStyles.gradient3, alpha = 0.2f),
        )
        Image(
            painter = painterResource(Res.drawable.projectlist_thumbnail_mark),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}

/**
 * 프로젝트 제목과 기술 스택을 표시한다.
 *
 * @param item 표시할 프로젝트 정보
 * @param modifier 텍스트 블록의 외부 배치와 추가 수식자
 */
@Composable
private fun ProjectTitleBlock(
    item: ProjectListItem,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = item.title,
            color = GitItTheme.colors.grey100,
            style = GitItTheme.typography.subtitle2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = item.techStack,
            color = GitItTheme.colors.grey400,
            style = GitItTheme.typography.caption2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * 최근 학습 세트 라벨과 제목을 표시한다.
 *
 * @param item 표시할 프로젝트 정보
 */
@Composable
private fun ProjectRecentSet(item: ProjectListItem) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(99.dp))
                    .background(GitItTheme.colors.grey500)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = item.setLabel,
                color = GitItTheme.colors.grey300,
                style = GitItTheme.typography.body3,
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = item.recentSetTitle,
            color = GitItTheme.colors.grey300,
            style = GitItTheme.typography.body2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * 프로젝트 카드의 진행률 바를 그린다.
 *
 * @param progress 진행률(0..100)
 */
@Composable
private fun ProjectProgressBar(progress: Int) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(GitItTheme.colors.grey500),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth(progress.coerceIn(0, 100) / 100f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(GitItTheme.colors.blue200),
        )
    }
}

/**
 * 프로젝트 카드의 문제풀이 재생 버튼을 그린다.
 *
 * @param onClick 문제풀이를 시작할 때 실행할 동작
 */
@Composable
private fun ProjectPlayButton(onClick: () -> Unit) {
    Image(
        painter = painterResource(Res.drawable.ic_play_project_list),
        contentDescription = stringResource(Res.string.project_play_button_description),
        modifier =
            Modifier
                .size(40.dp)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(2.dp),
    )
}

/** 프로젝트 목록의 메뉴 아이콘을 그린다. */
@Composable
private fun ProjectListMenuIcon() {
    Icon(
        painter = painterResource(DesignSystemRes.drawable.ic_menu),
        contentDescription = null,
        modifier = Modifier.size(width = 17.dp, height = 12.dp),
        tint = Color.White,
    )
}

@Preview
@Composable
private fun ProjectListScreenPreview() {
    GitItTheme {
        ProjectListScreen(
            uiState =
                ProjectListUiState(
                    projects =
                        listOf(
                            ProjectListItem(
                                id = "preview-1",
                                title = "Now in\nAndroid",
                                techStack = "Kotlin · Compose · Coroutines",
                                setLabel = "Set 1",
                                recentSetTitle = "Compose 핵심 개념",
                                progress = 65,
                            ),
                            ProjectListItem(
                                id = "preview-2",
                                title = "Now in\nAndroid",
                                techStack = "Kotlin · Compose · Coroutines",
                                setLabel = "Set 1",
                                recentSetTitle = "Compose 핵심 개념",
                                progress = 65,
                            ),
                        ),
                ),
            onIntent = {},
            showBackButton = false,
        )
    }
}

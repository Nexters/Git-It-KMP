package com.nexters.hytime.gitit.feature.projectlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.button.GitItButton
import com.nexters.hytime.gitit.designsystem.button.GitItButtonState
import com.nexters.hytime.gitit.designsystem.button.GitItButtonStyle
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassContainer
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassDropdownMenu
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassDropdownMenuItem
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButton
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButtonSize
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButtonState
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButtonVariant
import com.nexters.hytime.gitit.designsystem.liquidglass.gitItTopGradientBlur
import com.nexters.hytime.gitit.designsystem.navigation.GitItMainNavBar
import com.nexters.hytime.gitit.designsystem.navigation.GitItMainNavDestination
import com.nexters.hytime.gitit.designsystem.navigation.gitItMainNavSky
import com.nexters.hytime.gitit.designsystem.navigation.rememberGitItMainNavSky
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBar
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBarType
import git_it_kmp.core.designsystem.generated.resources.ic_menu
import git_it_kmp.feature.projectlist.generated.resources.Res
import git_it_kmp.feature.projectlist.generated.resources.ic_play_project_list
import git_it_kmp.feature.projectlist.generated.resources.project_delete
import git_it_kmp.feature.projectlist.generated.resources.project_delete_cancel
import git_it_kmp.feature.projectlist.generated.resources.project_delete_confirm
import git_it_kmp.feature.projectlist.generated.resources.project_delete_sheet_description
import git_it_kmp.feature.projectlist.generated.resources.project_delete_sheet_title
import git_it_kmp.feature.projectlist.generated.resources.project_list_empty_description
import git_it_kmp.feature.projectlist.generated.resources.project_list_empty_title
import git_it_kmp.feature.projectlist.generated.resources.project_list_title
import git_it_kmp.feature.projectlist.generated.resources.project_menu_button_description
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
 * @param isDeleteMode 프로젝트를 삭제할 수 있는 목록을 표시하는지 여부
 * @param onIntent 사용자 입력을 ViewModel로 올리는 콜백
 * @param modifier 화면의 크기와 배치를 지정할 수식자
 */
@Composable
fun ProjectListScreen(
    uiState: ProjectListUiState,
    isDeleteMode: Boolean,
    onIntent: (ProjectListIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sky = rememberGitItMainNavSky()
    var showMenu by remember { mutableStateOf(false) }
    val dismissMenuInteractionSource = remember { MutableInteractionSource() }
    val topBlurHeight = if (isDeleteMode) DELETE_MODE_TOP_BLUR_HEIGHT else TOP_BLUR_HEIGHT

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(GitItTheme.colors.grey700),
    ) {
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(GitItTheme.colors.grey700)
                    .gitItMainNavSky(sky)
                    .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = topBlurHeight, bottom = if (isDeleteMode) 34.dp else 120.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(uiState.projects, key = { it.id }) { project ->
                ProjectCard(
                    item = project,
                    isDeleteMode = isDeleteMode,
                    onClick = { onIntent(ProjectListIntent.ProjectClick(project.id)) },
                    onPlayClick = { onIntent(ProjectListIntent.PlayProjectClick(project.id)) },
                    onDeleteClick = { onIntent(ProjectListIntent.DeleteProjectClick(project.id)) },
                )
            }
        }

        if (uiState.projects.isEmpty()) {
            ProjectListEmptyState(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 20.dp),
            )
        }

        Column(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(topBlurHeight)
                    .gitItTopGradientBlur(sky)
                    .statusBarsPadding(),
        ) {
            GitItTopBar(
                type = if (isDeleteMode) GitItTopBarType.LargeTitle else GitItTopBarType.InlineTitle,
                title =
                    stringResource(
                        if (isDeleteMode) Res.string.project_delete else Res.string.project_list_title,
                    ),
                modifier = Modifier.padding(top = 8.dp),
                sky = sky,
                onBackClick =
                    if (isDeleteMode) {
                        { onIntent(ProjectListIntent.DeleteModeBackClick) }
                    } else {
                        null
                    },
                actions = {
                    if (!isDeleteMode) {
                        val menuButton: @Composable () -> Unit = {
                            GitItLiquidGlassIconButton(
                                onClick = { showMenu = !showMenu },
                                size = GitItLiquidGlassIconButtonSize.Md,
                                variant = GitItLiquidGlassIconButtonVariant.Secondary,
                            ) {
                                ProjectListMenuIcon(
                                    contentDescription = stringResource(Res.string.project_menu_button_description),
                                )
                            }
                        }
                        if (sky == null) {
                            menuButton()
                        } else {
                            GitItLiquidGlassContainer(sky = sky) { menuButton() }
                        }
                    }
                },
            )
        }

        if (!isDeleteMode) {
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

        if (showMenu) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = dismissMenuInteractionSource,
                            indication = null,
                            onClick = { showMenu = false },
                        ),
            )
            GitItLiquidGlassDropdownMenu(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 102.dp, end = 20.dp)
                        .width(160.dp),
                sky = sky,
            ) {
                GitItLiquidGlassDropdownMenuItem(
                    text = stringResource(Res.string.project_delete),
                    onClick = {
                        showMenu = false
                        onIntent(ProjectListIntent.DeleteMenuClick)
                    },
                )
            }
        }

        if (isDeleteMode) {
            uiState.pendingDeleteProjectId
                ?.let { projectId -> uiState.projects.find { it.id == projectId } }
                ?.let {
                    ProjectDeleteSheet(
                        onConfirm = { onIntent(ProjectListIntent.ConfirmDeleteClick) },
                        onDismiss = { onIntent(ProjectListIntent.DismissDeleteClick) },
                    )
                }
        }
    }
}

/** Figma 상단 dim과 동일한 점진 블러 영역 높이. */
private val TOP_BLUR_HEIGHT = 103.dp

/** Figma 프로젝트 삭제 화면의 상단 dim 영역 높이. */
private val DELETE_MODE_TOP_BLUR_HEIGHT = 151.dp

/**
 * 등록된 프로젝트가 없을 때 안내 문구를 표시한다.
 *
 * @param modifier 외부 배치와 추가 수식자
 */
@Composable
private fun ProjectListEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.project_list_empty_title),
            color = GitItTheme.colors.grey200,
            style = GitItTheme.typography.subtitle1,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(Res.string.project_list_empty_description),
            color = GitItTheme.colors.grey400,
            style = GitItTheme.typography.body2,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * 프로젝트 카드 한 개를 렌더링한다.
 *
 * @param item 카드에 표시할 프로젝트 정보
 * @param isDeleteMode 진행 정보 대신 삭제 버튼을 표시하는지 여부
 * @param onClick 프로젝트 상세 화면 이동을 요청하는 콜백
 * @param onPlayClick 문제풀이 버튼 클릭 콜백
 * @param onDeleteClick 프로젝트 삭제 버튼 클릭 콜백
 * @param modifier 카드의 외부 배치와 추가 수식자
 */
@Composable
private fun ProjectCard(
    item: ProjectListItem,
    isDeleteMode: Boolean,
    onClick: () -> Unit,
    onPlayClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(12.dp))
                .background(GitItTheme.colors.grey600)
                .then(if (isDeleteMode) Modifier else Modifier.clickable(role = Role.Button, onClick = onClick))
                .padding(start = 18.dp, top = 16.dp, end = 18.dp, bottom = if (isDeleteMode) 18.dp else 16.dp),
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
            if (isDeleteMode) {
                ProjectDeleteButton(onClick = onDeleteClick)
            } else {
                ProjectPlayButton(onClick = onPlayClick)
            }
        }

        if (!isDeleteMode) {
            Spacer(Modifier.height(12.dp))
            ProjectProgressBar(progress = item.progress)
            Spacer(Modifier.height(12.dp))
            ProjectRecentSet(item = item)
        }
    }
}

/**
 * 프로젝트 삭제 화면의 마이너스 버튼을 그린다.
 *
 * @param onClick 프로젝트 삭제를 요청할 때 실행할 동작
 */
@Composable
private fun ProjectDeleteButton(onClick: () -> Unit) {
    val deleteDescription = stringResource(Res.string.project_delete)

    GitItLiquidGlassIconButton(
        onClick = onClick,
        modifier = Modifier.semantics { contentDescription = deleteDescription },
        size = GitItLiquidGlassIconButtonSize.Md,
        variant = GitItLiquidGlassIconButtonVariant.Secondary,
        state = GitItLiquidGlassIconButtonState.Error,
    ) {
        Box(
            modifier =
                Modifier
                    .size(width = 14.dp, height = 2.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(GitItTheme.colors.error),
        )
    }
}

/**
 * 프로젝트 리스트 썸네일을 그린다.
 *
 * @param modifier 썸네일의 크기와 외부 배치를 지정할 수식자
 * @param size 썸네일의 가로와 세로 길이
 */
@Composable
fun ProjectThumbnail(
    modifier: Modifier = Modifier,
    size: Dp = 60.dp,
) {
    Box(
        modifier =
            modifier
                .size(size)
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
 * 프로젝트 삭제를 확인하는 Figma 바텀시트를 표시한다.
 *
 * @param onConfirm 삭제 확인 시 실행할 동작
 * @param onDismiss 취소 또는 시트 바깥 영역 선택 시 실행할 동작
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectDeleteSheet(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(),
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = GitItTheme.colors.grey600,
        contentColor = GitItTheme.colors.grey100,
        scrimColor = GitItTheme.colors.black70,
        tonalElevation = 0.dp,
        dragHandle = { ProjectDeleteSheetDragHandle() },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(22.dp))
            ProjectThumbnail(size = 128.dp)
            Spacer(Modifier.height(22.dp))
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(Res.string.project_delete_sheet_title),
                    color = GitItTheme.colors.grey100,
                    style = GitItTheme.typography.subtitle1.copy(fontSize = 22.sp, lineHeight = 32.56.sp),
                )
                Text(
                    text = stringResource(Res.string.project_delete_sheet_description),
                    color = GitItTheme.colors.grey400,
                    style = GitItTheme.typography.body2,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(Modifier.height(24.dp))
            GitItButton(
                text = stringResource(Res.string.project_delete_confirm),
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                style = GitItButtonStyle.Primary,
                state = GitItButtonState.Error,
            )
            Spacer(Modifier.height(8.dp))
            GitItButton(
                text = stringResource(Res.string.project_delete_cancel),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                style = GitItButtonStyle.Text,
            )
        }
    }
}

/** Figma 바텀시트 상단의 58×4dp grabber를 그린다. */
@Composable
private fun ProjectDeleteSheetDragHandle() {
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

/**
 * 프로젝트 목록의 메뉴 아이콘을 그린다.
 *
 * @param contentDescription 스크린 리더에 전달할 메뉴 버튼 설명
 */
@Composable
private fun ProjectListMenuIcon(contentDescription: String) {
    Icon(
        painter = painterResource(DesignSystemRes.drawable.ic_menu),
        contentDescription = contentDescription,
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
            isDeleteMode = false,
            onIntent = {},
        )
    }
}

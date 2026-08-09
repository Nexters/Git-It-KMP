package com.nexters.hytime.gitit.feature.projectdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButton
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButtonSize
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButtonVariant
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBar
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBarType

/**
 * 프로젝트 상세 화면의 순수 UI 영역이다. 상태와 콜백만 주입받아 상태를 소유하지 않는다.
 *
 * @param uiState 단일 UI 상태
 * @param onBackClick 뒤로가기 콜백
 * @param onMoreMenuClick 더보기 메뉴 토글 콜백
 * @param onDismissMoreMenu 더보기 메뉴 닫기 콜백
 * @param onSavedQuestionsClick 저장한 문제 콜백
 * @param onQuestionSolvingClick 문제풀이 바로가기 콜백
 * @param onRepoLinkClick 레포 바로가기 콜백
 * @param onDeleteProjectClick 삭제 콜백
 * @param onLearningSetClick 학습 세트 진입 콜백
 * @param onReviewStartClick 복습 시작 콜백
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
    onRepoLinkClick: () -> Unit,
    onDeleteProjectClick: () -> Unit,
    onLearningSetClick: (String) -> Unit,
    onReviewStartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(GitItTheme.colors.grey700),
    ) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            ProjectDetailTopBar(
                onBackClick = onBackClick,
                onMoreClick = onMoreMenuClick,
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
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
                item {
                    Spacer(Modifier.height(4.dp))
                    ReviewCard(onReviewStartClick = onReviewStartClick)
                }
            }
        }

        ProjectDetailMoreMenu(
            expanded = uiState.showMoreMenu,
            onDismiss = onDismissMoreMenu,
            onSavedQuestionsClick = onSavedQuestionsClick,
            onRepoLinkClick = onRepoLinkClick,
            onDeleteProjectClick = onDeleteProjectClick,
        )
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
 * 헤더 영역: 썸네일·칩·제목·참여자/기술스택·문제풀이 바로가기 버튼을 배치한다.
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

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
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
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            CategoryChip(text = project.category, isCategory = true)
            CategoryChip(text = project.difficulty, isCategory = false)
        }
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
                UsersIcon(modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(5.dp))
                Text(
                    text = "${project.memberCount}명",
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
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(GitItTheme.colors.grey100)
                    .clickable(onClick = onQuestionSolvingClick),
            contentAlignment = Alignment.Center,
        ) {
            // TODO: 재생버튼 아이콘 리소스 추가 후 교체한다.
            PlayIcon(
                modifier = Modifier.size(20.dp),
                color = GitItTheme.colors.grey700,
            )
        }
    }
}

/**
 * 카테고리/난이도 칩이다.
 *
 * @param text 칩 텍스트
 * @param isCategory true면 blue400 배경(카테고리), false면 grey500 배경(난이도)
 */
@Composable
private fun CategoryChip(
    text: String,
    isCategory: Boolean,
) {
    val background = if (isCategory) GitItTheme.colors.blue400 else GitItTheme.colors.grey500
    Box(
        modifier =
            Modifier
                .height(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(background)
                .padding(horizontal = if (isCategory) 9.dp else 7.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = GitItTheme.colors.blue100,
            style = GitItTheme.typography.body3,
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
            Box(
                modifier =
                    Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(GitItTheme.colors.blue400),
                contentAlignment = Alignment.Center,
            ) {
                PlayIcon(
                    modifier = Modifier.size(16.dp),
                    color = GitItTheme.colors.blue100,
                )
            }
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
 * 복습 카드. 제목·설명·"복습 시작" 버튼을 배치한다.
 *
 * @param onReviewStartClick 복습 시작 콜백
 */
@Composable
private fun ReviewCard(onReviewStartClick: () -> Unit) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(GitItTheme.colors.blue400)
                .padding(horizontal = 14.dp, vertical = 18.dp),
    ) {
        Text(
            text = "아이디어 PT 오답 복습",
            color = GitItTheme.colors.blue100,
            style = GitItTheme.typography.subtitle3,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "해당 프로젝트의 모든 세트에서 학습한 내용을 복습하여\n학습을 완료하세요.",
            color = GitItTheme.colors.grey200,
            style = GitItTheme.typography.caption1,
        )
        Spacer(Modifier.height(18.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(GitItTheme.colors.blue100)
                    .clickable(onClick = onReviewStartClick)
                    .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "복습 시작",
                color = GitItTheme.colors.grey700,
                style = GitItTheme.typography.body2,
            )
        }
    }
}

/**
 * 더보기 드롭다운 메뉴.
 *
 * @param expanded 메뉴 노출 여부
 * @param onDismiss 닫기 콜백
 * @param onSavedQuestionsClick 저장한 문제 콜백
 * @param onRepoLinkClick 레포 바로가기 콜백
 * @param onDeleteProjectClick 삭제 콜백
 */
@Composable
private fun ProjectDetailMoreMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onSavedQuestionsClick: () -> Unit,
    onRepoLinkClick: () -> Unit,
    onDeleteProjectClick: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier.background(GitItTheme.colors.grey600),
    ) {
        MoreMenuItem(text = "저장한 문제", onClick = onSavedQuestionsClick)
        MoreMenuItem(text = "레포 바로가기", onClick = onRepoLinkClick)
        MoreMenuItem(
            text = "삭제하기",
            color = GitItTheme.colors.grey300,
            onClick = onDeleteProjectClick,
        )
    }
}

/**
 * 더보기 메뉴의 개별 항목.
 *
 * @param text 항목 텍스트
 * @param color 텍스트 색상
 * @param onClick 클릭 콜백
 */
@Composable
private fun MoreMenuItem(
    text: String,
    color: Color = GitItTheme.colors.grey100,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Text(text = text, color = color, style = GitItTheme.typography.body3)
    }
}

@Composable
private fun MenuIcon() {
    androidx.compose.foundation.Canvas(modifier = Modifier.size(20.dp)) {
        val w = size.width
        val y1 = size.height * 0.3f
        val y2 = size.height * 0.5f
        val y3 = size.height * 0.7f
        drawLine(
            Color.White,
            start =
                androidx.compose.ui.geometry
                    .Offset(w * 0.25f, y1),
            end =
                androidx.compose.ui.geometry
                    .Offset(w * 0.75f, y1),
            strokeWidth = 2.dp.toPx(),
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
        )
        drawLine(
            Color.White,
            start =
                androidx.compose.ui.geometry
                    .Offset(w * 0.25f, y2),
            end =
                androidx.compose.ui.geometry
                    .Offset(w * 0.75f, y2),
            strokeWidth = 2.dp.toPx(),
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
        )
        drawLine(
            Color.White,
            start =
                androidx.compose.ui.geometry
                    .Offset(w * 0.25f, y3),
            end =
                androidx.compose.ui.geometry
                    .Offset(w * 0.75f, y3),
            strokeWidth = 2.dp.toPx(),
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
        )
    }
}

@Composable
private fun UsersIcon(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val r = size.minDimension * 0.22f
        drawCircle(
            Color.White,
            radius = r,
            center =
                androidx.compose.ui.geometry
                    .Offset(size.width * 0.35f, size.height * 0.4f),
        )
        drawCircle(
            Color.White,
            radius = r * 0.7f,
            center =
                androidx.compose.ui.geometry
                    .Offset(size.width * 0.7f, size.height * 0.5f),
        )
    }
}

/**
 * 재생 버튼에 사용하는 삼각형 아이콘을 그린다.
 *
 * @param modifier 아이콘의 크기와 배치를 지정할 수식자
 * @param color 아이콘 색상
 */
@Composable
private fun PlayIcon(
    modifier: Modifier = Modifier.size(12.dp),
    color: Color = Color.White,
) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val path =
            Path().apply {
                moveTo(size.width * 0.32f, size.height * 0.18f)
                lineTo(size.width * 0.84f, size.height * 0.5f)
                lineTo(size.width * 0.32f, size.height * 0.82f)
                close()
            }
        drawPath(path = path, color = color)
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
                            category = "Back-end",
                            difficulty = "입문",
                            memberCount = 13,
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
            onRepoLinkClick = {},
            onDeleteProjectClick = {},
            onLearningSetClick = {},
            onReviewStartClick = {},
        )
    }
}

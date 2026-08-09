package com.nexters.hytime.gitit.feature.bookmark

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.navigation.GitItMainNavBar
import com.nexters.hytime.gitit.designsystem.navigation.GitItMainNavDestination
import com.skydoves.cloudy.rememberSky
import com.skydoves.cloudy.sky

/**
 * 저장한 문제 화면의 순수 UI 영역이다.
 *
 * @param uiState 화면에 표시할 저장한 문제 상태
 * @param onIntent 사용자 입력을 ViewModel로 올리는 콜백
 * @param modifier 화면의 크기와 배치를 지정할 수식자
 */
@Composable
fun BookmarkScreen(
    uiState: BookmarkUiState,
    onIntent: (BookmarkIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sky = rememberSky()

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
                    .sky(sky)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 18.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Text(
                    text = "저장한 문제",
                    color = GitItTheme.colors.grey100,
                    style = GitItTheme.typography.subtitle1,
                )
                Spacer(Modifier.height(16.dp))
                BookmarkFilterRow(
                    filters = uiState.filters,
                    selectedFilterId = uiState.selectedFilterId,
                    onFilterClick = { filterId -> onIntent(BookmarkIntent.FilterClick(filterId)) },
                )
                Spacer(Modifier.height(22.dp))
                Text(
                    text = "${uiState.questions.size}개",
                    color = GitItTheme.colors.grey400,
                    style = GitItTheme.typography.body2,
                )
            }

            items(uiState.questions, key = { it.id }) { question ->
                BookmarkedQuestionCard(
                    question = question,
                    onBookmarkClick = { onIntent(BookmarkIntent.BookmarkClick(question.id)) },
                    onExplanationClick = { onIntent(BookmarkIntent.ExplanationClick(question.id)) },
                    onSolveClick = { onIntent(BookmarkIntent.SolveClick(question.id)) },
                )
            }
        }

        GitItMainNavBar(
            selectedDestination = GitItMainNavDestination.Saved,
            onDestinationClick = { destination ->
                when (destination) {
                    GitItMainNavDestination.Home -> onIntent(BookmarkIntent.HomeTabClick)
                    GitItMainNavDestination.Project -> onIntent(BookmarkIntent.ProjectTabClick)
                    GitItMainNavDestination.Saved -> onIntent(BookmarkIntent.SavedTabClick)
                    GitItMainNavDestination.My -> onIntent(BookmarkIntent.MyTabClick)
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
 * 저장한 문제 필터 목록을 렌더링한다.
 *
 * @param filters 표시할 필터 목록
 * @param selectedFilterId 현재 선택된 필터 식별자
 * @param onFilterClick 필터 선택 콜백
 * @param modifier 외부 배치와 추가 수식자
 */
@Composable
private fun BookmarkFilterRow(
    filters: List<BookmarkFilter>,
    selectedFilterId: String,
    onFilterClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        items(filters, key = { it.id }) { filter ->
            BookmarkFilterChip(
                filter = filter,
                selected = filter.id == selectedFilterId,
                onClick = { onFilterClick(filter.id) },
            )
        }
    }
}

/**
 * 저장한 문제 필터 칩 한 개를 표시한다.
 *
 * @param filter 표시할 필터
 * @param selected 선택 여부
 * @param onClick 필터 선택 콜백
 * @param modifier 외부 배치와 추가 수식자
 */
@Composable
private fun BookmarkFilterChip(
    filter: BookmarkFilter,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .height(27.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(if (selected) GitItTheme.colors.blue100 else GitItTheme.colors.grey500)
                .clickable(onClick = onClick)
                .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = filter.label,
            color = if (selected) GitItTheme.colors.grey700 else GitItTheme.colors.grey300,
            style = GitItTheme.typography.body2,
            maxLines = 1,
        )
    }
}

/**
 * 저장한 문제 카드 한 개를 렌더링한다.
 *
 * @param question 표시할 저장 문제
 * @param onBookmarkClick 북마크 버튼 선택 콜백
 * @param onExplanationClick 해설 보기 선택 콜백
 * @param onSolveClick 문제 풀기 선택 콜백
 * @param modifier 외부 배치와 추가 수식자
 */
@Composable
private fun BookmarkedQuestionCard(
    question: BookmarkedQuestion,
    onBookmarkClick: () -> Unit,
    onExplanationClick: () -> Unit,
    onSolveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .height(172.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(GitItTheme.colors.grey600)
                .padding(start = 16.dp, top = 17.dp, end = 16.dp, bottom = 14.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = question.meta,
                color = GitItTheme.colors.grey400,
                style = GitItTheme.typography.caption1,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            BookmarkIcon(
                modifier =
                    Modifier
                        .size(22.dp)
                        .clickable(onClick = onBookmarkClick),
            )
        }
        Spacer(Modifier.height(9.dp))
        Text(
            text = question.title,
            color = GitItTheme.colors.grey100,
            style = GitItTheme.typography.subtitle3,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            BookmarkActionButton(
                label = "해설보기",
                onClick = onExplanationClick,
                primary = false,
            )
            Spacer(Modifier.size(6.dp))
            BookmarkActionButton(
                label = "문제 풀기",
                onClick = onSolveClick,
                primary = true,
            )
        }
    }
}

/**
 * 저장한 문제 카드의 액션 버튼을 렌더링한다.
 *
 * @param label 버튼 라벨
 * @param onClick 버튼 선택 콜백
 * @param primary 강조 버튼 여부
 * @param modifier 외부 배치와 추가 수식자
 */
@Composable
private fun BookmarkActionButton(
    label: String,
    onClick: () -> Unit,
    primary: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(width = 82.dp, height = 30.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (primary) GitItTheme.colors.purple100 else GitItTheme.colors.grey500)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (primary) GitItTheme.colors.grey700 else GitItTheme.colors.grey300,
            style = GitItTheme.typography.body3,
        )
    }
}

/**
 * 저장됨 상태의 북마크 아이콘을 그린다.
 *
 * @param modifier 외부 배치와 추가 수식자
 */
@Composable
private fun BookmarkIcon(modifier: Modifier = Modifier) {
    val color = GitItTheme.colors.blue100

    Canvas(modifier = modifier) {
        val path =
            Path().apply {
                moveTo(size.width * 0.28f, size.height * 0.15f)
                lineTo(size.width * 0.72f, size.height * 0.15f)
                lineTo(size.width * 0.72f, size.height * 0.85f)
                lineTo(size.width * 0.5f, size.height * 0.68f)
                lineTo(size.width * 0.28f, size.height * 0.85f)
                close()
            }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.28f, size.height * 0.15f),
            end = Offset(size.width * 0.72f, size.height * 0.15f),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Preview
@Composable
private fun BookmarkScreenPreview() {
    GitItTheme {
        BookmarkScreen(
            uiState =
                BookmarkUiState(
                    filters =
                        listOf(
                            BookmarkFilter(id = "all", label = "전체"),
                            BookmarkFilter(id = "flask", label = "Flask"),
                            BookmarkFilter(id = "android", label = "Now in Android"),
                        ),
                    selectedFilterId = "all",
                    questions =
                        List(4) { index ->
                            BookmarkedQuestion(
                                id = "bookmark-$index",
                                meta = "Android · Set2 · 문제 1",
                                title = "sansio/blueprints.py에 정의된 BlueprintSetupState 클래스는 어떤 목적을 가진 개체인가?",
                            )
                        },
                ),
            onIntent = {},
        )
    }
}

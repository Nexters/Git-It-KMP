package com.nexters.hytime.gitit.feature.bookmark

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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.animation.GitItAnimation
import com.nexters.hytime.gitit.designsystem.animation.GitItLottieAnimation
import com.nexters.hytime.gitit.designsystem.navigation.GitItBookmarkIcon
import com.nexters.hytime.gitit.designsystem.navigation.GitItMainNavBar
import com.nexters.hytime.gitit.designsystem.navigation.GitItMainNavDestination
import com.nexters.hytime.gitit.designsystem.navigation.gitItMainNavSky
import com.nexters.hytime.gitit.designsystem.navigation.rememberGitItMainNavSky
import git_it_kmp.feature.bookmark.generated.resources.Res
import git_it_kmp.feature.bookmark.generated.resources.bookmark_filter_all
import git_it_kmp.feature.bookmark.generated.resources.bookmark_question_meta
import org.jetbrains.compose.resources.stringResource

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
    val sky = rememberGitItMainNavSky()

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
                if (uiState.questions.isNotEmpty()) {
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
            }

            items(uiState.questions, key = { it.id }) { question ->
                BookmarkedQuestionCard(
                    question = question,
                    isBookmarked = uiState.bookmarkChanges[question.id] ?: true,
                    onBookmarkClick = { onIntent(BookmarkIntent.BookmarkClick(question.id)) },
                    onSolveClick = { onIntent(BookmarkIntent.SolveClick(question.id)) },
                )
            }
        }

        if (uiState.questions.isEmpty()) {
            BookmarkEmptyState(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 20.dp),
            )
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
 * 저장한 문제가 없을 때 안내 문구를 표시한다.
 *
 * @param modifier 외부 배치와 추가 수식자
 */
@Composable
private fun BookmarkEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GitItLottieAnimation(
            animation = GitItAnimation.StorageEmpty,
            modifier = Modifier.size(128.dp),
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Nothing saved yet.",
            color = GitItTheme.colors.grey200,
            style = GitItTheme.typography.subtitle1,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "아직 저장한 문제가 없네요!\n다시 확인하고 싶은 문제를 저장해 보세요.",
            color = GitItTheme.colors.grey400,
            style = GitItTheme.typography.body2,
            textAlign = TextAlign.Center,
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
            text = filter.label ?: stringResource(Res.string.bookmark_filter_all),
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
 * @param isBookmarked 현재 문제의 북마크 상태
 * @param onBookmarkClick 북마크 버튼 선택 콜백
 * @param onSolveClick 문제 풀기 선택 콜백
 * @param modifier 외부 배치와 추가 수식자
 */
@Composable
private fun BookmarkedQuestionCard(
    question: BookmarkedQuestion,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
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
        Text(
            text =
                stringResource(
                    Res.string.bookmark_question_meta,
                    question.projectName,
                    question.setLabel,
                    question.problemNumber,
                ),
            color = GitItTheme.colors.grey400,
            style = GitItTheme.typography.caption1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
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
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BookmarkIcon(
                filled = isBookmarked,
                modifier =
                    Modifier
                        .size(22.dp)
                        .clickable(onClick = onBookmarkClick),
            )
            Spacer(Modifier.weight(1f))
            BookmarkActionButton(
                label = "문제 풀기",
                onClick = onSolveClick,
            )
        }
    }
}

/**
 * 저장한 문제 카드의 액션 버튼을 렌더링한다.
 *
 * @param label 버튼 라벨
 * @param onClick 버튼 선택 콜백
 * @param modifier 외부 배치와 추가 수식자
 */
@Composable
private fun BookmarkActionButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(width = 82.dp, height = 30.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(GitItTheme.colors.purple100)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = GitItTheme.colors.grey700,
            style = GitItTheme.typography.body3,
        )
    }
}

/**
 * 현재 저장 상태에 맞는 북마크 아이콘을 그린다.
 *
 * @param filled 현재 문제가 저장된 상태인지 여부
 * @param modifier 외부 배치와 추가 수식자
 */
@Composable
private fun BookmarkIcon(
    filled: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalContentColor provides GitItTheme.colors.blue100) {
            GitItBookmarkIcon(filled = filled)
        }
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
                            BookmarkFilter(id = "all", label = null),
                            BookmarkFilter(id = "flask", label = "Flask"),
                            BookmarkFilter(id = "android", label = "Now in Android"),
                        ),
                    selectedFilterId = "all",
                    questions =
                        List(4) { index ->
                            BookmarkedQuestion(
                                id = "bookmark-$index",
                                projectName = "Android",
                                setLabel = "Set2",
                                problemNumber = 1,
                                title = "sansio/blueprints.py에 정의된 BlueprintSetupState 클래스는 어떤 목적을 가진 개체인가?",
                            )
                        },
                ),
            onIntent = {},
        )
    }
}

package com.nexters.hytime.gitit.feature.my

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.navigation.GitItMainNavBar
import com.nexters.hytime.gitit.designsystem.navigation.GitItMainNavDestination
import com.nexters.hytime.gitit.designsystem.navigation.gitItMainNavSky
import com.nexters.hytime.gitit.designsystem.navigation.rememberGitItMainNavSky
import git_it_kmp.feature.my.generated.resources.Res
import git_it_kmp.feature.my.generated.resources.my_default_avatar
import org.jetbrains.compose.resources.painterResource

/**
 * 마이 학습 화면의 순수 UI 영역이다.
 *
 * @param uiState 화면에 표시할 마이 학습 상태
 * @param onIntent 사용자 입력을 ViewModel로 올리는 콜백
 * @param modifier 화면의 크기와 배치를 지정할 수식자
 */
@Composable
fun MyScreen(
    uiState: MyUiState,
    onIntent: (MyIntent) -> Unit,
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
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(24.dp))
            MyProfileHeader(profile = uiState.profile)
            Spacer(Modifier.height(29.dp))

            Text(
                text = "학습 현황",
                color = GitItTheme.colors.grey100,
                style = GitItTheme.typography.subtitle2,
            )
            Spacer(Modifier.height(12.dp))

            MyStatsCard(stats = uiState.stats)
            Spacer(Modifier.height(12.dp))
            WeeklyStudyCard(items = uiState.weeklyStudy)
        }

        GitItMainNavBar(
            selectedDestination = GitItMainNavDestination.My,
            onDestinationClick = { destination ->
                when (destination) {
                    GitItMainNavDestination.Home -> onIntent(MyIntent.HomeTabClick)
                    GitItMainNavDestination.Project -> onIntent(MyIntent.ProjectTabClick)
                    GitItMainNavDestination.Saved -> onIntent(MyIntent.SavedTabClick)
                    GitItMainNavDestination.My -> onIntent(MyIntent.MyTabClick)
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
 * 마이 화면 상단의 사용자 프로필을 표시한다.
 *
 * @param profile 표시할 사용자 프로필
 * @param modifier 외부 배치와 추가 수식자
 */
@Composable
private fun MyProfileHeader(
    profile: MyProfile,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AvatarIcon()
        Spacer(Modifier.size(11.dp))
        Column {
            Text(
                text = profile.name,
                color = GitItTheme.colors.grey100,
                style = GitItTheme.typography.subtitle3,
            )
            Text(
                text = profile.role,
                color = GitItTheme.colors.grey400,
                style = GitItTheme.typography.body3,
            )
        }
    }
}

/**
 * 기본 사용자 아바타 아이콘을 그린다.
 *
 * @param modifier 외부 배치와 추가 수식자
 */
@Composable
private fun AvatarIcon(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(Res.drawable.my_default_avatar),
        contentDescription = null,
        modifier = modifier.size(40.dp),
    )
}

/**
 * 학습 현황 요약 카드를 렌더링한다.
 *
 * @param stats 표시할 학습 요약 항목
 * @param modifier 외부 배치와 추가 수식자
 */
@Composable
private fun MyStatsCard(
    stats: List<MyStudyStat>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(88.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Brush.horizontalGradient(
                        colors =
                            listOf(
                                GitItTheme.colors.blue500,
                                GitItTheme.colors.blue500.copy(alpha = 0.5f),
                            ),
                    ),
                ).border(1.dp, GitItTheme.colors.blue400, RoundedCornerShape(10.dp))
                .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        stats.forEach { stat ->
            MyStatItem(stat = stat)
        }
    }
}

/**
 * 학습 현황 요약 항목 한 개를 표시한다.
 *
 * @param stat 표시할 학습 요약 항목
 * @param modifier 외부 배치와 추가 수식자
 */
@Composable
private fun MyStatItem(
    stat: MyStudyStat,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.width(90.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stat.label,
            color = GitItTheme.colors.grey300,
            style = GitItTheme.typography.caption2,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stat.value,
            color = GitItTheme.colors.blue100,
            style = GitItTheme.typography.subtitle3,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * 주간 학습량 막대 차트를 표시한다.
 *
 * @param items 요일별 학습량
 * @param modifier 외부 배치와 추가 수식자
 */
@Composable
private fun WeeklyStudyCard(
    items: List<MyWeeklyStudy>,
    modifier: Modifier = Modifier,
) {
    val axisMax = weeklyStudyAxisMax(items.maxOfOrNull(MyWeeklyStudy::solvedCount) ?: 0)

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .height(216.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(GitItTheme.colors.grey600)
                .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 12.dp),
    ) {
        Text(
            text = "학습한 문제 수 (일별)",
            color = GitItTheme.colors.grey400,
            style = GitItTheme.typography.body2,
        )
        Spacer(Modifier.height(18.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            WeeklyStudyYAxis(axisMax = axisMax)
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(start = 10.dp, top = 8.dp, end = 4.dp),
            ) {
                WeeklyStudyPlot(items = items, axisMax = axisMax)
                Spacer(Modifier.height(7.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    items.forEach { item ->
                        Text(
                            text = item.day,
                            color = GitItTheme.colors.grey400,
                            style = GitItTheme.typography.body3,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(24.dp),
                        )
                    }
                }
            }
        }
    }
}

/**
 * 주간 학습량 차트의 세로축 눈금을 표시한다.
 *
 * @param axisMax 세로축에 표시할 최댓값
 * @param modifier 외부 배치와 추가 수식자
 */
@Composable
private fun WeeklyStudyYAxis(
    axisMax: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.height(120.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        (4 downTo 0).forEach { index ->
            Text(
                text = (axisMax * index / 4).toString(),
                color = GitItTheme.colors.grey400,
                style = GitItTheme.typography.body3,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier.width(24.dp),
            )
        }
    }
}

/**
 * 주간 학습량 막대와 가로 눈금선을 표시한다.
 *
 * @param items 요일별 학습량
 * @param axisMax 막대 높이 계산에 사용할 세로축 최댓값
 * @param modifier 외부 배치와 추가 수식자
 */
@Composable
private fun WeeklyStudyPlot(
    items: List<MyWeeklyStudy>,
    axisMax: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(104.dp),
    ) {
        val gridColor = GitItTheme.colors.grey500
        Canvas(modifier = Modifier.fillMaxSize()) {
            repeat(5) { index ->
                val y = size.height * index / 4
                drawLine(gridColor, Offset(0f, y), Offset(size.width, y))
            }
        }
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            items.forEach { item ->
                Box(
                    modifier =
                        Modifier
                            .width(24.dp)
                            .height((88f * item.solvedCount.coerceAtLeast(0) / axisMax).dp)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(GitItTheme.colors.blue100),
                )
            }
        }
    }
}

/**
 * 주간 최댓값을 모두 표시할 수 있도록 세로축 최댓값을 20 단위로 계산한다.
 *
 * @param maxSolvedCount 주간 일별 학습량 중 최댓값
 * @return 최소 20이며 [maxSolvedCount] 이상인 가장 작은 20의 배수
 */
internal fun weeklyStudyAxisMax(maxSolvedCount: Int): Int = maxOf(20, (maxSolvedCount.coerceAtLeast(0) + 19) / 20 * 20)

@Preview
@Composable
private fun MyScreenPreview() {
    GitItTheme {
        MyScreen(
            uiState =
                MyUiState(
                    profile = MyProfile(name = "김이박", role = "Junior Developer"),
                    stats =
                        listOf(
                            MyStudyStat(label = "이번 주", value = "13문제"),
                            MyStudyStat(label = "이번 달", value = "47문제"),
                            MyStudyStat(label = "연속 학습", value = "7일"),
                        ),
                    weeklyStudy =
                        listOf(
                            MyWeeklyStudy(day = "수", solvedCount = 18),
                            MyWeeklyStudy(day = "목", solvedCount = 11),
                            MyWeeklyStudy(day = "금", solvedCount = 14),
                            MyWeeklyStudy(day = "토", solvedCount = 11),
                            MyWeeklyStudy(day = "일", solvedCount = 18),
                            MyWeeklyStudy(day = "월", solvedCount = 14),
                            MyWeeklyStudy(day = "화", solvedCount = 8),
                        ),
                ),
            onIntent = {},
        )
    }
}

package com.nexters.hytime.gitit.feature.my

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.navigation.GitItMainNavBar
import com.nexters.hytime.gitit.designsystem.navigation.GitItMainNavDestination
import com.skydoves.cloudy.rememberSky
import com.skydoves.cloudy.sky

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
    val sky = rememberSky()

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
                    .sky(sky)
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
            Spacer(Modifier.height(14.dp))

            MyStatsCard(stats = uiState.stats)
            Spacer(Modifier.height(14.dp))
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
    val background = GitItTheme.colors.grey300
    val foreground = GitItTheme.colors.grey700

    Canvas(modifier = modifier.size(40.dp)) {
        drawCircle(color = background)
        drawCircle(
            color = foreground,
            radius = size.minDimension * 0.15f,
            center = Offset(size.width * 0.5f, size.height * 0.38f),
        )
        drawArc(
            color = foreground,
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(size.width * 0.26f, size.height * 0.55f),
            size = Size(size.width * 0.48f, size.height * 0.32f),
            style = Stroke(width = 4.dp.toPx()),
        )
    }
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
                .height(69.dp)
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
                .padding(horizontal = 26.dp),
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
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stat.label,
            color = GitItTheme.colors.grey300,
            style = GitItTheme.typography.caption2,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(2.dp))
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
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(GitItTheme.colors.grey600)
                .padding(start = 27.dp, top = 20.dp, end = 27.dp, bottom = 11.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        items.forEach { item ->
            WeeklyStudyBar(item = item)
        }
    }
}

/**
 * 주간 학습량 막대 한 개를 표시한다.
 *
 * @param item 표시할 요일별 학습량
 * @param modifier 외부 배치와 추가 수식자
 */
@Composable
private fun WeeklyStudyBar(
    item: MyWeeklyStudy,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Box(
            modifier =
                Modifier
                    .size(width = 26.dp, height = (88 * item.progress.coerceIn(0, 100) / 100).dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(GitItTheme.colors.blue100),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = item.day,
            color = GitItTheme.colors.grey400,
            style = GitItTheme.typography.caption2,
        )
    }
}

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
                            MyStudyStat(label = "푼 문제", value = "100문제"),
                            MyStudyStat(label = "푼 세트", value = "5세트"),
                            MyStudyStat(label = "복습한 문제", value = "15문제"),
                        ),
                    weeklyStudy =
                        listOf(
                            MyWeeklyStudy(day = "수", progress = 100),
                            MyWeeklyStudy(day = "목", progress = 58),
                            MyWeeklyStudy(day = "금", progress = 76),
                            MyWeeklyStudy(day = "토", progress = 58),
                            MyWeeklyStudy(day = "일", progress = 100),
                            MyWeeklyStudy(day = "월", progress = 76),
                            MyWeeklyStudy(day = "화", progress = 41),
                        ),
                ),
            onIntent = {},
        )
    }
}

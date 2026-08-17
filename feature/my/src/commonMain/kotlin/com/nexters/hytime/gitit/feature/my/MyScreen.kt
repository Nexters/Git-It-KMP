package com.nexters.hytime.gitit.feature.my

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassContainer
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButton
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButtonSize
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButtonVariant
import com.nexters.hytime.gitit.designsystem.navigation.GitItMainNavBar
import com.nexters.hytime.gitit.designsystem.navigation.GitItMainNavDestination
import com.nexters.hytime.gitit.designsystem.navigation.gitItMainNavSky
import com.nexters.hytime.gitit.designsystem.navigation.rememberGitItMainNavSky
import com.nexters.hytime.gitit.domain.model.CareerLevel
import com.nexters.hytime.gitit.domain.model.Position
import git_it_kmp.feature.my.generated.resources.Res
import git_it_kmp.feature.my.generated.resources.my_default_avatar
import git_it_kmp.feature.my.generated.resources.my_settings
import git_it_kmp.feature.my.generated.resources.my_settings_content_description
import git_it_kmp.feature.my.generated.resources.my_stat_solved_count
import git_it_kmp.feature.my.generated.resources.my_stat_streak
import git_it_kmp.feature.my.generated.resources.my_stat_streak_days
import git_it_kmp.feature.my.generated.resources.my_stat_this_month
import git_it_kmp.feature.my.generated.resources.my_stat_this_week
import git_it_kmp.feature.my.generated.resources.my_weekly_study_complete
import git_it_kmp.feature.my.generated.resources.my_weekly_study_empty
import git_it_kmp.feature.my.generated.resources.my_weekly_study_in_progress
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

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
            Spacer(Modifier.height(28.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "마이",
                    color = GitItTheme.colors.grey100,
                    style = GitItTheme.typography.subtitle1.copy(fontSize = 22.sp, lineHeight = 32.56.sp),
                    modifier = Modifier.weight(1f),
                )
                val settingsButton: @Composable () -> Unit = {
                    GitItLiquidGlassIconButton(
                        onClick = { onIntent(MyIntent.SettingsClick) },
                        size = GitItLiquidGlassIconButtonSize.Md,
                        variant = GitItLiquidGlassIconButtonVariant.Secondary,
                    ) {
                        SettingsIcon(
                            contentDescription = stringResource(Res.string.my_settings_content_description),
                        )
                    }
                }
                if (sky != null) {
                    GitItLiquidGlassContainer(sky = sky) { settingsButton() }
                } else {
                    settingsButton()
                }
            }
            Spacer(Modifier.height(30.dp))

            MyProfileHeader(profile = uiState.profile)
            Spacer(Modifier.height(40.dp))

            Text(
                text = "학습 현황",
                color = GitItTheme.colors.grey400,
                style = GitItTheme.typography.body3,
            )
            Spacer(Modifier.height(10.dp))

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
 * 설정 진입을 나타내는 피그마 원본 톱니바퀴 아이콘을 표시한다.
 *
 * @param contentDescription 접근성 서비스에 전달할 설명
 * @param modifier 외부 배치와 추가 수식자
 */
@Composable
private fun SettingsIcon(
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(Res.drawable.my_settings),
        contentDescription = contentDescription,
        modifier = modifier.size(18.1.dp),
    )
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
        Spacer(Modifier.width(15.dp))
        Column {
            Text(
                text = profile.name,
                color = GitItTheme.colors.grey100,
                style = GitItTheme.typography.subtitle2,
            )
            Text(
                text = profile.email,
                color = GitItTheme.colors.grey400,
                style = GitItTheme.typography.caption1,
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                MyProfileTag(
                    text = profile.position?.let { stringResource(it.toDisplayLabelResource()) }.orEmpty(),
                    backgroundColor = GitItTheme.colors.blue400,
                )
                MyProfileTag(
                    text = profile.careerLevel?.let { stringResource(it.toDisplayLabelResource()) }.orEmpty(),
                    backgroundColor = GitItTheme.colors.grey500,
                )
            }
        }
    }
}

/**
 * 프로필의 개발 분야 또는 학습 수준 태그를 표시한다.
 *
 * @param text 태그에 표시할 값
 * @param backgroundColor 태그 종류를 구분하는 배경색
 */
@Composable
private fun MyProfileTag(
    text: String,
    backgroundColor: androidx.compose.ui.graphics.Color,
) {
    Box(
        modifier =
            Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .padding(start = 10.dp, top = 3.dp, end = 10.dp, bottom = 4.dp),
    ) {
        Text(
            text = text,
            color = GitItTheme.colors.blue100,
            style = GitItTheme.typography.body3,
        )
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
        modifier = modifier.size(78.dp).clip(CircleShape),
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
            text = stringResource(stat.type.toLabelResource()),
            color = GitItTheme.colors.grey300,
            style = GitItTheme.typography.caption2,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(stat.type.toCountResource(), stat.count),
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
    val orderedItems = fixedWeeklyStudyItems(items)
    val maxSolvedCount = orderedItems.maxOfOrNull(MyWeeklyStudy::solvedCount) ?: 0

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .height(223.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(GitItTheme.colors.grey600)
                .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(
            text = "주간 문제 풀이량",
            color = GitItTheme.colors.grey400,
            style = GitItTheme.typography.caption2,
        )
        Text(
            text = stringResource(weeklyStudyMessageResource(orderedItems)),
            color = GitItTheme.colors.grey100,
            style = GitItTheme.typography.subtitle3,
        )
        Spacer(Modifier.height(26.dp))
        Column(modifier = Modifier.height(126.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().height(101.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                orderedItems.forEachIndexed { index, item ->
                    WeeklyStudyBar(
                        item = item,
                        maxSolvedCount = maxSolvedCount,
                        isToday = index == orderedItems.lastIndex,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Spacer(Modifier.height(7.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                orderedItems.forEach { item ->
                    Text(
                        text = item.day,
                        color = GitItTheme.colors.grey100,
                        style = GitItTheme.typography.body3,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

/**
 * 주간 학습량 막대 한 개를 표시한다.
 *
 * @param item 표시할 요일과 문제 수
 * @param maxSolvedCount 막대 높이 계산에 사용할 주간 최댓값
 * @param isToday 오늘에 해당하는 막대인지 여부
 * @param modifier 외부 배치와 추가 수식자
 */
@Composable
private fun WeeklyStudyBar(
    item: MyWeeklyStudy,
    maxSolvedCount: Int,
    isToday: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = item.solvedCount.coerceAtLeast(0).toString(),
            color = if (isToday) GitItTheme.colors.grey200 else GitItTheme.colors.grey300,
            style = GitItTheme.typography.body3,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(2.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(weeklyStudyBarHeight(item.solvedCount, maxSolvedCount).dp)
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .background(if (isToday) GitItTheme.colorStyles.gradient3 else GitItTheme.colorStyles.gradient1),
        )
    }
}

/**
 * 주간 최댓값에 비례하는 막대 높이를 계산한다.
 *
 * @param solvedCount 높이를 계산할 문제 수
 * @param maxSolvedCount 주간 일별 학습량 중 최댓값
 * @return 0부터 81 사이의 막대 높이(dp)
 */
internal fun weeklyStudyBarHeight(
    solvedCount: Int,
    maxSolvedCount: Int,
): Float = 81f * solvedCount.coerceIn(0, maxSolvedCount.coerceAtLeast(0)) / maxSolvedCount.coerceAtLeast(1)

/** 그래프에 표시할 월요일부터 일요일까지의 요일 순서다. */
private val WEEK_DAYS = listOf("월", "화", "수", "목", "금", "토", "일")

/**
 * 주간 학습량을 월요일부터 일요일 순서의 7개 항목으로 정규화한다.
 *
 * @param items 순서가 정해지지 않은 요일별 학습량
 * @return 월요일부터 일요일까지 정렬되고 누락된 요일은 0으로 채운 목록
 */
internal fun fixedWeeklyStudyItems(items: List<MyWeeklyStudy>): List<MyWeeklyStudy> {
    val itemsByDay = items.associateBy(MyWeeklyStudy::day)
    return WEEK_DAYS.map { day -> itemsByDay[day] ?: MyWeeklyStudy(day = day, solvedCount = 0) }
}

/**
 * 이번 주 학습한 날짜 수에 맞는 그래프 안내 문구를 반환한다.
 *
 * @param items 월요일부터 일요일까지 정규화된 주간 학습량
 * @return 학습 전·진행 중·7일 완료 상태에 대응하는 안내 문구 리소스
 */
internal fun weeklyStudyMessageResource(items: List<MyWeeklyStudy>): StringResource =
    when {
        items.none { it.solvedCount > 0 } -> Res.string.my_weekly_study_empty
        items.all { it.solvedCount > 0 } -> Res.string.my_weekly_study_complete
        else -> Res.string.my_weekly_study_in_progress
    }

/**
 * 학습 현황 항목의 이름 문구 리소스를 찾는다.
 *
 * @return 수치 위에 표시할 항목 이름 리소스
 */
private fun MyStudyStatType.toLabelResource(): StringResource =
    when (this) {
        MyStudyStatType.THIS_WEEK -> Res.string.my_stat_this_week
        MyStudyStatType.THIS_MONTH -> Res.string.my_stat_this_month
        MyStudyStatType.STREAK -> Res.string.my_stat_streak
    }

/**
 * 학습 현황 항목의 수치를 단위와 함께 표시할 문구 리소스를 찾는다.
 *
 * @return 수치 하나를 인자로 받는 문구 리소스
 */
private fun MyStudyStatType.toCountResource(): StringResource =
    when (this) {
        MyStudyStatType.THIS_WEEK, MyStudyStatType.THIS_MONTH -> Res.string.my_stat_solved_count
        MyStudyStatType.STREAK -> Res.string.my_stat_streak_days
    }

@Preview
@Composable
private fun MyScreenPreview() {
    GitItTheme {
        MyScreen(
            uiState =
                MyUiState(
                    profile =
                        MyProfile(
                            name = "김이박",
                            email = "kimlee@github.io",
                            position = Position.BACKEND,
                            careerLevel = CareerLevel.ENTRY,
                        ),
                    stats =
                        listOf(
                            MyStudyStat(type = MyStudyStatType.THIS_WEEK, count = 13),
                            MyStudyStat(type = MyStudyStatType.THIS_MONTH, count = 47),
                            MyStudyStat(type = MyStudyStatType.STREAK, count = 7),
                        ),
                    weeklyStudy =
                        listOf(
                            MyWeeklyStudy(day = "월", solvedCount = 14),
                            MyWeeklyStudy(day = "화", solvedCount = 8),
                            MyWeeklyStudy(day = "수", solvedCount = 18),
                            MyWeeklyStudy(day = "목", solvedCount = 11),
                            MyWeeklyStudy(day = "금", solvedCount = 14),
                            MyWeeklyStudy(day = "토", solvedCount = 11),
                            MyWeeklyStudy(day = "일", solvedCount = 18),
                        ),
                ),
            onIntent = {},
        )
    }
}

package com.nexters.hytime.gitit.feature.my

import com.nexters.hytime.gitit.domain.model.CareerLevel
import com.nexters.hytime.gitit.domain.model.DaySolvedCount
import com.nexters.hytime.gitit.domain.model.MemberProfile
import com.nexters.hytime.gitit.domain.model.Position
import git_it_kmp.feature.my.generated.resources.Res
import git_it_kmp.feature.my.generated.resources.my_career_level_entry
import git_it_kmp.feature.my.generated.resources.my_career_level_junior
import git_it_kmp.feature.my.generated.resources.my_career_level_middle
import git_it_kmp.feature.my.generated.resources.my_career_level_senior
import git_it_kmp.feature.my.generated.resources.my_position_android
import git_it_kmp.feature.my.generated.resources.my_position_backend
import git_it_kmp.feature.my.generated.resources.my_position_frontend
import git_it_kmp.feature.my.generated.resources.my_position_ios
import org.jetbrains.compose.resources.StringResource

/**
 * 회원 프로필 도메인 모델을 마이 화면 상태로 변환한다.
 *
 * 표시 문구는 화면이 문자열 리소스에서 만들므로 여기서는 값만 옮긴다.
 *
 * @return 프로필·학습 통계·주간 학습량이 채워진 화면 상태
 */
internal fun MemberProfile.toUiState(): MyUiState =
    MyUiState(
        profile =
            MyProfile(
                name = name.orEmpty(),
                email = email.orEmpty(),
                position = position,
                careerLevel = careerLevel,
            ),
        stats =
            listOf(
                MyStudyStat(type = MyStudyStatType.THIS_WEEK, count = thisWeekSolvedCount),
                MyStudyStat(type = MyStudyStatType.THIS_MONTH, count = thisMonthSolvedCount),
                MyStudyStat(type = MyStudyStatType.STREAK, count = streakDays),
            ),
        weeklyStudy = weeklyChart.map(DaySolvedCount::toWeeklyStudy),
    )

/**
 * 요일별 문제 풀이량을 주간 차트 항목으로 변환한다.
 *
 * @return 요일 라벨과 풀이 수를 담은 차트 항목
 */
private fun DaySolvedCount.toWeeklyStudy(): MyWeeklyStudy =
    MyWeeklyStudy(
        day = dayLabel,
        solvedCount = count,
    )

/**
 * 개발 분야 표기에 쓸 문자열 리소스를 찾는다.
 *
 * @return 화면에 표시할 표기 문구 리소스
 */
internal fun Position.toDisplayLabelResource(): StringResource =
    when (this) {
        Position.BACKEND -> Res.string.my_position_backend
        Position.FRONTEND -> Res.string.my_position_frontend
        Position.IOS -> Res.string.my_position_ios
        Position.ANDROID -> Res.string.my_position_android
    }

/**
 * 개발 수준 표기에 쓸 문자열 리소스를 찾는다.
 *
 * @return 화면에 표시할 표기 문구 리소스
 */
internal fun CareerLevel.toDisplayLabelResource(): StringResource =
    when (this) {
        CareerLevel.ENTRY -> Res.string.my_career_level_entry
        CareerLevel.JUNIOR -> Res.string.my_career_level_junior
        CareerLevel.MIDDLE -> Res.string.my_career_level_middle
        CareerLevel.SENIOR -> Res.string.my_career_level_senior
    }

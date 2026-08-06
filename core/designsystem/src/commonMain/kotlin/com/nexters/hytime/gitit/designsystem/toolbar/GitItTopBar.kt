package com.nexters.hytime.gitit.designsystem.toolbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.icons.GitItBackIcon
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButton
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButtonSize
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButtonVariant

/** Figma Toolbar Top 컴포넌트의 4가지 레이아웃 변형이다. */
enum class GitItTopBarType {
    /** 좌측 뒤로가기 버튼, 우측 액션 버튼 그룹. */
    Default,

    /** 컨트롤 행 아래에 큰 제목·부제목 블록을 추가로 배치한다. */
    LargeTitle,

    /** 좌측에 프로필 아바타·이름·역할을 인라인으로 표시한다. */
    InlineUser,

    /** 좌측에 제목·부제목을 인라인으로 표시하고 우측으로 밀어낸다. */
    InlineTitle,
}

/**
 * Figma의 Toolbar Top 컴포넌트를 Compose로 렌더링한다.
 *
 * [type]에 따라 컨트롤 행의 좌측 콘텐츠가 결정되며, 우측 [actions] 슬롯에는
 * [GitItLiquidGlassButtonGroup]을 배치하는 것이 기본 패턴이다.
 *
 * 프로필 아바타([userAvatar])는 40dp 원형으로 클리핑되는 슬롯이므로,
 * 호출하는 쪽에서 Coil `AsyncImage` 등 원하는 이미지 콘텐츠를 제공한다.
 *
 * @param type 툴바 레이아웃 변형
 * @param modifier 툴바의 외부 배치와 추가 수식자
 * @param title LargeTitle·InlineTitle 변형에서 표시할 제목
 * @param subtitle 제목 아래에 표시할 부제목
 * @param userName InlineUser 변형에서 표시할 사용자 이름
 * @param userSubtitle InlineUser 변형에서 표시할 사용자 역할·설명
 * @param userAvatar InlineUser 변형에서 40dp 원형으로 클리핑할 프로필 이미지 슬롯
 * @param onBackClick Default·LargeTitle 변형에서 뒤로가기 버튼 클릭 시 실행할 동작.
 *     null이면 뒤로가기 버튼을 표시하지 않는다
 * @param actions 컨트롤 행 우측에 배치할 액션 콘텐츠
 */
@Composable
fun GitItTopBar(
    type: GitItTopBarType = GitItTopBarType.Default,
    modifier: Modifier = Modifier,
    title: String = "",
    subtitle: String = "",
    userName: String = "",
    userSubtitle: String = "",
    userAvatar: @Composable () -> Unit = {},
    onBackClick: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {},
) {
    Column(modifier = modifier.padding(bottom = 10.dp)) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.Top,
        ) {
            when (type) {
                GitItTopBarType.InlineTitle -> {
                    GitItTopBarTitle(title = title, subtitle = subtitle, modifier = Modifier.weight(1f))
                    actions()
                }

                GitItTopBarType.InlineUser -> {
                    GitItTopBarUserProfile(
                        name = userName,
                        subtitle = userSubtitle,
                        avatar = userAvatar,
                    )
                    Spacer(Modifier.weight(1f))
                    actions()
                }

                else -> {
                    if (onBackClick != null) {
                        GitItLiquidGlassIconButton(
                            onClick = onBackClick,
                            size = GitItLiquidGlassIconButtonSize.Md,
                            variant = GitItLiquidGlassIconButtonVariant.Secondary,
                        ) {
                            GitItBackIcon(size = GitItLiquidGlassIconButtonSize.Md)
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    actions()
                }
            }
        }

        if (type == GitItTopBarType.LargeTitle) {
            Spacer(Modifier.height(16.dp))
            GitItTopBarTitle(
                title = title,
                subtitle = subtitle,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
            )
        }
    }
}

/**
 * 툴바 제목과 부제목을 세로로 배치한다.
 *
 * @param title 표시할 제목. 빈 문자열이면 렌더링하지 않는다
 * @param subtitle 표시할 부제목. 빈 문자열이면 렌더링하지 않는다
 * @param modifier 이 블록의 외부 배치와 추가 수식자
 */
@Composable
private fun GitItTopBarTitle(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                color = GitItTheme.colors.grey100,
                style = GitItTheme.typography.subtitle1,
            )
        }
        if (subtitle.isNotEmpty()) {
            Text(
                text = subtitle,
                color = GitItTheme.colors.white30,
                style = GitItTheme.typography.body2,
            )
        }
    }
}

/**
 * 툴바 인라인 사용자 영역에 아바타·이름·역할을 가로로 배치한다.
 *
 * @param name 사용자 이름. 빈 문자열이면 렌더링하지 않는다
 * @param subtitle 사용자 역할·설명. 빈 문자열이면 렌더링하지 않는다
 * @param avatar 40dp 원형으로 클리핑되는 프로필 이미지 슬롯
 * @param modifier 이 영역의 외부 배치와 추가 수식자
 */
@Composable
private fun GitItTopBarUserProfile(
    name: String,
    subtitle: String,
    avatar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            avatar()
        }
        Column {
            if (name.isNotEmpty()) {
                Text(
                    text = name,
                    color = GitItTheme.colors.grey100,
                    style = GitItTheme.typography.subtitle3,
                )
            }
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    color = GitItTheme.colors.grey400,
                    style = GitItTheme.typography.body3,
                )
            }
        }
    }
}

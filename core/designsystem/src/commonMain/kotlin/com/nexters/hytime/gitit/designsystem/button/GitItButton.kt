package com.nexters.hytime.gitit.designsystem.button

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItColors
import com.nexters.hytime.gitit.designsystem.GitItTheme

/**
 * Git-it 버튼의 높이와 내부 여백 규격을 정의한다.
 *
 * @property height Figma에 정의된 버튼의 고정 높이
 * @property horizontalPadding 버튼 양끝과 콘텐츠 사이의 수평 여백
 */
enum class GitItButtonSize(
    internal val height: Dp,
    internal val horizontalPadding: Dp,
) {
    /** 본문 1 타이포그래피를 사용하는 54dp 버튼이다. */
    Large(
        height = 54.dp,
        horizontalPadding = 12.dp,
    ),

    /** 본문 2 타이포그래피를 사용하는 40dp 버튼이다. */
    Medium(
        height = 40.dp,
        horizontalPadding = 10.dp,
    ),

    /** 본문 2 타이포그래피를 사용하는 36dp 버튼이다. */
    Small(
        height = 36.dp,
        horizontalPadding = 8.dp,
    ),
}

/** 버튼의 배경과 강조 방식을 정의한다. */
enum class GitItButtonStyle {
    /** 브랜드 블루 배경을 사용하는 기본 강조 버튼이다. */
    Primary,

    /** 반투명 흰색 배경을 사용하는 보조 강조 버튼이다. */
    Secondary,

    /** 브랜드 블루 텍스트만 표시하는 버튼이다. */
    PrimaryText,

    /** 흰색 텍스트만 표시하는 버튼이다. */
    Text,
}

/** 버튼의 상호작용 및 피드백 상태를 정의한다. */
enum class GitItButtonState {
    /** 클릭할 수 있는 기본 상태다. */
    Default,

    /** 버튼을 누르는 동안 Figma의 active 시각 피드백을 표시하는 상태다. */
    Active,

    /** 클릭할 수 없고 낮은 대비로 표시되는 상태다. */
    Disabled,

    /** 클릭할 수 있으며 오류 색상으로 피드백하는 상태다. */
    Error,
}

/**
 * Figma Button 컴포넌트의 크기, 스타일, 상태 조합을 렌더링한다.
 *
 * 아이콘 슬롯에는 현재 버튼의 콘텐츠 색상을 [LocalContentColor]로 제공한다. 버튼 너비는 별도 옵션 없이 [modifier]를 따르므로,
 * 단독 전체 너비는 `Modifier.fillMaxWidth()`, Row 안의 균등 너비는 `Modifier.weight(1f)`로 지정한다.
 * [GitItButtonState.Default]에서 버튼을 누르면 Figma의 [GitItButtonState.Active] 색상으로 자동 전환하고 기본 리플을 함께 표시한다.
 * [GitItButtonState.Disabled]일 때는 클릭 이벤트가 전달되지 않는다.
 *
 * @param text 버튼에 한 줄로 표시할 레이블
 * @param onClick 활성 상태에서 버튼을 누르면 호출할 이벤트
 * @param modifier 버튼의 크기와 배치를 조정할 수식자
 * @param size 버튼 높이와 타이포그래피 크기를 결정할 규격
 * @param style 버튼의 배경색과 콘텐츠 색상을 결정할 강조 방식
 * @param state 버튼의 활성 여부와 피드백 색상을 결정할 상태. Default의 눌림 상태는 Active로 자동 전환된다
 * @param leadingIcon 레이블 앞의 20dp 영역에 표시할 선택적 아이콘 콘텐츠
 * @param trailingIcon 레이블 뒤의 20dp 영역에 표시할 선택적 아이콘 콘텐츠
 */
@Composable
fun GitItButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: GitItButtonSize = GitItButtonSize.Large,
    style: GitItButtonStyle = GitItButtonStyle.Primary,
    state: GitItButtonState = GitItButtonState.Default,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val visualState = resolveButtonVisualState(state = state, isPressed = isPressed)
    val colors = resolveButtonColors(style = style, state = visualState, colors = GitItTheme.colors)

    Row(
        modifier =
            modifier
                .height(size.height)
                .clip(buttonShape)
                .background(colors.containerColor)
                .clickable(
                    interactionSource = interactionSource,
                    indication = LocalIndication.current,
                    enabled = state != GitItButtonState.Disabled,
                    role = Role.Button,
                    onClick = onClick,
                ).padding(horizontal = size.horizontalPadding),
        horizontalArrangement =
            Arrangement.spacedBy(
                space = buttonContentSpacing,
                alignment = Alignment.CenterHorizontally,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingIcon?.let { icon ->
            ButtonIconSlot(
                contentColor = colors.contentColor,
                content = icon,
            )
        }
        Text(
            text = text,
            color = colors.contentColor,
            style = size.textStyle(),
            maxLines = 1,
            softWrap = false,
        )
        trailingIcon?.let { icon ->
            ButtonIconSlot(
                contentColor = colors.contentColor,
                content = icon,
            )
        }
    }
}

/**
 * 실제 누름 여부를 Figma Button의 시각 상태로 변환한다.
 *
 * @param state 외부에서 지정한 버튼 상태
 * @param isPressed 현재 포인터나 터치로 버튼을 누르고 있는지 여부
 * @return Default를 누르는 동안에는 Active, 그 외에는 외부 상태를 그대로 반환한다
 */
internal fun resolveButtonVisualState(
    state: GitItButtonState,
    isPressed: Boolean,
): GitItButtonState =
    if (isPressed && state == GitItButtonState.Default) {
        GitItButtonState.Active
    } else {
        state
    }

/**
 * 호출자가 제공한 아이콘을 버튼의 20×20dp 아이콘 영역 중앙에 배치한다.
 *
 * @param contentColor 별도 색상을 지정하지 않은 Material 아이콘에 적용할 버튼 콘텐츠 색상
 * @param content 아이콘 영역에 표시할 임의의 Composable 콘텐츠
 */
@Composable
private fun ButtonIconSlot(
    contentColor: Color,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Box(
            modifier = Modifier.size(buttonIconContainerSize),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

/**
 * 버튼 크기에 맞는 Git-it 타이포그래피 토큰을 반환한다.
 *
 * @return Large는 본문 1, Medium과 Small은 본문 2 텍스트 스타일
 */
@Composable
private fun GitItButtonSize.textStyle(): TextStyle =
    when (this) {
        GitItButtonSize.Large -> GitItTheme.typography.body1
        GitItButtonSize.Medium,
        GitItButtonSize.Small,
        -> GitItTheme.typography.body2
    }

/**
 * 버튼 배경과 콘텐츠에 적용할 색상을 묶는다.
 *
 * @property containerColor 버튼의 배경색
 * @property contentColor 레이블과 chevron에 함께 적용할 색상
 */
@Immutable
internal data class GitItButtonColors(
    val containerColor: Color,
    val contentColor: Color,
)

/**
 * Figma의 스타일 및 상태 조합을 Git-it 원시 색상 토큰에 매핑한다.
 *
 * @param style 배경과 기본 콘텐츠 강조 방식을 결정할 스타일
 * @param state 기본, 눌림, 비활성, 오류 색상을 결정할 상태
 * @param colors 매핑에 사용할 Git-it 원시 색상 토큰
 * @return 배경과 콘텐츠에 적용할 색상 조합
 */
internal fun resolveButtonColors(
    style: GitItButtonStyle,
    state: GitItButtonState,
    colors: GitItColors,
): GitItButtonColors =
    GitItButtonColors(
        containerColor = resolveContainerColor(style = style, state = state, colors = colors),
        contentColor = resolveContentColor(style = style, state = state, colors = colors),
    )

/**
 * 버튼 스타일 및 상태에 맞는 배경색을 반환한다.
 *
 * @param style 배경 강조 방식
 * @param state 기본, 눌림, 비활성, 오류 상태
 * @param colors 매핑에 사용할 Git-it 원시 색상 토큰
 * @return 조합에 맞는 배경색
 */
private fun resolveContainerColor(
    style: GitItButtonStyle,
    state: GitItButtonState,
    colors: com.nexters.hytime.gitit.designsystem.GitItColors,
): Color =
    when (style) {
        GitItButtonStyle.Primary ->
            when (state) {
                GitItButtonState.Default -> colors.blue100
                GitItButtonState.Active -> colors.white30.compositeOver(colors.blue100)
                GitItButtonState.Disabled -> colors.white15
                GitItButtonState.Error -> colors.error
            }

        GitItButtonStyle.Secondary -> colors.white15
        GitItButtonStyle.PrimaryText,
        GitItButtonStyle.Text,
        ->
            when (state) {
                GitItButtonState.Active -> colors.white15
                GitItButtonState.Default,
                GitItButtonState.Disabled,
                GitItButtonState.Error,
                -> Color.Transparent
            }
    }

/**
 * 버튼 스타일 및 상태에 맞는 레이블과 아이콘 색상을 반환한다.
 *
 * @param style 콘텐츠 강조 방식
 * @param state 기본, 눌림, 비활성, 오류 상태
 * @param colors 매핑에 사용할 Git-it 원시 색상 토큰
 * @return 조합에 맞는 콘텐츠 색상
 */
private fun resolveContentColor(
    style: GitItButtonStyle,
    state: GitItButtonState,
    colors: com.nexters.hytime.gitit.designsystem.GitItColors,
): Color =
    when (style) {
        GitItButtonStyle.Primary ->
            when (state) {
                GitItButtonState.Default,
                GitItButtonState.Active,
                -> colors.grey700
                GitItButtonState.Disabled -> colors.white30
                GitItButtonState.Error -> colors.grey100
            }

        GitItButtonStyle.Secondary ->
            when (state) {
                GitItButtonState.Default,
                GitItButtonState.Active,
                -> colors.grey100
                GitItButtonState.Disabled -> colors.white30
                GitItButtonState.Error -> colors.error
            }

        GitItButtonStyle.PrimaryText ->
            when (state) {
                GitItButtonState.Default,
                GitItButtonState.Active,
                -> colors.blue100
                GitItButtonState.Disabled -> colors.blue400
                GitItButtonState.Error -> colors.error
            }

        GitItButtonStyle.Text ->
            when (state) {
                GitItButtonState.Default,
                GitItButtonState.Active,
                -> colors.grey100
                GitItButtonState.Disabled -> colors.white30
                GitItButtonState.Error -> colors.error
            }
    }

@Preview(
    widthDp = 680,
    heightDp = 725,
)
@Composable
private fun GitItButtonPreview() {
    GitItTheme {
        Column(
            modifier =
                Modifier
                    .width(680.dp)
                    .background(GitItTheme.colors.grey700)
                    .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            GitItButtonSize.entries.forEach { size ->
                GitItButtonStyle.entries.forEach { style ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        GitItButtonState.entries.forEach { state ->
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center,
                            ) {
                                GitItButton(
                                    text = "Label",
                                    onClick = {},
                                    size = size,
                                    style = style,
                                    state = state,
                                    leadingIcon = { PreviewButtonIcon() },
                                    trailingIcon = { PreviewButtonIcon() },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    widthDp = 360,
    heightDp = 102,
)
@Composable
private fun GitItButtonFillPreview() {
    GitItTheme {
        Row(
            modifier =
                Modifier
                    .width(360.dp)
                    .background(GitItTheme.colors.grey600)
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            GitItButton(
                text = "Label",
                onClick = {},
                modifier = Modifier.weight(1f),
                style = GitItButtonStyle.Secondary,
            )
            GitItButton(
                text = "Label",
                onClick = {},
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** 범용 아이콘 슬롯과 콘텐츠 색상 전달을 프리뷰에서 확인하기 위한 도형을 표시한다. */
@Composable
private fun PreviewButtonIcon() {
    Box(
        modifier =
            Modifier
                .size(12.dp)
                .background(LocalContentColor.current, CircleShape),
    )
}

/** 버튼 외곽선의 Figma 반지름이다. */
private val buttonShape = RoundedCornerShape(12.dp)

/** 레이블과 아이콘 사이의 Figma 수평 간격이다. */
private val buttonContentSpacing = 8.dp

/** 호출자가 제공한 아이콘을 정렬하는 영역의 크기다. */
private val buttonIconContainerSize = 20.dp

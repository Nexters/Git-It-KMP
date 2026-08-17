package com.nexters.hytime.gitit.designsystem.selectcard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItColors
import com.nexters.hytime.gitit.designsystem.GitItTheme

/** Select Card 외곽에 적용하는 Figma의 12dp 모서리 규격이다. */
private val selectCardShape = RoundedCornerShape(12.dp)

/** 썸네일을 52dp 영역 안에서 자르는 Figma의 8dp 모서리 규격이다. */
private val selectCardThumbnailShape = RoundedCornerShape(8.dp)

/**
 * Figma Select Card 컴포넌트를 Compose로 렌더링한다.
 *
 * 선택 상태는 호출자가 [selected]로 소유하며, 카드를 누르면 [onClick]만 전달한다. [thumbnail]은
 * 52×52dp 영역에 클리핑되므로 이미지 콘텐츠가 영역을 채우도록 구성한다. [description], [tag],
 * [thumbnail]에 null을 전달하면 해당 요소를 렌더링하지 않는다. 특히 [thumbnail]이 null이면
 * 썸네일 영역과 텍스트 사이 간격까지 함께 사라져 텍스트가 카드 왼쪽 패딩에 바로 붙는다.
 *
 * @param title 카드가 나타내는 선택지를 한 줄로 설명하는 제목
 * @param onClick 사용자가 카드를 선택했을 때 호출할 이벤트
 * @param modifier 카드의 너비와 외부 배치를 조정할 수식자
 * @param selected 현재 선택 여부. true이면 blue200 테두리를 표시한다
 * @param description 제목 아래에 표시할 선택적 한 줄 설명
 * @param tag 제목 옆에 표시할 선택적 짧은 태그
 * @param thumbnail 52×52dp로 클리핑되는 선택적 썸네일 콘텐츠
 */
@Composable
fun GitItSelectCard(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    description: String? = null,
    tag: String? = null,
    thumbnail: (@Composable BoxScope.() -> Unit)? = null,
) {
    val colors = resolveSelectCardColors(selected = selected, colors = GitItTheme.colors)

    Row(
        modifier =
            modifier
                .clip(selectCardShape)
                .background(colors.containerColor)
                .border(
                    width = 1.dp,
                    color = colors.borderColor,
                    shape = selectCardShape,
                ).selectable(
                    selected = selected,
                    role = Role.RadioButton,
                    onClick = onClick,
                ).padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        thumbnail?.let { thumbnailContent ->
            Box(
                modifier =
                    Modifier
                        .size(52.dp)
                        .clip(selectCardThumbnailShape),
                contentAlignment = Alignment.Center,
                content = thumbnailContent,
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(weight = 1f, fill = false),
                    color = colors.titleColor,
                    style = GitItTheme.typography.subtitle3,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                tag?.let { tagText ->
                    SelectCardTag(
                        text = tagText,
                        containerColor = colors.tagContainerColor,
                        contentColor = colors.tagContentColor,
                    )
                }
            }

            description?.let { descriptionText ->
                Text(
                    text = descriptionText,
                    color = colors.descriptionColor,
                    style = GitItTheme.typography.caption1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/**
 * Select Card 제목 옆에 짧은 태그를 표시한다.
 *
 * @param text 태그에 표시할 한 줄 레이블
 * @param containerColor 태그 캡슐의 배경색
 * @param contentColor 태그 레이블의 색상
 */
@Composable
private fun SelectCardTag(
    text: String,
    containerColor: Color,
    contentColor: Color,
) {
    Box(
        modifier =
            Modifier
                .height(18.dp)
                .defaultMinSize(minWidth = 32.dp)
                .clip(CircleShape)
                .background(containerColor)
                .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = contentColor,
            style = GitItTheme.typography.caption2,
            maxLines = 1,
            softWrap = false,
        )
    }
}

/**
 * Select Card의 선택 상태별 색상을 묶는다.
 *
 * @property containerColor 카드의 배경색
 * @property borderColor 선택 여부를 나타내는 외곽선 색상
 * @property titleColor 제목에 적용할 색상
 * @property descriptionColor 설명에 적용할 색상
 * @property tagContainerColor 태그 캡슐의 배경색
 * @property tagContentColor 태그 레이블의 색상
 */
@Immutable
internal data class GitItSelectCardColors(
    val containerColor: Color,
    val borderColor: Color,
    val titleColor: Color,
    val descriptionColor: Color,
    val tagContainerColor: Color,
    val tagContentColor: Color,
)

/**
 * Figma의 Select Card 선택 상태를 Git-it 원시 색상 토큰에 매핑한다.
 *
 * @param selected 현재 선택 여부
 * @param colors 매핑에 사용할 Git-it 원시 색상 토큰
 * @return 카드, 텍스트, 태그에 적용할 색상 조합
 */
internal fun resolveSelectCardColors(
    selected: Boolean,
    colors: GitItColors,
): GitItSelectCardColors =
    GitItSelectCardColors(
        containerColor = colors.grey600,
        borderColor = if (selected) colors.blue200 else Color.Transparent,
        titleColor = colors.grey100,
        descriptionColor = colors.grey300,
        tagContainerColor = colors.blue500,
        tagContentColor = colors.blue200,
    )

@Preview(widthDp = 360, heightDp = 520)
@Composable
private fun GitItSelectCardPreview() {
    GitItTheme {
        val thumbnail: @Composable BoxScope.() -> Unit = {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(GitItTheme.colorStyles.gradient3),
            )
        }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(GitItTheme.colors.grey700)
                    .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            GitItSelectCard(
                title = "코드 의도",
                description = "특정 구현 방식을 선택한 이유",
                tag = "추천",
                onClick = {},
                thumbnail = thumbnail,
            )
            GitItSelectCard(
                title = "기술 개념은 알아요",
                description = "실제 코드 작동 방식을 흐름 중심으로 학습",
                selected = true,
                onClick = {},
                thumbnail = thumbnail,
            )
            GitItSelectCard(
                title = "일부 코드를 봤어요",
                description = "구현 의도와 연결 영향까지 포함",
                onClick = {},
                thumbnail = thumbnail,
            )
            GitItSelectCard(
                title = "유사 프로젝트 경험이 있어요",
                onClick = {},
                thumbnail = thumbnail,
            )
            GitItSelectCard(
                title = "Front-end",
                onClick = {},
            )
            GitItSelectCard(
                title = "Back-end",
                selected = true,
                onClick = {},
            )
        }
    }
}

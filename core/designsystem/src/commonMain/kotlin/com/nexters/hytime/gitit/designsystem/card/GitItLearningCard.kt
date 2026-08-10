package com.nexters.hytime.gitit.designsystem.card

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexters.hytime.gitit.designsystem.GitItTheme

/**
 * Figma의 학습 세트 카드를 렌더링한다.
 *
 * 카드 본문과 재생 버튼은 서로 다른 클릭 영역이며 각각 [onCardClick], [onPlayClick]으로 이벤트를 전달한다.
 *
 * @param title 카드 상단에 최대 두 줄로 표시할 제목
 * @param technologies 제목 아래에 표시할 기술 목록
 * @param setLabel 학습 세트 식별 문구
 * @param description 학습 세트 설명
 * @param progress 완료 진행률. 0f보다 작거나 1f보다 크면 표시 범위로 보정한다
 * @param onCardClick 재생 버튼을 제외한 카드를 눌렀을 때 실행할 동작
 * @param onPlayClick 재생 버튼을 눌렀을 때 실행할 동작
 * @param modifier 카드의 외부 배치와 추가 수식자
 * @param backgroundColor 카드 배경색
 * @param playContentDescription 재생 버튼의 접근성 설명
 */
@Composable
fun GitItLearningCard(
    title: String,
    technologies: String,
    setLabel: String,
    description: String,
    progress: Float,
    onCardClick: () -> Unit,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = GitItTheme.colors.purple300,
    playContentDescription: String = "재생",
) {
    Box(
        modifier =
            modifier
                .size(width = 154.dp, height = 192.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .clickable(role = Role.Button, onClick = onCardClick),
    ) {
        Row(
            modifier =
                Modifier
                    .offset(x = 13.dp, y = 17.dp)
                    .width(132.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.width(94.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = title,
                    color = GitItTheme.colors.grey100,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = GitItTheme.typography.subtitle3.copy(lineHeight = 19.2.sp),
                )
                Text(
                    text = technologies,
                    color = GitItTheme.colors.grey200,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = GitItTheme.typography.caption2,
                )
            }

            GitItPlayButton(
                contentDescription = playContentDescription,
                onClick = onPlayClick,
            )
        }

        Column(
            modifier =
                Modifier
                    .offset(x = 13.dp, y = 119.dp)
                    .width(128.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Box(
                    modifier =
                        Modifier
                            .height(19.dp)
                            .clip(RoundedCornerShape(99.dp))
                            .background(GitItTheme.colors.purple400)
                            .padding(horizontal = 5.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = setLabel,
                        color = GitItTheme.colors.grey100,
                        maxLines = 1,
                        style = GitItTheme.typography.caption2,
                    )
                }
                Text(
                    text = description,
                    modifier = Modifier.fillMaxWidth(),
                    color = GitItTheme.colors.grey100,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = GitItTheme.typography.caption1,
                )
            }

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(GitItTheme.colors.purple200),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(normalizedProgress(progress))
                            .fillMaxHeight()
                            .background(GitItTheme.colors.purple400),
                )
            }
        }
    }
}

/** Figma에서 내보낸 34×34 재생 아이콘의 원본 path 데이터다. */
private const val PLAY_ICON_PATH =
    "M17 2C25.2843 2 32 8.71573 32 17C32 25.2843 25.2843 32 17 32C8.71573 32 2 25.2843 2 17" +
        "C2 8.71573 8.71573 2 17 2Z" +
        "M13.8271 11.7188C13.5614 11.5874 13.2501 11.7807 13.25 12.0771V21.9238" +
        "C13.2503 22.2201 13.5615 22.4134 13.8271 22.2822L23.7754 17.3584" +
        "C24.0721 17.2115 24.0722 16.7885 23.7754 16.6416L13.8271 11.7188Z"

/**
 * 재생 아이콘을 독립된 클릭 영역으로 렌더링한다.
 *
 * @param contentDescription 접근성 서비스에 전달할 재생 동작 설명
 * @param onClick 재생 아이콘을 눌렀을 때 실행할 동작
 * @param modifier 아이콘의 외부 배치와 추가 수식자
 */
@Composable
private fun GitItPlayButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val path = remember { PathParser().parsePathString(PLAY_ICON_PATH).toPath() }
    val iconColor = GitItTheme.colors.grey200

    Canvas(
        modifier =
            modifier
                .size(34.dp)
                .semantics { this.contentDescription = contentDescription }
                .clickable(
                    role = Role.Button,
                    onClick = onClick,
                ),
    ) {
        withTransform(
            transformBlock = {
                scale(
                    scaleX = size.width / 34f,
                    scaleY = size.height / 34f,
                    pivot = Offset.Zero,
                )
            },
        ) {
            drawPath(path = path, color = iconColor)
        }
    }
}

/**
 * 외부 진행률을 Compose 크기 비율로 사용할 수 있는 범위로 보정한다.
 *
 * @param progress 외부에서 전달된 진행률
 * @return 0f..1f 범위로 제한된 진행률
 */
internal fun normalizedProgress(progress: Float): Float = if (progress.isNaN()) 0f else progress.coerceIn(0f, 1f)

@Preview(name = "학습 카드")
@Composable
private fun GitItLearningCardPreview() {
    GitItTheme {
        GitItLearningCard(
            title = "Now in\nAndroid",
            technologies = "Kotlin · Compose · Coroutines",
            setLabel = "Set 1",
            description = "Compose 핵심 개념",
            progress = 0.21f,
            onCardClick = {},
            onPlayClick = {},
        )
    }
}

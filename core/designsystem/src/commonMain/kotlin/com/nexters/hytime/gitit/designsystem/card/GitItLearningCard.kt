package com.nexters.hytime.gitit.designsystem.card

import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import git_it_kmp.core.designsystem.generated.resources.Res
import git_it_kmp.core.designsystem.generated.resources.ic_play_learning
import org.jetbrains.compose.resources.painterResource

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
    Image(
        painter = painterResource(Res.drawable.ic_play_learning),
        contentDescription = contentDescription,
        modifier =
            modifier
                .size(34.dp)
                .clickable(
                    role = Role.Button,
                    onClick = onClick,
                ),
    )
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

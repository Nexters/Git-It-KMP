package com.nexters.hytime.gitit.feature.onboarding.tutorial

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.button.GitItButton
import com.nexters.hytime.gitit.designsystem.button.GitItButtonState
import com.nexters.hytime.gitit.designsystem.button.GitItButtonStyle
import com.nexters.hytime.gitit.designsystem.selectcard.GitItSelectCard
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBar
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBarType
import git_it_kmp.feature.onboarding.generated.resources.Res
import git_it_kmp.feature.onboarding.generated.resources.tutorial_preview_thumbnail
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/**
 * 튜토리얼 선택 화면에 표시할 항목이다.
 *
 * @property id 선택 상태와 LazyColumn key에 사용하는 화면 내 고유 식별자
 * @property title 선택지를 한 줄로 설명하는 제목
 * @property thumbnailRes 카드 왼쪽 52×52dp 영역에 표시할 이미지 리소스
 * @property description 제목 아래에 표시할 선택적 한 줄 설명
 */
@Immutable
data class TutorialOption(
    val id: String,
    val title: String,
    val thumbnailRes: DrawableResource,
    val description: String? = null,
)

/**
 * 학습 분야 또는 코드 이해 수준을 선택하는 튜토리얼 화면이다.
 *
 * 화면은 선택 상태를 소유하지 않는다. 호출자가 [selectedOptionId]를 전달하고 [onOptionClick]에서
 * 상태를 갱신해야 한다. 선택 전에는 다음 버튼을 비활성화하고, 선택 후에는 활성화한다.
 *
 * @param title 화면 상단에 두 줄까지 표시할 질문형 제목
 * @param options 카드로 표시할 선택지 목록. 각 항목의 id는 화면 안에서 고유해야 한다
 * @param selectedOptionId 현재 선택된 항목 식별자. 선택 전에는 null
 * @param nextButtonText 화면 하단 진행 버튼에 표시할 레이블
 * @param onOptionClick 사용자가 선택 카드를 누르면 해당 항목과 함께 호출할 이벤트
 * @param onBackClick 사용자가 상단 뒤로가기 버튼을 누르면 호출할 이벤트
 * @param onNextClick 활성화된 진행 버튼을 누르면 호출할 이벤트
 * @param modifier 화면의 크기와 외부 배치를 지정할 수식자
 * @param helperText 진행 버튼 위에 표시할 선택적 한 줄 안내문
 * @param nextButtonEnabled 선택값 외의 조건을 포함한 진행 버튼 활성화 여부
 */
@Composable
fun TutorialScreen(
    title: String,
    options: List<TutorialOption>,
    selectedOptionId: String?,
    nextButtonText: String,
    onOptionClick: (TutorialOption) -> Unit,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
    helperText: String? = null,
    nextButtonEnabled: Boolean = selectedOptionId != null,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(GitItTheme.colors.grey700)
                .statusBarsPadding()
                .navigationBarsPadding(),
    ) {
        GitItTopBar(
            type = GitItTopBarType.Default,
            onBackClick = onBackClick,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = title,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            color = GitItTheme.colors.grey100,
            style = GitItTheme.typography.subtitle1,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp)
                    .selectableGroup(),
            contentPadding = PaddingValues(top = 64.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                items = options,
                key = TutorialOption::id,
            ) { option ->
                GitItSelectCard(
                    title = option.title,
                    description = option.description,
                    selected = option.id == selectedOptionId,
                    onClick = { onOptionClick(option) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Image(
                        painter = painterResource(option.thumbnailRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
        helperText?.let { text ->
            Text(
                text = text,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                color = GitItTheme.colors.grey400,
                style = GitItTheme.typography.caption1,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        GitItButton(
            text = nextButtonText,
            onClick = onNextClick,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            style = GitItButtonStyle.Primary,
            state =
                if (!nextButtonEnabled) {
                    GitItButtonState.Disabled
                } else {
                    GitItButtonState.Default
                },
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

/** 학습 분야 튜토리얼 프리뷰에서 사용하는 선택지다. */
private val tutorialCategoryOptions =
    listOf(
        TutorialOption(id = "frontend", title = "Front-end", thumbnailRes = Res.drawable.tutorial_preview_thumbnail),
        TutorialOption(id = "backend", title = "Back-end", thumbnailRes = Res.drawable.tutorial_preview_thumbnail),
        TutorialOption(id = "ios", title = "iOS", thumbnailRes = Res.drawable.tutorial_preview_thumbnail),
        TutorialOption(id = "android", title = "Android", thumbnailRes = Res.drawable.tutorial_preview_thumbnail),
    )

/** 코드 이해 수준 튜토리얼 프리뷰에서 사용하는 선택지다. */
private val tutorialLevelOptions =
    listOf(
        TutorialOption(
            id = "beginner",
            title = "입문",
            description = "프로젝트 코드를 처음 살펴봐요.",
            thumbnailRes = Res.drawable.tutorial_preview_thumbnail,
        ),
        TutorialOption(
            id = "junior",
            title = "주니어",
            description = "작은 기능 단위로 코드를 이해할 수 있어요.",
            thumbnailRes = Res.drawable.tutorial_preview_thumbnail,
        ),
        TutorialOption(
            id = "middle",
            title = "미들",
            description = "프로젝트 구조와 흐름을 함께 살펴봐요.",
            thumbnailRes = Res.drawable.tutorial_preview_thumbnail,
        ),
        TutorialOption(
            id = "senior",
            title = "시니어",
            description = "설계 의도와 변경 영향을 분석할 수 있어요.",
            thumbnailRes = Res.drawable.tutorial_preview_thumbnail,
        ),
    )

/**
 * Preview에서 카드 선택과 버튼 활성화 변화를 확인할 수 있도록 로컬 선택 상태를 제공한다.
 *
 * @param title 화면 상단에 표시할 질문형 제목
 * @param options 카드로 표시할 선택지 목록
 * @param initialSelectedOptionId Preview 최초 선택 항목 식별자
 * @param helperText 진행 버튼 위에 표시할 선택적 안내문
 */
@Composable
private fun TutorialInteractivePreview(
    title: String,
    options: List<TutorialOption>,
    initialSelectedOptionId: String?,
    helperText: String? = null,
) {
    var selectedOptionId by remember(initialSelectedOptionId) { mutableStateOf(initialSelectedOptionId) }

    GitItTheme {
        TutorialScreen(
            title = title,
            options = options,
            selectedOptionId = selectedOptionId,
            nextButtonText = "다음",
            onOptionClick = { selectedOptionId = it.id },
            onBackClick = {},
            onNextClick = {},
            helperText = helperText,
        )
    }
}

@Preview(name = "분야 - 선택 전", widthDp = 360, heightDp = 800)
@Composable
internal fun TutorialCategoryUnselectedPreview() {
    TutorialInteractivePreview(
        title = "어떤 분야의 코드를\n학습하고 싶나요?",
        options = tutorialCategoryOptions,
        initialSelectedOptionId = null,
    )
}

@Preview(name = "분야 - 선택 후", widthDp = 360, heightDp = 800)
@Composable
internal fun TutorialCategorySelectedPreview() {
    TutorialInteractivePreview(
        title = "어떤 분야의 코드를\n학습하고 싶나요?",
        options = tutorialCategoryOptions,
        initialSelectedOptionId = "backend",
    )
}

@Preview(name = "수준 - 선택 전", widthDp = 360, heightDp = 800)
@Composable
internal fun TutorialLevelUnselectedPreview() {
    TutorialInteractivePreview(
        title = "실제 프로젝트 코드를\n어느 정도 이해할 수 있나요?",
        options = tutorialLevelOptions,
        initialSelectedOptionId = null,
        helperText = "정답은 없어요. 현재 가장 가까운 수준을 선택해주세요.",
    )
}

@Preview(name = "수준 - 선택 후", widthDp = 360, heightDp = 800)
@Composable
internal fun TutorialLevelSelectedPreview() {
    TutorialInteractivePreview(
        title = "실제 프로젝트 코드를\n어느 정도 이해할 수 있나요?",
        options = tutorialLevelOptions,
        initialSelectedOptionId = "junior",
        helperText = "정답은 없어요. 현재 가장 가까운 수준을 선택해주세요.",
    )
}

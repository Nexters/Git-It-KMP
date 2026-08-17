package com.nexters.hytime.gitit.feature.my

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.selectcard.GitItSelectCard
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBar
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBarType
import git_it_kmp.feature.my.generated.resources.Res
import git_it_kmp.feature.my.generated.resources.settings_select_thumbnail
import org.jetbrains.compose.resources.painterResource

/**
 * 설정 선택 화면의 카드 한 장을 표현한다.
 *
 * @property id 선택 상태와 목록 key에 사용하는 화면 내 고유 식별자
 * @property title 선택지를 한 줄로 설명하는 제목
 * @property description 제목 아래에 표시할 선택적 한 줄 설명
 */
data class SettingsSelectionOption(
    val id: String,
    val title: String,
    val description: String? = null,
)

/**
 * 설정에서 개발 분야·수준 하나를 고르는 선택 화면이다.
 *
 * 선택 상태는 호출자가 [selectedOptionId]로 소유하고, 카드를 누르면 [onOptionClick]만 전달한다.
 * 저장은 뒤로가기에서 일어나므로 이 화면은 별도 확인 버튼을 두지 않는다.
 *
 * @param title 상단 큰 제목에 표시할 설정 이름
 * @param options 카드로 표시할 선택지 목록. 각 항목의 id는 화면 안에서 고유해야 한다
 * @param selectedOptionId 현재 선택된 항목 식별자. 값이 없으면 null
 * @param onOptionClick 사용자가 선택 카드를 누르면 해당 항목과 함께 호출할 이벤트
 * @param onBackClick 사용자가 상단 뒤로가기 버튼을 누르면 호출할 이벤트
 * @param modifier 화면의 크기와 외부 배치를 지정할 수식자
 */
@Composable
fun SettingsSelectionScreen(
    title: String,
    options: List<SettingsSelectionOption>,
    selectedOptionId: String?,
    onOptionClick: (SettingsSelectionOption) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(GitItTheme.colors.grey700)
                .statusBarsPadding(),
    ) {
        GitItTopBar(
            type = GitItTopBarType.LargeTitle,
            title = title,
            onBackClick = onBackClick,
            modifier = Modifier.padding(top = 8.dp),
        )
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp)
                    .selectableGroup(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                items = options,
                key = SettingsSelectionOption::id,
            ) { option ->
                GitItSelectCard(
                    title = option.title,
                    description = option.description,
                    selected = option.id == selectedOptionId,
                    onClick = { onOptionClick(option) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Image(
                        painter = painterResource(Res.drawable.settings_select_thumbnail),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun SettingsSelectionScreenPreview() {
    GitItTheme {
        SettingsSelectionScreen(
            title = "개발 분야",
            options =
                listOf(
                    SettingsSelectionOption(id = "FRONTEND", title = "Front-end"),
                    SettingsSelectionOption(id = "BACKEND", title = "Back-end"),
                    SettingsSelectionOption(id = "IOS", title = "iOS"),
                    SettingsSelectionOption(id = "ANDROID", title = "Android"),
                ),
            selectedOptionId = "BACKEND",
            onOptionClick = {},
            onBackClick = {},
        )
    }
}

@Preview
@Composable
private fun SettingsSelectionScreenWithDescriptionPreview() {
    GitItTheme {
        SettingsSelectionScreen(
            title = "개발 수준",
            options =
                listOf(
                    SettingsSelectionOption(id = "ENTRY", title = "입문", description = "프로젝트 코드를 처음 살펴봐요."),
                    SettingsSelectionOption(id = "JUNIOR", title = "주니어", description = "작은 기능 단위로 코드를 이해할 수 있어요."),
                ),
            selectedOptionId = "JUNIOR",
            onOptionClick = {},
            onBackClick = {},
        )
    }
}

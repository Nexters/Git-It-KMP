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
import git_it_kmp.feature.my.generated.resources.illust_levels_beginner
import git_it_kmp.feature.my.generated.resources.illust_levels_junior
import git_it_kmp.feature.my.generated.resources.my_career_level_entry
import git_it_kmp.feature.my.generated.resources.my_career_level_junior
import git_it_kmp.feature.my.generated.resources.my_position_backend
import git_it_kmp.feature.my.generated.resources.my_position_frontend
import git_it_kmp.feature.my.generated.resources.settings_career_level_entry_description
import git_it_kmp.feature.my.generated.resources.settings_career_level_junior_description
import git_it_kmp.feature.my.generated.resources.settings_development_field
import git_it_kmp.feature.my.generated.resources.settings_development_level
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * 설정 선택 화면의 카드 한 장을 표현한다.
 *
 * @property id 선택 상태와 목록 key에 사용하는 화면 내 고유 식별자
 * @property title 선택지를 한 줄로 설명하는 제목
 * @property thumbnail 카드 왼쪽 52dp 영역을 채울 선택적 일러스트 리소스. null이면 썸네일 영역을 표시하지 않는다
 * @property description 제목 아래에 표시할 선택적 한 줄 설명
 */
data class SettingsSelectionOption(
    val id: String,
    val title: String,
    val thumbnail: DrawableResource? = null,
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
                    thumbnail =
                        option.thumbnail?.let { resource ->
                            { SettingsSelectionThumbnail(resource = resource) }
                        },
                )
            }
        }
    }
}

/**
 * 선택 카드의 썸네일 영역을 Figma 시안대로 채운다.
 *
 * 일러스트 리소스는 배경이 없는 벡터이므로 grey500 배경 위에 겹쳐 그린다.
 *
 * @param resource 52dp 영역에 그릴 일러스트 리소스
 * @param modifier 외부 배치와 추가 수식자
 */
@Composable
private fun SettingsSelectionThumbnail(
    resource: DrawableResource,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(resource),
        contentDescription = null,
        modifier =
            modifier
                .fillMaxSize()
                .background(GitItTheme.colors.grey500),
        contentScale = ContentScale.Crop,
    )
}

@Preview
@Composable
private fun SettingsSelectionScreenPreview() {
    GitItTheme {
        SettingsSelectionScreen(
            title = stringResource(Res.string.settings_development_field),
            options =
                listOf(
                    SettingsSelectionOption(
                        id = "FRONTEND",
                        title = stringResource(Res.string.my_position_frontend),
                    ),
                    SettingsSelectionOption(
                        id = "BACKEND",
                        title = stringResource(Res.string.my_position_backend),
                    ),
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
            title = stringResource(Res.string.settings_development_level),
            options =
                listOf(
                    SettingsSelectionOption(
                        id = "ENTRY",
                        title = stringResource(Res.string.my_career_level_entry),
                        thumbnail = Res.drawable.illust_levels_beginner,
                        description = stringResource(Res.string.settings_career_level_entry_description),
                    ),
                    SettingsSelectionOption(
                        id = "JUNIOR",
                        title = stringResource(Res.string.my_career_level_junior),
                        thumbnail = Res.drawable.illust_levels_junior,
                        description = stringResource(Res.string.settings_career_level_junior_description),
                    ),
                ),
            selectedOptionId = "JUNIOR",
            onOptionClick = {},
            onBackClick = {},
        )
    }
}

package com.nexters.hytime.gitit.feature.questioncreate

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBar
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBarType
import com.nexters.hytime.gitit.domain.model.GitHubRepository
import git_it_kmp.feature.questioncreate.generated.resources.Res
import git_it_kmp.feature.questioncreate.generated.resources.ic_chevron_right
import git_it_kmp.feature.questioncreate.generated.resources.question_create_avatar_description
import git_it_kmp.feature.questioncreate.generated.resources.question_create_clear
import git_it_kmp.feature.questioncreate.generated.resources.question_create_confirm_title
import git_it_kmp.feature.questioncreate.generated.resources.question_create_help_step_1
import git_it_kmp.feature.questioncreate.generated.resources.question_create_help_step_2
import git_it_kmp.feature.questioncreate.generated.resources.question_create_help_step_3
import git_it_kmp.feature.questioncreate.generated.resources.question_create_help_step_4
import git_it_kmp.feature.questioncreate.generated.resources.question_create_help_step_5
import git_it_kmp.feature.questioncreate.generated.resources.question_create_help_title
import git_it_kmp.feature.questioncreate.generated.resources.question_create_help_toggle
import git_it_kmp.feature.questioncreate.generated.resources.question_create_invalid_url
import git_it_kmp.feature.questioncreate.generated.resources.question_create_link_label
import git_it_kmp.feature.questioncreate.generated.resources.question_create_link_placeholder
import git_it_kmp.feature.questioncreate.generated.resources.question_create_load_failed
import git_it_kmp.feature.questioncreate.generated.resources.question_create_next
import git_it_kmp.feature.questioncreate.generated.resources.question_create_reject
import git_it_kmp.feature.questioncreate.generated.resources.question_create_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * 질문 생성을 시작할 GitHub 저장소를 입력하고 확인하는 화면이다.
 *
 * @param uiState 링크 입력과 조회 결과를 포함한 화면 상태
 * @param onIntent 사용자 입력을 ViewModel로 전달하는 콜백
 * @param modifier 화면의 크기와 배치를 지정할 수식자
 */
@Composable
fun QuestionCreateScreen(
    uiState: QuestionCreateUiState,
    onIntent: (QuestionCreateIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(GitItTheme.colors.grey700)
                .statusBarsPadding()
                .navigationBarsPadding(),
    ) {
        if (uiState.repository == null) {
            RepositoryInputContent(uiState = uiState, onIntent = onIntent)
        } else {
            RepositoryConfirmationContent(
                repository = uiState.repository,
                onIntent = onIntent,
            )
        }
    }
}

/**
 * GitHub 저장소 링크 입력 단계의 콘텐츠다.
 *
 * @param uiState 링크와 오류 상태
 * @param onIntent 사용자 입력 콜백
 */
@Composable
private fun RepositoryInputContent(
    uiState: QuestionCreateUiState,
    onIntent: (QuestionCreateIntent) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        GitItTopBar(
            type = GitItTopBarType.Default,
            onBackClick = { onIntent(QuestionCreateIntent.BackClick) },
            modifier = Modifier.padding(top = 8.dp),
        )
        Spacer(Modifier.height(8.dp))
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = stringResource(Res.string.question_create_title),
                color = GitItTheme.colors.grey100,
                style = GitItTheme.typography.subtitle2,
            )
            Spacer(Modifier.height(16.dp))
            RepositoryUrlField(
                value = uiState.repositoryUrl,
                error = uiState.error,
                enabled = !uiState.isLoading,
                onValueChange = { onIntent(QuestionCreateIntent.RepositoryUrlChanged(it)) },
                onClear = { onIntent(QuestionCreateIntent.ClearRepositoryUrl) },
            )
            Spacer(Modifier.height(if (uiState.error == null) 32.dp else 10.dp))
            RepositoryLoadHelp()
        }
        Spacer(Modifier.weight(1f))
        QuestionCreateButton(
            text = stringResource(Res.string.question_create_next),
            enabled = uiState.repositoryUrl.isNotBlank() && !uiState.isLoading,
            primary = true,
            onClick = { onIntent(QuestionCreateIntent.LoadRepository) },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
        )
    }
}

/**
 * 저장소 URL 입력 필드다.
 *
 * @param value 현재 입력값
 * @param error 표시할 입력 오류
 * @param enabled 입력 가능 여부
 * @param onValueChange 입력값 변경 콜백
 * @param onClear 입력값 삭제 콜백
 */
@Composable
private fun RepositoryUrlField(
    value: String,
    error: QuestionCreateError?,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
) {
    val accentColor = if (error == null) GitItTheme.colors.blue100 else GitItTheme.colors.error

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.question_create_link_label),
                color = accentColor,
                style = GitItTheme.typography.body2,
                modifier = Modifier.width(31.dp),
            )
            Spacer(Modifier.width(16.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                enabled = enabled,
                singleLine = true,
                textStyle = GitItTheme.typography.body1.copy(color = GitItTheme.colors.grey100),
                cursorBrush = SolidColor(accentColor),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = stringResource(Res.string.question_create_link_placeholder),
                                color = GitItTheme.colors.white30,
                                style = GitItTheme.typography.body1,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        innerTextField()
                    }
                },
            )
            if (value.isNotEmpty()) {
                val clearIconColor = GitItTheme.colors.grey400
                val clearDescription = stringResource(Res.string.question_create_clear)
                Canvas(
                    modifier =
                        Modifier
                            .size(36.dp)
                            .clickable(role = Role.Button, onClick = onClear)
                            .semantics { contentDescription = clearDescription }
                            .padding(8.dp),
                ) {
                    val strokeWidth = 2.dp.toPx()
                    drawCircle(
                        color = clearIconColor,
                        radius = (size.minDimension - strokeWidth) / 2f,
                        style = Stroke(width = strokeWidth),
                    )
                    drawLine(
                        clearIconColor,
                        Offset(size.width * 0.25f, size.height * 0.25f),
                        Offset(size.width * 0.75f, size.height * 0.75f),
                        strokeWidth,
                    )
                    drawLine(
                        clearIconColor,
                        Offset(size.width * 0.75f, size.height * 0.25f),
                        Offset(size.width * 0.25f, size.height * 0.75f),
                        strokeWidth,
                    )
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(accentColor))
        error?.let {
            Text(
                text =
                    stringResource(
                        if (it == QuestionCreateError.InvalidUrl) {
                            Res.string.question_create_invalid_url
                        } else {
                            Res.string.question_create_load_failed
                        },
                    ),
                color = GitItTheme.colors.error,
                style = GitItTheme.typography.caption1,
                modifier = Modifier.padding(start = 47.dp, top = 4.dp, end = 16.dp),
            )
        }
    }
}

/** 불러오기 방법을 접고 펼칠 수 있는 안내 카드다. */
@Composable
private fun RepositoryLoadHelp() {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val steps =
        listOf(
            Res.string.question_create_help_step_1,
            Res.string.question_create_help_step_2,
            Res.string.question_create_help_step_3,
            Res.string.question_create_help_step_4,
            Res.string.question_create_help_step_5,
        )

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(GitItTheme.colors.grey600)
                .animateContentSize(),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable(role = Role.Button) { expanded = !expanded }
                    .padding(start = 20.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.question_create_help_title),
                color = GitItTheme.colors.blue100,
                style = GitItTheme.typography.body2,
                modifier = Modifier.weight(1f),
            )
            Image(
                painter = painterResource(Res.drawable.ic_chevron_right),
                contentDescription = stringResource(Res.string.question_create_help_toggle),
                modifier = Modifier.size(16.dp).padding(5.dp).rotate(if (expanded) -90f else 90f),
            )
        }
        if (expanded) {
            Column(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                steps.forEachIndexed { index, resource ->
                    HelpStep(number = index + 1, text = stringResource(resource))
                }
            }
        }
    }
}

/**
 * 불러오기 안내 한 단계를 표시한다.
 *
 * @param number 단계 번호
 * @param text 안내 문구
 */
@Composable
private fun HelpStep(
    number: Int,
    text: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(16.dp).clip(RoundedCornerShape(99.dp)).background(GitItTheme.colors.grey500),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = number.toString(),
                color = GitItTheme.colors.grey300,
                style = GitItTheme.typography.caption2,
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            color = GitItTheme.colors.grey100,
            style = GitItTheme.typography.caption1,
            maxLines = 1,
        )
    }
}

/**
 * 조회된 저장소를 최종 확인하는 콘텐츠다.
 *
 * @param repository GitHub에서 조회한 저장소
 * @param onIntent 사용자 입력 콜백
 */
@Composable
private fun RepositoryConfirmationContent(
    repository: GitHubRepository,
    onIntent: (QuestionCreateIntent) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        GitItTopBar(
            type = GitItTopBarType.Default,
            onBackClick = { onIntent(QuestionCreateIntent.BackClick) },
            modifier = Modifier.padding(top = 8.dp),
        )
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(Res.string.question_create_confirm_title),
                color = GitItTheme.colors.grey100,
                style = GitItTheme.typography.subtitle1,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(48.dp))
            RepositorySummary(repository = repository)
        }
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            QuestionCreateButton(
                text = stringResource(Res.string.question_create_next),
                enabled = true,
                primary = true,
                onClick = { onIntent(QuestionCreateIntent.ConfirmRepository) },
                modifier = Modifier.fillMaxWidth(),
            )
            QuestionCreateButton(
                text = stringResource(Res.string.question_create_reject),
                enabled = true,
                primary = false,
                onClick = { onIntent(QuestionCreateIntent.RejectRepository) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * 저장소 소유자 아바타와 이름을 표시한다.
 *
 * @param repository 표시할 저장소 정보
 */
@Composable
private fun RepositorySummary(repository: GitHubRepository) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = repository.ownerAvatarUrl,
            contentDescription = stringResource(Res.string.question_create_avatar_description),
            contentScale = ContentScale.Crop,
            modifier =
                Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(GitItTheme.colors.grey500),
        )
        Spacer(Modifier.width(21.dp))
        Column {
            Text(
                text = repository.ownerName,
                color = GitItTheme.colors.white30,
                style = GitItTheme.typography.body2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = repository.name,
                color = GitItTheme.colors.grey100,
                style = GitItTheme.typography.subtitle3,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * 화면 하단의 54dp 액션 버튼이다.
 *
 * @param text 버튼 문구
 * @param enabled 클릭 가능 여부
 * @param primary 브랜드 색상을 사용하는 주요 버튼 여부
 * @param onClick 클릭 콜백
 * @param modifier 버튼 외부 배치 수식자
 */
@Composable
private fun QuestionCreateButton(
    text: String,
    enabled: Boolean,
    primary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = if (primary && enabled) GitItTheme.colors.blue100 else GitItTheme.colors.grey500
    val contentColor =
        when {
            primary && enabled -> GitItTheme.colors.grey700
            enabled -> GitItTheme.colors.grey100
            else -> GitItTheme.colors.white30
        }

    Box(
        modifier =
            modifier
                .height(54.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(background)
                .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, color = contentColor, style = GitItTheme.typography.body1)
    }
}

@Preview
@Composable
private fun QuestionCreateInputPreview() {
    GitItTheme {
        QuestionCreateScreen(uiState = QuestionCreateUiState(), onIntent = {})
    }
}

@Preview
@Composable
private fun QuestionCreateConfirmationPreview() {
    GitItTheme {
        QuestionCreateScreen(
            uiState =
                QuestionCreateUiState(
                    repository =
                        GitHubRepository(
                            name = "react",
                            ownerName = "facebook",
                            ownerAvatarUrl = "",
                        ),
                ),
            onIntent = {},
        )
    }
}

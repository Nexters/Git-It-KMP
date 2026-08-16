package com.nexters.hytime.gitit.feature.quiz.create.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.selectcard.GitItSelectCard
import com.nexters.hytime.gitit.feature.quiz.create.QuizKnowledgeLevel
import com.nexters.hytime.gitit.feature.quiz.create.component.QuizCreateSelectionScaffold
import com.nexters.hytime.gitit.feature.quiz.create.component.QuizCreateSelectionThumbnail
import git_it_kmp.feature.quiz.generated.resources.Res
import git_it_kmp.feature.quiz.generated.resources.quiz_create_knowledge_concepts_description
import git_it_kmp.feature.quiz.generated.resources.quiz_create_knowledge_concepts_title
import git_it_kmp.feature.quiz.generated.resources.quiz_create_knowledge_experienced_description
import git_it_kmp.feature.quiz.generated.resources.quiz_create_knowledge_experienced_title
import git_it_kmp.feature.quiz.generated.resources.quiz_create_knowledge_some_code_description
import git_it_kmp.feature.quiz.generated.resources.quiz_create_knowledge_some_code_title
import git_it_kmp.feature.quiz.generated.resources.quiz_create_knowledge_title
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * 프로젝트와 기술에 대한 사용자의 이해도를 선택한다.
 *
 * @param selected 현재 선택한 이해도
 * @param onSelect 이해도 선택 콜백
 * @param onBackClick 이전 화면 이동 콜백
 * @param onNextClick 다음 화면 이동 콜백
 */
@Composable
internal fun QuizCreateKnowledgeScreen(
    selected: QuizKnowledgeLevel?,
    onSelect: (QuizKnowledgeLevel) -> Unit,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    QuizCreateSelectionScaffold(
        title = stringResource(Res.string.quiz_create_knowledge_title),
        onBackClick = onBackClick,
        buttonEnabled = selected != null,
        onNextClick = onNextClick,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            QuizKnowledgeLevel.entries.forEach { level ->
                GitItSelectCard(
                    title = stringResource(level.titleResource()),
                    description = stringResource(level.descriptionResource()),
                    selected = selected == level,
                    onClick = { onSelect(level) },
                    modifier = Modifier.fillMaxWidth(),
                    thumbnail = { QuizCreateSelectionThumbnail() },
                )
            }
        }
    }
}

/**
 * 이해도 선택 카드에 표시할 제목 리소스를 반환한다.
 *
 * @return 현재 이해도 단계에 대응하는 제목 리소스
 */
private fun QuizKnowledgeLevel.titleResource(): StringResource =
    when (this) {
        QuizKnowledgeLevel.Concepts -> Res.string.quiz_create_knowledge_concepts_title
        QuizKnowledgeLevel.SomeCode -> Res.string.quiz_create_knowledge_some_code_title
        QuizKnowledgeLevel.Experienced -> Res.string.quiz_create_knowledge_experienced_title
    }

/**
 * 이해도 선택 카드에 표시할 설명 리소스를 반환한다.
 *
 * @return 현재 이해도 단계에 대응하는 설명 리소스
 */
private fun QuizKnowledgeLevel.descriptionResource(): StringResource =
    when (this) {
        QuizKnowledgeLevel.Concepts -> Res.string.quiz_create_knowledge_concepts_description
        QuizKnowledgeLevel.SomeCode -> Res.string.quiz_create_knowledge_some_code_description
        QuizKnowledgeLevel.Experienced -> Res.string.quiz_create_knowledge_experienced_description
    }

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun QuizCreateKnowledgePreview() {
    GitItTheme {
        QuizCreateKnowledgeScreen(
            selected = QuizKnowledgeLevel.SomeCode,
            onSelect = {},
            onBackClick = {},
            onNextClick = {},
        )
    }
}

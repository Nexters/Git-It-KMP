package com.nexters.hytime.gitit.feature.quiz.create.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.selectcard.GitItSelectCard
import com.nexters.hytime.gitit.feature.quiz.create.QuizCreateTopic
import com.nexters.hytime.gitit.feature.quiz.create.component.QuizCreateSelectionScaffold
import com.nexters.hytime.gitit.feature.quiz.create.component.QuizCreateSelectionThumbnail
import git_it_kmp.feature.quiz.generated.resources.Res
import git_it_kmp.feature.quiz.generated.resources.quiz_create_topic_change_impact_description
import git_it_kmp.feature.quiz.generated.resources.quiz_create_topic_change_impact_title
import git_it_kmp.feature.quiz.generated.resources.quiz_create_topic_code_intent_description
import git_it_kmp.feature.quiz.generated.resources.quiz_create_topic_code_intent_title
import git_it_kmp.feature.quiz.generated.resources.quiz_create_topic_core_concepts_description
import git_it_kmp.feature.quiz.generated.resources.quiz_create_topic_core_concepts_title
import git_it_kmp.feature.quiz.generated.resources.quiz_create_topic_feature_flow_description
import git_it_kmp.feature.quiz.generated.resources.quiz_create_topic_feature_flow_title
import git_it_kmp.feature.quiz.generated.resources.quiz_create_topic_project_structure_description
import git_it_kmp.feature.quiz.generated.resources.quiz_create_topic_project_structure_title
import git_it_kmp.feature.quiz.generated.resources.quiz_create_topic_recommended
import git_it_kmp.feature.quiz.generated.resources.quiz_create_topics_description
import git_it_kmp.feature.quiz.generated.resources.quiz_create_topics_title
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * 생성할 문제에서 중점적으로 다룰 주제를 선택한다.
 *
 * @param selected 현재 선택한 주제 집합
 * @param onToggle 주제 선택 상태 변경 콜백
 * @param onBackClick 이전 화면 이동 콜백
 * @param onNextClick 다음 화면 이동 콜백
 */
@Composable
internal fun QuizCreateTopicsScreen(
    selected: Set<QuizCreateTopic>,
    onToggle: (QuizCreateTopic) -> Unit,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    QuizCreateSelectionScaffold(
        title = stringResource(Res.string.quiz_create_topics_title),
        description = stringResource(Res.string.quiz_create_topics_description),
        onBackClick = onBackClick,
        buttonEnabled = selected.isNotEmpty(),
        onNextClick = onNextClick,
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(QuizCreateTopic.entries, key = { it.name }) { topic ->
                GitItSelectCard(
                    title = stringResource(topic.titleResource()),
                    description = stringResource(topic.descriptionResource()),
                    tag =
                        if (topic == QuizCreateTopic.ProjectStructure || topic == QuizCreateTopic.FeatureFlow) {
                            stringResource(Res.string.quiz_create_topic_recommended)
                        } else {
                            null
                        },
                    selected = topic in selected,
                    onClick = { onToggle(topic) },
                    modifier = Modifier.fillMaxWidth(),
                    thumbnail = { QuizCreateSelectionThumbnail() },
                )
            }
        }
    }
}

/**
 * 문제 주제 카드에 표시할 제목 리소스를 반환한다.
 *
 * @return 현재 문제 주제에 대응하는 제목 리소스
 */
private fun QuizCreateTopic.titleResource(): StringResource =
    when (this) {
        QuizCreateTopic.ProjectStructure -> Res.string.quiz_create_topic_project_structure_title
        QuizCreateTopic.FeatureFlow -> Res.string.quiz_create_topic_feature_flow_title
        QuizCreateTopic.CoreConcepts -> Res.string.quiz_create_topic_core_concepts_title
        QuizCreateTopic.CodeIntent -> Res.string.quiz_create_topic_code_intent_title
        QuizCreateTopic.ChangeImpact -> Res.string.quiz_create_topic_change_impact_title
    }

/**
 * 문제 주제 카드에 표시할 설명 리소스를 반환한다.
 *
 * @return 현재 문제 주제에 대응하는 설명 리소스
 */
private fun QuizCreateTopic.descriptionResource(): StringResource =
    when (this) {
        QuizCreateTopic.ProjectStructure -> Res.string.quiz_create_topic_project_structure_description
        QuizCreateTopic.FeatureFlow -> Res.string.quiz_create_topic_feature_flow_description
        QuizCreateTopic.CoreConcepts -> Res.string.quiz_create_topic_core_concepts_description
        QuizCreateTopic.CodeIntent -> Res.string.quiz_create_topic_code_intent_description
        QuizCreateTopic.ChangeImpact -> Res.string.quiz_create_topic_change_impact_description
    }

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun QuizCreateTopicsPreview() {
    GitItTheme {
        QuizCreateTopicsScreen(
            selected = setOf(QuizCreateTopic.ProjectStructure),
            onToggle = {},
            onBackClick = {},
            onNextClick = {},
        )
    }
}

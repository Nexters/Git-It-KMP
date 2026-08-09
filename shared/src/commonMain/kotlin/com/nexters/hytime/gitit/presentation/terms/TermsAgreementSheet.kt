package com.nexters.hytime.gitit.presentation.terms

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import git_it_kmp.shared.generated.resources.Res
import git_it_kmp.shared.generated.resources.terms_agreement_all
import git_it_kmp.shared.generated.resources.terms_agreement_cancel
import git_it_kmp.shared.generated.resources.terms_agreement_next
import git_it_kmp.shared.generated.resources.terms_agreement_privacy
import git_it_kmp.shared.generated.resources.terms_agreement_service
import git_it_kmp.shared.generated.resources.terms_agreement_title
import org.jetbrains.compose.resources.stringResource

/**
 * 약관 동의 체크 상태를 보관하는 UI 상태다.
 *
 * @property isServiceAgreed 서비스 이용 약관 동의 여부
 * @property isPrivacyAgreed 개인정보 수집 동의 여부
 */
data class TermsAgreementState(
    val isServiceAgreed: Boolean = false,
    val isPrivacyAgreed: Boolean = false,
) {
    /** 필수 약관을 모두 동의했는지 반환한다. */
    val isAllAgreed: Boolean get() = isServiceAgreed && isPrivacyAgreed
}

/**
 * 체크 아이콘을 그린다.
 *
 * [checked]가 true면 `blue100` 배경, false면 20% 흰색 배경에
 * 둥근 원과 체크 마크를 그린다.
 *
 * @param checked 선택 여부
 * @param modifier 크기와 배치를 지정할 수식자
 */
@Composable
private fun CheckIcon(
    checked: Boolean,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (checked) GitItTheme.colors.blue100 else GitItTheme.colors.white15
    val checkColor = GitItTheme.colors.grey600

    Canvas(modifier = modifier.size(23.dp)) {
        drawCircle(color = backgroundColor, radius = size.minDimension / 2f)
        val path =
            Path().apply {
                moveTo(size.width * 0.30f, size.height * 0.50f)
                lineTo(size.width * 0.45f, size.height * 0.65f)
                lineTo(size.width * 0.72f, size.height * 0.35f)
            }
        drawPath(path = path, color = checkColor, style = Stroke(width = size.width * 0.12f, cap = StrokeCap.Round))
    }
}

/**
 * 우측 chevron 아이콘을 그린다.
 *
 * @param modifier 크기와 배치를 지정할 수식자
 */
@Composable
private fun ChevronIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(12.dp)) {
        val path =
            Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width * 0.5f, size.height * 0.5f)
                lineTo(0f, size.height)
            }
        drawPath(path = path, color = GitItTheme.colors.grey400, style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round))
    }
}

/**
 * 약관 동의 바텀 시트를 화면 전체에 오버레이로 표시한다.
 *
 * 반투명 배경과 함께 아래에서 올라오는 시트 형태로 약관 항목들을 보여준다.
 *
 * @param visible 시트 표시 여부
 * @param state 약관 체크 상태
* @param onDismiss 시트를 닫을 때 호출될 콜백
 * @param onToggleAllTerms 전체 동의 항목 클릭 시 호출될 콜백
 * @param onServiceClick 서비스 약관 항목 클릭 시 호출될 콜백
 * @param onPrivacyClick 개인정보 약관 항목 클릭 시 호출될 콜백
 * @param onCancelClick 취소 버튼 클릭 시 호출될 콜백
 * @param onConfirmClick 다음 버튼 클릭 시 호출될 콜백
 * @param modifier 크기와 배치를 지정할 수식자
 */
@Composable
fun TermsAgreementSheet(
    visible: Boolean,
    state: TermsAgreementState,
    onDismiss: () -> Unit,
    onToggleAllTerms: () -> Unit,
    onServiceClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(GitItTheme.colors.black70)
                    .clickable(onClick = onDismiss),
            contentAlignment = Alignment.BottomCenter,
        ) {
            TermsAgreementSheetContent(
                state = state,
                onToggleAllTerms = onToggleAllTerms,
                onServiceClick = onServiceClick,
                onPrivacyClick = onPrivacyClick,
                onCancelClick = onCancelClick,
                onConfirmClick = onConfirmClick,
            )
        }
    }
}

/**
 * 약관 동의 시트의 실제 콘텐츠 영역이다.
 *
 * Figma의 "Sheet" 컴포넌트 구조를 따라, 그래버, 타이틀, 체크리스트,
 * 하단 버튼을 세로로 배치한다.
 */
@Composable
private fun TermsAgreementSheetContent(
    state: TermsAgreementState,
    onToggleAllTerms: () -> Unit,
    onServiceClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(GitItTheme.colors.grey600)
                .clickable(enabled = false, onClick = {})
                .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Grabber
        Box(
            modifier =
                Modifier
                    .padding(top = 5.dp)
                    .width(58.dp)
                    .height(4.dp)
                    .background(GitItTheme.colors.grey400, CircleShape),
        )
        Spacer(Modifier.height(23.dp))

        // Title
        Text(
            text = stringResource(Res.string.terms_agreement_title),
            modifier = Modifier.fillMaxWidth(),
            color = GitItTheme.colors.grey100,
            style = GitItTheme.typography.subtitle1,
        )
        Spacer(Modifier.height(20.dp))

        // Check list: 전체 동의
        TermsCheckItem(
            checked = state.isAllAgreed,
            label = stringResource(Res.string.terms_agreement_all),
            showChevron = true,
            backgroundColor = GitItTheme.colors.grey500,
            onClick = onToggleAllTerms,
        )
        Spacer(Modifier.height(8.dp))

        // Check list: 서비스 이용 약관
        TermsCheckItem(
            checked = state.isServiceAgreed,
            label = stringResource(Res.string.terms_agreement_service),
            showChevron = true,
            onClick = onServiceClick,
        )
        Spacer(Modifier.height(8.dp))

        // Check list: 개인정보 수집
        TermsCheckItem(
            checked = state.isPrivacyAgreed,
            label = stringResource(Res.string.terms_agreement_privacy),
            showChevron = true,
            onClick = onPrivacyClick,
        )

        Spacer(Modifier.height(24.dp))

        // Bottom buttons: 취소, 다음
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TermsActionButton(
                label = stringResource(Res.string.terms_agreement_cancel),
                textColor = GitItTheme.colors.grey100,
                modifier = Modifier.weight(1f),
                onClick = onCancelClick,
            )
            TermsActionButton(
                label = stringResource(Res.string.terms_agreement_next),
                textColor =
                    if (state.isAllAgreed) {
                        GitItTheme.colors.grey100
                    } else {
                        GitItTheme.colors.white30
                    },
                modifier = Modifier.weight(1f),
                onClick = { if (state.isAllAgreed) onConfirmClick() },
            )
        }

        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}

/**
 * 약관 동의 개별 항목(체크박스 + 라벨 + chevron)을 그린다.
 *
 * @param checked 체크 여부
 * @param label 항목 이름
 * @param showChevron 오른쪽 chevron 표시 여부
 * @param backgroundColor 항목 배경색
 * @param onClick 항목 클릭 시 호출될 콜백
 */
@Composable
private fun TermsCheckItem(
    checked: Boolean,
    label: String,
    showChevron: Boolean,
    onClick: () -> Unit,
    backgroundColor: Color = GitItTheme.colors.grey600,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(backgroundColor)
                .clickable(onClick = onClick)
                .padding(horizontal = 17.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CheckIcon(checked = checked)
        Spacer(Modifier.width(12.dp))
        Text(
            text = label,
            color = GitItTheme.colors.grey100,
            style = GitItTheme.typography.body2,
        )
        if (showChevron) {
            Spacer(Modifier.weight(1f))
            ChevronIcon()
        }
    }
}

/**
 * 약관 동의 시트 하단의 둥근 버튼을 그린다.
 *
 * @param label 버전 텍스트
 * @param textColor 텍스트 색상
 * @param modifier 크기와 배치를 지정할 수식자
 * @param onClick 버튼 클릭 시 호출될 콜백
 */
@Composable
private fun TermsActionButton(
    label: String,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .height(54.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(GitItTheme.colors.white15)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = textColor,
            style = GitItTheme.typography.body1,
        )
    }
}

@Preview
@Composable
private fun TermsAgreementSheetPreview() {
    GitItTheme {
        var state by rememberSaveable { mutableStateOf(TermsAgreementState()) }
        TermsAgreementSheet(
            visible = true,
            state = state,
            onDismiss = {},
            onToggleAllTerms = {},
            onServiceClick = { state = state.copy(isServiceAgreed = !state.isServiceAgreed) },
            onPrivacyClick = { state = state.copy(isPrivacyAgreed = !state.isPrivacyAgreed) },
            onCancelClick = {},
            onConfirmClick = {},
        )
    }
}

package com.nexters.hytime.gitit.feature.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.feature.onboarding.terms.TermsAgreementSheet
import com.nexters.hytime.gitit.feature.onboarding.terms.TermsAgreementState
import git_it_kmp.feature.onboarding.generated.resources.Res
import git_it_kmp.feature.onboarding.generated.resources.ic_google_logo
import git_it_kmp.feature.onboarding.generated.resources.onboarding_google_login
import git_it_kmp.feature.onboarding.generated.resources.onboarding_signup_tooltip
import git_it_kmp.feature.onboarding.generated.resources.onboarding_title_1
import git_it_kmp.feature.onboarding.generated.resources.onboarding_title_2
import git_it_kmp.feature.onboarding.generated.resources.onboarding_title_3
import git_it_kmp.feature.onboarding.generated.resources.onboarding_version
import git_it_kmp.feature.onboarding.generated.resources.task2_01
import git_it_kmp.feature.onboarding.generated.resources.task3_04
import git_it_kmp.feature.onboarding.generated.resources.task4_06
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * 온보딩 한 페이지의 제목 문자열과 미리보기 이미지를 묶는다.
 *
 * @property title 페이지 상단에 표시할 제목 문자열 리소스
 * @property image 페이지 중앙에 표시할 앱 미리보기 이미지 리소스
 */
private data class OnboardingPage(
    val title: StringResource,
    val image: DrawableResource,
)

/** 온보딩에 표시할 페이지 목록이다. 순서대로 가로로 넘길 수 있다. */
private val onboardingPages: List<OnboardingPage> =
    listOf(
        OnboardingPage(Res.string.onboarding_title_1, Res.drawable.task2_01),
        OnboardingPage(Res.string.onboarding_title_2, Res.drawable.task3_04),
        OnboardingPage(Res.string.onboarding_title_3, Res.drawable.task4_06),
    )

/**
 * 앱 최초 실행 시 주요 기능을 소개하는 온보딩 화면이다.
 *
 * [OnboardingViewModel]을 직접 생성하고 단일 [OnboardingUiState]를 수집한다.
 * 약관 동의 바텀 시트 노출 여부(`showTermsSheet`)는 다이얼로그 성격의
 * UI 제어 값이므로 Composable 로컬 상태(`rememberSaveable`)로 관리한다.
 * 네비게이션 등 일회성 부작용은 [OnboardingViewModel.events]를 구독해 처리한다.
 *
 * @param onNavigateToHome 로그인 성공 후 홈 화면으로 이동하는 콜백
 */
@Composable
fun OnboardingScreen(onNavigateToHome: () -> Unit) {
    val viewModel = koinViewModel<OnboardingViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showTermsSheet by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                OnboardingEvent.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    OnboardingContent(
        uiState = uiState,
        showTermsSheet = showTermsSheet,
        onGoogleLoginClick = { showTermsSheet = true },
        onToggleAllTerms = viewModel::toggleAllTerms,
        onToggleServiceTerm = viewModel::toggleServiceTerm,
        onTogglePrivacyTerm = viewModel::togglePrivacyTerm,
        onDismissTermsSheet = { showTermsSheet = false },
        onCancelTerms = {
            showTermsSheet = false
            viewModel.resetTerms()
        },
        onConfirmTerms = {
            showTermsSheet = false
            viewModel.confirmTerms()
        },
    )
}

/**
 * 온보딩 화면의 순수 UI 렌더링을 담당한다. 상태와 콜백만 주입받아 상태를 소유하지 않는다.
 *
 * @param uiState 온보딩 단일 UI 상태
 * @param showTermsSheet 약관 동의 시트 노출 여부
 * @param onGoogleLoginClick 구글 로그인 버튼 클릭 콜백 (약관 시트 오픈)
 * @param onToggleAllTerms 전체 동의 토글 콜백
 * @param onToggleServiceTerm 서비스 약관 체크 토글 콜백
 * @param onTogglePrivacyTerm 개인정보 약관 체크 토글 콜백
 * @param onDismissTermsSheet 시트 닫기 콜백
 * @param onCancelTerms 취소 버튼 콜백
 * @param onConfirmTerms 다음 버튼 콜백
 */
@Composable
private fun OnboardingContent(
    uiState: OnboardingUiState,
    showTermsSheet: Boolean,
    onGoogleLoginClick: () -> Unit,
    onToggleAllTerms: () -> Unit,
    onToggleServiceTerm: () -> Unit,
    onTogglePrivacyTerm: () -> Unit,
    onDismissTermsSheet: () -> Unit,
    onCancelTerms: () -> Unit,
    onConfirmTerms: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(GitItTheme.colors.grey700),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(GitItTheme.colors.blue500),
            ) { pageIndex ->
                OnboardingPage(page = onboardingPages[pageIndex])
            }
            OnboardingBottomSection(
                pageCount = onboardingPages.size,
                currentPage = pagerState.currentPage,
                showTooltip = pagerState.currentPage == onboardingPages.lastIndex,
                isLoginLoading = uiState.loginStep is LoginStep.Loading,
                onGoogleLoginClick = onGoogleLoginClick,
            )
            if (uiState.loginStep is LoginStep.Error) {
                Text(
                    text = uiState.loginStep.message,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    color = GitItTheme.colors.grey100,
                    textAlign = TextAlign.Center,
                    style = GitItTheme.typography.body3,
                )
            }
            Spacer(Modifier.height(29.dp))
        }

        TermsAgreementSheet(
            visible = showTermsSheet,
            state = uiState.termsAgreement,
            onDismiss = onDismissTermsSheet,
            onToggleAllTerms = onToggleAllTerms,
            onServiceClick = onToggleServiceTerm,
            onPrivacyClick = onTogglePrivacyTerm,
            onCancelClick = onCancelTerms,
            onConfirmClick = onConfirmTerms,
        )
    }
}

/**
 * 온보딩 한 페이지의 제목과 미리보기 이미지를 세로로 배치한다.
 *
 * @param page 표시할 온보딩 페이지 데이터
 */
@Composable
private fun OnboardingPage(page: OnboardingPage) {
    Box(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Text(
            text = stringResource(page.title),
            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(top = 68.dp),
            color = GitItTheme.colors.grey100,
            textAlign = TextAlign.Center,
            style = GitItTheme.typography.subtitle1,
        )
        Image(
            painter = painterResource(page.image),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 74.dp),
        )
    }
}

/**
 * 페이지 인디케이터, 가입 유도 툴팁, 구글 로그인 버튼, 버전 정보를 담는 하단 영역이다.
 *
 * @param pageCount 전체 페이지 수
 * @param currentPage 현재 페이지 인덱스
 * @param showTooltip 가입 유도 툴팁 표시 여부
 * @param isLoginLoading 로그인 진행 중 여부
 * @param onGoogleLoginClick 구글 로그인 버튼 클릭 콜백
 */
@Composable
private fun OnboardingBottomSection(
    pageCount: Int,
    currentPage: Int,
    showTooltip: Boolean,
    isLoginLoading: Boolean,
    onGoogleLoginClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OnboardingPageIndicator(
            pageCount = pageCount,
            currentPage = currentPage,
            modifier = Modifier.padding(top = 12.dp),
        )
        Box(
            modifier = Modifier.height(57.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            if (showTooltip) {
                OnboardingSignUpTooltip()
            }
        }
        GoogleLoginButton(
            onClick = onGoogleLoginClick,
            enabled = !isLoginLoading,
        )
        Spacer(Modifier.height(21.dp))
        Text(
            text = stringResource(Res.string.onboarding_version),
            modifier = Modifier.padding(top = 12.dp),
            color = GitItTheme.colors.grey500,
            style = GitItTheme.typography.body2,
        )
        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}

/**
 * 현재 페이지 위치를 나타내는 점 인디케이터를 그린다.
 *
 * @param pageCount 전체 점 개수
 * @param currentPage 선택된 점 인덱스
 * @param modifier 인디케이터의 크기와 배치를 지정할 수식자
 */
@Composable
private fun OnboardingPageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        repeat(pageCount) { index ->
            val color =
                if (index == currentPage) {
                    GitItTheme.colors.grey100
                } else {
                    GitItTheme.colors.white30
                }
            Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        }
    }
}

/**
 * 구글 계정으로 시작하는 로그인 버튼이다.
 *
 * @param onClick 버튼 클릭 콜백
 * @param enabled 버튼 활성화 여부
 * @param modifier 버튼의 크기와 배치를 지정할 수식자
 */
@Composable
private fun GoogleLoginButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(54.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(GitItTheme.colors.grey100)
                .clickable(enabled = enabled, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_google_logo),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = stringResource(Res.string.onboarding_google_login),
            color = GitItTheme.colors.grey600,
            style = GitItTheme.typography.body1,
        )
    }
}

/**
 * 마지막 페이지에서 구글 로그인 버튼에 표시하는 가입 유도 툴팁이다.
 *
 * @param modifier 툴팁의 크기와 배치를 지정할 수식자
 */
@Composable
private fun OnboardingSignUpTooltip(modifier: Modifier = Modifier) {
    val backgroundColor = GitItTheme.colors.grey600
    Column(
        modifier = modifier.padding(bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .background(backgroundColor, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(Res.string.onboarding_signup_tooltip),
                color = GitItTheme.colors.grey100,
                style = GitItTheme.typography.body3,
            )
        }
        Canvas(
            modifier =
                Modifier
                    .size(width = 12.dp, height = 8.dp)
                    .offset(y = (-1).dp),
        ) {
            val path =
                Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width / 2f, size.height)
                    close()
                }
            drawPath(path = path, color = backgroundColor)
        }
    }
}

@Preview
@Composable
private fun OnboardingContentPreview() {
    GitItTheme {
        OnboardingContent(
            uiState = OnboardingUiState(),
            showTermsSheet = false,
            onGoogleLoginClick = {},
            onToggleAllTerms = {},
            onToggleServiceTerm = {},
            onTogglePrivacyTerm = {},
            onDismissTermsSheet = {},
            onCancelTerms = {},
            onConfirmTerms = {},
        )
    }
}

package com.nexters.hytime.gitit.designsystem.navigation

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassNavBar
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassNavBarItem
import com.skydoves.cloudy.Sky
import com.skydoves.cloudy.rememberSky
import com.skydoves.cloudy.sky
import git_it_kmp.core.designsystem.generated.resources.Res
import git_it_kmp.core.designsystem.generated.resources.ic_bookmark_filled
import git_it_kmp.core.designsystem.generated.resources.ic_nav_bookmark
import git_it_kmp.core.designsystem.generated.resources.ic_nav_file_text
import git_it_kmp.core.designsystem.generated.resources.ic_nav_home
import git_it_kmp.core.designsystem.generated.resources.ic_nav_user
import org.jetbrains.compose.resources.painterResource

/** 앱 하단 탭바의 목적지다. */
enum class GitItMainNavDestination {
    /** 홈 화면. */
    Home,

    /** 프로젝트 리스트 화면. */
    Project,

    /** 저장한 문제 화면. */
    Saved,

    /** 마이 화면. */
    My,
}

/**
 * 앱 공통 하단 탭바를 렌더링한다.
 *
 * @param selectedDestination 현재 선택된 목적지
 * @param onDestinationClick 탭을 눌렀을 때 목적지를 전달하는 콜백
 * @param modifier 탭바의 외부 배치와 추가 수식자
 * @param sky 흐림 배경을 캡처하는 Cloudy 상태. null이면 정적 배경만 그린다
 */
@Composable
fun GitItMainNavBar(
    selectedDestination: GitItMainNavDestination,
    onDestinationClick: (GitItMainNavDestination) -> Unit,
    modifier: Modifier = Modifier,
    sky: Sky? = null,
) {
    val items = mainNavItems()

    GitItLiquidGlassNavBar(
        items =
            items.map { item ->
                GitItLiquidGlassNavBarItem(
                    label = item.label,
                    icon = item.icon,
                )
            },
        selectedIndex = items.indexOfFirst { it.destination == selectedDestination }.coerceAtLeast(0),
        onSelectedIndexChange = { index -> onDestinationClick(items[index].destination) },
        modifier = modifier.navigationBarsPadding(),
        sky = sky,
    )
}

/**
 * 하단 탭바에 사용할 Cloudy 상태를 만든다.
 *
 * Compose Preview에서는 Cloudy 렌더링이 깨질 수 있어 null을 반환하고,
 * 실제 실행 환경에서만 리퀴드 글래스 캡처를 활성화한다.
 *
 * @return 실제 실행 환경의 [Sky], Preview에서는 null
 */
@Composable
fun rememberGitItMainNavSky(): Sky? =
    if (LocalInspectionMode.current) {
        null
    } else {
        rememberSky()
    }

/**
 * [sky]가 있을 때만 현재 Modifier를 Cloudy 캡처 대상으로 만든다.
 *
 * @param sky 흐림 배경을 캡처하는 Cloudy 상태. null이면 원본 Modifier를 반환한다
 * @return Cloudy 캡처가 조건부로 적용된 Modifier
 */
@Composable
fun Modifier.gitItMainNavSky(sky: Sky?): Modifier =
    if (sky == null) {
        this
    } else {
        sky(sky)
    }

/** 하단 탭바의 내부 항목이다. */
private data class MainNavItem(
    /** 이동할 목적지. */
    val destination: GitItMainNavDestination,
    /** 탭 아래에 표시할 라벨. */
    val label: String,
    /** 탭 상단에 표시할 아이콘. */
    val icon: @Composable () -> Unit,
)

/**
 * 하단 탭바 항목 목록을 만든다.
 *
 * @return 홈·프로젝트·저장·마이 순서의 항목
 */
private fun mainNavItems(): List<MainNavItem> =
    listOf(
        MainNavItem(GitItMainNavDestination.Home, "홈") { HomeIcon() },
        MainNavItem(GitItMainNavDestination.Project, "프로젝트") { FileTextIcon() },
        MainNavItem(GitItMainNavDestination.Saved, "저장") { GitItBookmarkIcon() },
        MainNavItem(GitItMainNavDestination.My, "마이") { UserIcon() },
    )

/**
 * 홈 탭 아이콘을 그린다.
 */
@Composable
private fun HomeIcon() {
    Icon(painterResource(Res.drawable.ic_nav_home), contentDescription = null, modifier = Modifier.size(width = 18.214.dp, height = 20.dp))
}

/**
 * 프로젝트 탭 아이콘을 그린다.
 */
@Composable
private fun FileTextIcon() {
    Icon(
        painterResource(Res.drawable.ic_nav_file_text),
        contentDescription = null,
        modifier = Modifier.size(width = 16.428.dp, height = 20.dp),
    )
}

/**
 * 북마크 아이콘을 그린다.
 *
 * @param modifier 아이콘의 크기와 배치를 지정할 수식자
 * @param filled 내부를 채워 저장 상태를 강조할지 여부
 */
@Composable
fun GitItBookmarkIcon(
    modifier: Modifier = Modifier,
    filled: Boolean = false,
) {
    Icon(
        painterResource(if (filled) Res.drawable.ic_bookmark_filled else Res.drawable.ic_nav_bookmark),
        contentDescription = null,
        modifier =
            if (filled) {
                modifier.size(20.dp)
            } else {
                modifier.size(width = 16.078.dp, height = 20.dp)
            },
    )
}

/**
 * 마이 탭 아이콘을 그린다.
 */
@Composable
private fun UserIcon() {
    Icon(painterResource(Res.drawable.ic_nav_user), contentDescription = null, modifier = Modifier.size(width = 18.039.dp, height = 20.dp))
}

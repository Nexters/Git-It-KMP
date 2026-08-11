package com.nexters.hytime.gitit.designsystem.navigation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassNavBar
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassNavBarItem
import com.skydoves.cloudy.Sky
import com.skydoves.cloudy.rememberSky
import com.skydoves.cloudy.sky

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
    val color = LocalContentColor.current
    Canvas(modifier = Modifier.size(20.dp)) {
        val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val roof =
            Path().apply {
                moveTo(size.width * 0.18f, size.height * 0.46f)
                lineTo(size.width * 0.5f, size.height * 0.2f)
                lineTo(size.width * 0.82f, size.height * 0.46f)
            }
        val body =
            Path().apply {
                moveTo(size.width * 0.26f, size.height * 0.42f)
                lineTo(size.width * 0.26f, size.height * 0.82f)
                lineTo(size.width * 0.74f, size.height * 0.82f)
                lineTo(size.width * 0.74f, size.height * 0.42f)
            }
        drawPath(roof, color = color, style = stroke)
        drawPath(body, color = color, style = stroke)
        drawLine(
            color = color,
            start = Offset(size.width * 0.5f, size.height * 0.82f),
            end = Offset(size.width * 0.5f, size.height * 0.62f),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

/**
 * 프로젝트 탭 아이콘을 그린다.
 */
@Composable
private fun FileTextIcon() {
    val color = LocalContentColor.current
    Canvas(modifier = Modifier.size(20.dp)) {
        val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val file =
            Path().apply {
                moveTo(size.width * 0.26f, size.height * 0.14f)
                lineTo(size.width * 0.58f, size.height * 0.14f)
                lineTo(size.width * 0.78f, size.height * 0.34f)
                lineTo(size.width * 0.78f, size.height * 0.86f)
                lineTo(size.width * 0.26f, size.height * 0.86f)
                close()
            }
        drawPath(file, color = color, style = stroke)
        drawLine(
            color = color,
            start = Offset(size.width * 0.58f, size.height * 0.14f),
            end = Offset(size.width * 0.58f, size.height * 0.34f),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.58f, size.height * 0.34f),
            end = Offset(size.width * 0.78f, size.height * 0.34f),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(color, Offset(size.width * 0.38f, size.height * 0.5f), Offset(size.width * 0.66f, size.height * 0.5f), 1.8.dp.toPx())
        drawLine(color, Offset(size.width * 0.38f, size.height * 0.64f), Offset(size.width * 0.66f, size.height * 0.64f), 1.8.dp.toPx())
    }
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
    val color = LocalContentColor.current
    Canvas(modifier = modifier.size(20.dp)) {
        val bookmark =
            Path().apply {
                moveTo(size.width * 0.3f, size.height * 0.16f)
                lineTo(size.width * 0.7f, size.height * 0.16f)
                lineTo(size.width * 0.7f, size.height * 0.84f)
                lineTo(size.width * 0.5f, size.height * 0.68f)
                lineTo(size.width * 0.3f, size.height * 0.84f)
                close()
            }
        if (filled) {
            drawPath(path = bookmark, color = color)
        } else {
            drawPath(
                path = bookmark,
                color = color,
                style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
        }
    }
}

/**
 * 마이 탭 아이콘을 그린다.
 */
@Composable
private fun UserIcon() {
    val color = LocalContentColor.current
    Canvas(modifier = Modifier.size(20.dp)) {
        val strokeWidth = 1.8.dp.toPx()
        drawCircle(
            color = color,
            radius = size.minDimension * 0.16f,
            center = Offset(size.width * 0.5f, size.height * 0.28f),
            style = Stroke(width = strokeWidth),
        )
        drawArc(
            color = color,
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(size.width * 0.25f, size.height * 0.48f),
            size = Size(size.width * 0.5f, size.height * 0.42f),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
        )
    }
}

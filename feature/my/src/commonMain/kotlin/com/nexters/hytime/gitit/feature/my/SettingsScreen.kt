package com.nexters.hytime.gitit.feature.my

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.GitItTheme
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBar
import com.nexters.hytime.gitit.designsystem.toolbar.GitItTopBarType
import git_it_kmp.feature.my.generated.resources.Res
import git_it_kmp.feature.my.generated.resources.settings_delete_account
import git_it_kmp.feature.my.generated.resources.settings_development_field
import git_it_kmp.feature.my.generated.resources.settings_development_field_value
import git_it_kmp.feature.my.generated.resources.settings_development_level
import git_it_kmp.feature.my.generated.resources.settings_development_level_value
import git_it_kmp.feature.my.generated.resources.settings_general
import git_it_kmp.feature.my.generated.resources.settings_learning
import git_it_kmp.feature.my.generated.resources.settings_logout
import git_it_kmp.feature.my.generated.resources.settings_notification
import git_it_kmp.feature.my.generated.resources.settings_on
import git_it_kmp.feature.my.generated.resources.settings_policy
import git_it_kmp.feature.my.generated.resources.settings_set_created_notification
import git_it_kmp.feature.my.generated.resources.settings_title
import org.jetbrains.compose.resources.stringResource

private const val POLICY_URL =
    "https://app.notion.com/p/Git-it-3bb7221e5fe78005bcd9fab953906df1?source=copy_link"

/**
 * 학습 환경과 계정 관련 메뉴를 표시하는 설정 화면이다.
 *
 * @param onBackClick 이전 화면으로 돌아가는 콜백
 * @param modifier 화면의 크기와 배치를 지정할 수식자
 */
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(GitItTheme.colors.grey700)
                .statusBarsPadding(),
    ) {
        GitItTopBar(
            type = GitItTopBarType.LargeTitle,
            title = stringResource(Res.string.settings_title),
            onBackClick = onBackClick,
            modifier = Modifier.padding(top = 8.dp),
        )
        Spacer(Modifier.height(10.dp))

        SettingsSection(title = stringResource(Res.string.settings_learning)) {
            SettingsRow(
                icon = SettingsIconType.Develop,
                label = stringResource(Res.string.settings_development_field),
                value = stringResource(Res.string.settings_development_field_value),
            )
            SettingsDivider()
            SettingsRow(
                icon = SettingsIconType.Level,
                label = stringResource(Res.string.settings_development_level),
                value = stringResource(Res.string.settings_development_level_value),
            )
        }

        SettingsSection(title = stringResource(Res.string.settings_notification)) {
            SettingsRow(
                icon = SettingsIconType.Alert,
                label = stringResource(Res.string.settings_set_created_notification),
                value = stringResource(Res.string.settings_on),
            )
        }

        SettingsSection(title = stringResource(Res.string.settings_general)) {
            SettingsRow(
                icon = SettingsIconType.Policy,
                label = stringResource(Res.string.settings_policy),
                onClick = { uriHandler.openUri(POLICY_URL) },
            )
            SettingsDivider()
            SettingsRow(
                icon = SettingsIconType.Logout,
                label = stringResource(Res.string.settings_logout),
                labelColor = GitItTheme.colors.error,
            )
            SettingsDivider()
            SettingsRow(
                icon = SettingsIconType.Delete,
                label = stringResource(Res.string.settings_delete_account),
                labelColor = GitItTheme.colors.grey400,
            )
        }
    }
}

/**
 * 제목과 둥근 메뉴 그룹으로 구성된 설정 섹션이다.
 *
 * @param title 섹션의 분류 이름
 * @param modifier 외부 배치와 추가 수식자
 * @param content 그룹 안에 표시할 설정 항목
 */
@Composable
private fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier.padding(top = 10.dp, start = 20.dp, end = 20.dp)) {
        Text(
            text = title,
            color = GitItTheme.colors.grey400,
            style = GitItTheme.typography.caption2,
            modifier = Modifier.padding(bottom = 10.dp),
        )
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(GitItTheme.colors.grey600),
            content = content,
        )
    }
}

/**
 * 아이콘, 이름, 현재 값과 이동 표시를 한 줄로 보여준다.
 *
 * @param icon 항목의 의미에 맞는 아이콘 종류
 * @param label 설정 항목 이름
 * @param modifier 외부 배치와 추가 수식자
 * @param value 현재 선택값. 없으면 이름만 표시한다
 * @param labelColor 항목 이름 색상
 * @param onClick 선택 시 실행할 동작. null이면 읽기 전용으로 표시한다
 */
@Composable
private fun SettingsRow(
    icon: SettingsIconType,
    label: String,
    modifier: Modifier = Modifier,
    value: String = "",
    labelColor: Color = GitItTheme.colors.grey100,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(56.dp)
                .then(if (onClick == null) Modifier else Modifier.clickable(onClick = onClick))
                .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsItemIcon(type = icon)
        Spacer(Modifier.size(12.dp))
        Text(
            text = label,
            color = labelColor,
            style = GitItTheme.typography.body2,
            modifier = Modifier.weight(1f),
        )
        if (value.isNotEmpty()) {
            Text(
                text = value,
                color = GitItTheme.colors.grey400,
                style = GitItTheme.typography.body2,
            )
            Spacer(Modifier.size(6.dp))
        }
        SettingsChevron()
    }
}

/** 메뉴 그룹 안에서 인접한 설정 항목을 구분한다. */
@Composable
private fun SettingsDivider() {
    Spacer(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(GitItTheme.colors.grey500),
    )
}

/** 설정 메뉴에서 사용하는 아이콘 종류다. */
private enum class SettingsIconType {
    /** 개발 환경을 나타내는 모니터. */
    Develop,

    /** 개발 수준을 나타내는 막대 차트. */
    Level,

    /** 알림 시간을 나타내는 시계. */
    Alert,

    /** 서비스 정책 문서. */
    Policy,

    /** 로그아웃 동작. */
    Logout,

    /** 계정 삭제 동작. */
    Delete,
}

/**
 * 설정 메뉴의 선형 아이콘을 Compose Canvas로 그린다.
 *
 * @param type 그릴 아이콘 종류
 * @param modifier 외부 배치와 추가 수식자
 */
@Composable
private fun SettingsItemIcon(
    type: SettingsIconType,
    modifier: Modifier = Modifier,
) {
    val color =
        when (type) {
            SettingsIconType.Logout -> GitItTheme.colors.error
            SettingsIconType.Delete -> GitItTheme.colors.grey400
            else -> GitItTheme.colors.blue100
        }

    Canvas(modifier = modifier.size(24.dp)) {
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        when (type) {
            SettingsIconType.Develop -> {
                drawRoundRect(
                    color,
                    Offset(2f, 3f) * density,
                    size.copy(
                        width = size.width - 4.dp.toPx(),
                        height =
                            size.height - 8.dp.toPx(),
                    ),
                    style = stroke,
                )
                drawLine(
                    color,
                    Offset(size.width / 2f, size.height - 5.dp.toPx()),
                    Offset(size.width / 2f, size.height - 1.dp.toPx()),
                    stroke.width,
                )
                drawLine(
                    color,
                    Offset(size.width * 0.3f, size.height - 1.dp.toPx()),
                    Offset(size.width * 0.7f, size.height - 1.dp.toPx()),
                    stroke.width,
                )
            }
            SettingsIconType.Level ->
                listOf(0.3f to 0.6f, 0.5f to 0.25f, 0.7f to 0.45f).forEach { (x, top) ->
                    drawLine(
                        color,
                        Offset(size.width * x, size.height * top),
                        Offset(size.width * x, size.height * 0.85f),
                        stroke.width,
                        StrokeCap.Round,
                    )
                }
            SettingsIconType.Alert -> {
                drawCircle(color, size.minDimension * 0.38f, style = stroke)
                drawLine(color, center, Offset(center.x, size.height * 0.28f), stroke.width, StrokeCap.Round)
                drawLine(color, center, Offset(size.width * 0.67f, size.height * 0.62f), stroke.width, StrokeCap.Round)
            }
            SettingsIconType.Policy -> {
                val path =
                    Path().apply {
                        moveTo(size.width * 0.25f, size.height * 0.08f)
                        lineTo(size.width * 0.62f, size.height * 0.08f)
                        lineTo(size.width * 0.82f, size.height * 0.3f)
                        lineTo(size.width * 0.82f, size.height * 0.9f)
                        lineTo(size.width * 0.25f, size.height * 0.9f)
                        close()
                        moveTo(size.width * 0.62f, size.height * 0.08f)
                        lineTo(size.width * 0.62f, size.height * 0.3f)
                        lineTo(size.width * 0.82f, size.height * 0.3f)
                    }
                drawPath(path, color, style = stroke)
            }
            SettingsIconType.Logout -> {
                drawLine(
                    color,
                    Offset(size.width * 0.22f, size.height * 0.2f),
                    Offset(size.width * 0.22f, size.height * 0.8f),
                    stroke.width,
                    StrokeCap.Round,
                )
                drawLine(
                    color,
                    Offset(size.width * 0.22f, size.height * 0.2f),
                    Offset(size.width * 0.45f, size.height * 0.2f),
                    stroke.width,
                    StrokeCap.Round,
                )
                drawLine(
                    color,
                    Offset(size.width * 0.22f, size.height * 0.8f),
                    Offset(size.width * 0.45f, size.height * 0.8f),
                    stroke.width,
                    StrokeCap.Round,
                )
                drawLine(color, Offset(size.width * 0.4f, center.y), Offset(size.width * 0.85f, center.y), stroke.width, StrokeCap.Round)
                drawLine(
                    color,
                    Offset(size.width * 0.68f, size.height * 0.34f),
                    Offset(size.width * 0.85f, center.y),
                    stroke.width,
                    StrokeCap.Round,
                )
                drawLine(
                    color,
                    Offset(size.width * 0.68f, size.height * 0.66f),
                    Offset(size.width * 0.85f, center.y),
                    stroke.width,
                    StrokeCap.Round,
                )
            }
            SettingsIconType.Delete -> {
                drawLine(
                    color,
                    Offset(size.width * 0.3f, size.height * 0.3f),
                    Offset(size.width * 0.7f, size.height * 0.7f),
                    stroke.width,
                    StrokeCap.Round,
                )
                drawLine(
                    color,
                    Offset(size.width * 0.7f, size.height * 0.3f),
                    Offset(size.width * 0.3f, size.height * 0.7f),
                    stroke.width,
                    StrokeCap.Round,
                )
            }
        }
    }
}

/** 설정 항목 우측의 이동 표시를 그린다. */
@Composable
private fun SettingsChevron(modifier: Modifier = Modifier) {
    val color = GitItTheme.colors.grey400
    Canvas(modifier = modifier.size(16.dp)) {
        val path =
            Path().apply {
                moveTo(size.width * 0.4f, size.height * 0.25f)
                lineTo(size.width * 0.65f, size.height * 0.5f)
                lineTo(size.width * 0.4f, size.height * 0.75f)
            }
        drawPath(path, color, style = Stroke(1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

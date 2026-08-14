package com.nexters.hytime.gitit.designsystem.icons

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nexters.hytime.gitit.designsystem.liquidglass.GitItLiquidGlassIconButtonSize
import git_it_kmp.core.designsystem.generated.resources.Res
import git_it_kmp.core.designsystem.generated.resources.ic_back
import git_it_kmp.core.designsystem.generated.resources.ic_back_small
import org.jetbrains.compose.resources.painterResource

/**
 * 뒤로가기 용도의 chevron left 아이콘을 그린다.
 *
 * @param size 아이콘을 올릴 리퀴드 글래스 버튼 크기
 * @param modifier 아이콘의 외부 배치와 추가 수식자
 */
@Composable
fun GitItBackIcon(
    size: GitItLiquidGlassIconButtonSize,
    modifier: Modifier = Modifier,
) {
    val isMedium = size == GitItLiquidGlassIconButtonSize.Md
    Icon(
        painter = painterResource(if (isMedium) Res.drawable.ic_back else Res.drawable.ic_back_small),
        contentDescription = null,
        modifier = modifier.size(width = if (isMedium) 8.5.dp else 7.dp, height = if (isMedium) 14.5.dp else 12.dp),
    )
}

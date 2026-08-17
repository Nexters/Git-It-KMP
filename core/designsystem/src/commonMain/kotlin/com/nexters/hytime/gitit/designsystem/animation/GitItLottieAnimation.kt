package com.nexters.hytime.gitit.designsystem.animation

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import git_it_kmp.core.designsystem.generated.resources.Res
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter

/** 디자인 시스템에서 제공하는 Lottie 애니메이션 리소스다. */
enum class GitItAnimation(
    internal val resourcePath: String,
) {
    /** 프로젝트 목록의 빈 상태 애니메이션이다. */
    ProjectEmpty("files/animation_project_empty.json"),

    /** 저장한 문제 목록의 빈 상태 애니메이션이다. */
    StorageEmpty("files/animation_storage_empty.json"),

    /** 문제 풀이 완료 애니메이션이다. */
    Complete("files/animation_complete.json"),

    /** 공통 로딩 애니메이션이다. */
    GeneralLoading("files/animation_general_loading.json"),

    /** 문제 생성 진행 애니메이션이다. */
    SetCreationLoading("files/animation_set_creation_loading.json"),

    /** 알림 설정 안내 애니메이션이다. */
    Notification("files/animation_notification.json"),
}

/**
 * 디자인 시스템의 로컬 JSON 애니메이션을 재생한다.
 *
 * @param animation 화면에 표시할 애니메이션 리소스
 * @param modifier 애니메이션의 크기와 배치를 지정할 수식자
 * @param loop 화면에 표시되는 동안 반복 재생할지 여부
 * @param contentDescription 의미가 있는 애니메이션의 접근성 설명. 장식용이면 null
 */
@Composable
fun GitItLottieAnimation(
    animation: GitItAnimation,
    modifier: Modifier = Modifier,
    loop: Boolean = false,
    contentDescription: String? = null,
) {
    val composition by
        rememberLottieComposition {
            LottieCompositionSpec.JsonString(Res.readBytes(animation.resourcePath).decodeToString())
        }

    Image(
        painter =
            rememberLottiePainter(
                composition = composition,
                iterations = if (loop) Compottie.IterateForever else 1,
            ),
        contentDescription = contentDescription,
        modifier = modifier,
    )
}

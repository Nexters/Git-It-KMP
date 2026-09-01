// 플러그인 버전 관리와 적용은 `build-logic` included build가 담당한다.
// 각 모듈은 `gitit.*` 컨벤션 플러그인을 통해 필요한 설정을 가져간다.
plugins {
    alias(libs.plugins.gitit.kotzilla.root)
}

# AGENTS.md

이 파일은 AI 코딩 에이전트(Claude Code, Codex 등)를 위한 프로젝트 지침입니다.
`CLAUDE.md`는 이 파일을 가리키는 심볼릭 링크이므로, 지침 수정은 **항상 `AGENTS.md`에서** 합니다.

## 프로젝트 개요

Git-It-KMP는 개발자가 오픈소스 코드 구조와 개발 맥락을 AI 생성 질문으로 학습하도록 돕는
Kotlin Multiplatform 앱입니다. **Android**와 **Desktop(JVM)** 두 플랫폼을 지원하며
UI는 Compose Multiplatform(Material 3)으로 공유합니다.

- 패키지 루트: `com.nexters.hytime.gitit`
- JDK: 21 (Gradle Daemon JVM도 21로 고정, `gradle/gradle-daemon-jvm.properties`)
- 문서·주석·커밋 메시지는 한국어로 작성합니다.

## 자주 쓰는 명령

```bash
# Android 앱 빌드
./gradlew :androidApp:assembleDebug

# Desktop 앱 실행
./gradlew :desktopApp:run

# 테스트
./gradlew :shared:testAndroidHostTest   # Android host test
./gradlew :shared:jvmTest               # Desktop(JVM) test
./gradlew :shared:allTests              # 전 타겟 테스트 + 통합 리포트

# 코드 스타일 (CI가 ktlintCheck를 강제)
./gradlew ktlintCheck --continue
./gradlew ktlintFormat
```

작업을 끝내기 전에 최소한 `ktlintCheck`와 변경한 모듈의 테스트를 통과시킵니다.

## 모듈 구조

```
androidApp/          Android 진입점 (MainActivity)
desktopApp/          Desktop 진입점 (Main.kt, Compose Desktop)
shared/              공유 UI + 앱 로직 (KMP: commonMain/androidMain/jvmMain)
feature/home/         홈 화면 기능 (KMP + Compose)
core/designsystem/   디자인 시스템 (KMP + Compose)
core/auth/           Google 로그인 인증 추상화 (KMP: commonMain/androidMain/jvmMain)
core/network/        Ktor 기반 HTTP 클라이언트 (순수 JVM)
data/                Repository 구현체, API DTO, DTO↔도메인 매핑 (순수 JVM)
domain/              도메인 레이어 (순수 JVM)
build-logic/         Gradle 컨벤션 플러그인 (included build)
```

의존 방향: `androidApp`/`desktopApp` → `shared` → `feature:*` / `core:*` / `domain` / `data`.
역방향 의존이나 `core` 모듈 간 순환 의존을 만들지 않습니다.

모듈 의존성은 타입 세이프 프로젝트 액세서(`enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")`)로
참조합니다. 문자열 표기(`project(":core:network")`)는 쓰지 않습니다.

```kotlin
implementation(projects.core.designsystem)
```

## 빌드 규칙

### 컨벤션 플러그인으로만 설정한다

플러그인 버전 관리와 공통 설정은 전부 `build-logic`에 있습니다. 모듈의 `build.gradle.kts`에는
`gitit.*` 컨벤션 플러그인 하나와 그 모듈만의 의존성을 둡니다. compileSdk, minSdk, jvmTarget,
ktlint 설정 등을 개별 모듈에 중복 선언하지 않습니다.

| 플러그인 | 용도 |
| --- | --- |
| `gitit.kmp.library` | Android + JVM 타겟 KMP 라이브러리 |
| `gitit.kmp.library.compose` | 위 + Compose Multiplatform |
| `gitit.jvm.library` | 순수 Kotlin/JVM 라이브러리 (Android SDK 접근 불가) |
| `gitit.jvm.compose` | Compose Desktop 애플리케이션 |
| `gitit.android.application` | Android 애플리케이션 |
| `gitit.ktlint` | ktlint (다른 컨벤션 플러그인이 자동 적용) |

플랫폼에 의존하지 않아야 하는 모듈(`domain`, `core:network`)은 `gitit.jvm.library`를 써서
Android SDK를 참조할 수 없게 만드는 것이 의도된 제약입니다. 이 제약을 우회하지 않습니다.

### 새 모듈 추가 절차

1. `settings.gradle.kts`에 `include(":...")` 추가
2. 모듈 디렉터리에 `build.gradle.kts` 생성, 알맞은 `gitit.*` 플러그인 적용
3. KMP 모듈이면 모듈 이름이 그대로 Android namespace가 됩니다
   (`com.nexters.hytime.gitit.<모듈명>`). **모듈 이름에 하이픈을 쓰지 않습니다.**

### 의존성은 버전 카탈로그로만

새 라이브러리는 `gradle/libs.versions.toml`에 등록한 뒤 `libs.*` 접근자로 씁니다.
버전 문자열을 `build.gradle.kts`에 직접 적지 않습니다. 카탈로그는 주석으로 구획(`# Network` 등)이
나뉘어 있으니 맞는 구획에 추가합니다.

JVM 버전은 카탈로그의 `javaVersion` 하나만이 소스입니다. 변경이 필요하면
`build-logic/convention/src/main/kotlin/Jvm.kt`의 `configureJvmTarget()`을 통해 반영합니다.

## 코드 스타일

**Kotlin 공식 코딩 컨벤션(<https://kotlinlang.org/docs/coding-conventions.html>)을 따릅니다.**
`gradle.properties`의 `kotlin.code.style=official`, `.editorconfig`의 `ktlint_code_style = ktlint_official`이
이를 기계적으로 강제하고, CI(`.github/workflows/ktlint.yml`)가 `ktlintCheck`로 검사합니다.
공식 컨벤션과 이 문서가 충돌하면 이 문서의 예외 항목이 우선합니다.

- 인디케이션 4칸, 최대 줄 길이 140
- 네이밍: 클래스 PascalCase, 함수·프로퍼티 camelCase, 상수 SCREAMING_SNAKE_CASE
- 프로젝트 예외 1 — 와일드카드 import 허용 (Compose 관례)
- 프로젝트 예외 2 — `@Composable` 함수는 PascalCase 허용
- yml/json/xml은 2칸 인디케이션
- 생성 소스(`build/` 하위, Compose Resources 등)는 ktlint 대상에서 제외됨 — 직접 수정하지 않습니다

### KDoc 주석

**클래스, 함수, 프로퍼티에는 KDoc 주석을 작성합니다.** 태그(`@param`, `@return`, `@throws`,
`@property`)로 각 요소를 명시하고, 한국어로 씁니다.

```kotlin
/**
 * 저장소의 커밋 이력을 기반으로 학습용 질문을 생성한다.
 *
 * @param repositoryId 질문을 생성할 저장소 식별자
 * @param level 사용자 학습 수준. 값이 높을수록 어려운 질문이 나온다
 * @return 생성된 질문 목록. 생성할 내용이 없으면 빈 목록
 * @throws IllegalArgumentException [level]이 1..5 범위를 벗어난 경우
 */
fun generateQuestions(
    repositoryId: String,
    level: Int,
): List<Question>
```

- 생성자 프로퍼티는 클래스 KDoc에서 `@property`로 문서화합니다.
- 반환 타입이 `Unit`이면 `@return`을 생략합니다.
- **예외: Compose 프리뷰(`@Preview`가 붙은 Composable)에는 KDoc을 쓰지 않습니다.**
  그 외 일반 `@Composable` 함수는 KDoc 대상이며, 파라미터는 `@param`으로 설명합니다.
- 시그니처를 그대로 옮겨 적는 주석(`@param name 이름`)은 쓰지 않습니다. 의도·제약·단위처럼
  코드에서 안 보이는 정보를 적습니다.
- 기존 코드 중 KDoc이 없는 파일이 남아 있습니다. 일괄 정비는 별도 작업으로 다루고,
  지금은 새로 추가하거나 수정하는 선언에 KDoc을 붙입니다.

## KMP 소스셋 규칙

- 기본은 `commonMain`입니다. 플랫폼 API가 꼭 필요할 때만 `expect`/`actual`로 내립니다.
- 파일 네이밍은 기존 패턴을 따릅니다: `Platform.kt` / `Platform.android.kt` / `Platform.jvm.kt`
- 테스트: 공통 로직은 `commonTest`, 플랫폼 검증은 `androidHostTest` / `jvmTest`
- 데스크톱 타겟 이름은 `jvm`입니다 (`desktop`이 아님).

## 레이어 구조와 의존 방향

MVI + Clean Architecture를 지향합니다. 도입 예정 스택은 Koin(DI), Coil, Navigation 3, Sentry입니다.
아직 코드에 없는 라이브러리를 임의로 끌어오지 말고, 필요하면 먼저 제안합니다.

현재 `domain`, `core:designsystem`은 `build.gradle.kts`만 있고 소스가 비어 있습니다.
아래는 첫 코드가 들어갈 때부터 지킬 규칙입니다.

| 레이어 | 위치 | 책임 |
| --- | --- | --- |
| domain | `domain` | 도메인 모델, UseCase, Repository **인터페이스**. 다른 모듈에 의존하지 않음 |
| data | `data` | Repository **구현체**, API 정의, DTO, DTO↔도메인 매핑, 캐싱·재시도 정책 |
| network | `core:network` | Ktor 클라이언트와 공통 HTTP 설정. Ktor를 아는 유일한 곳 |
| presentation | `shared` | Compose UI, ViewModel, UiState |

의존 방향은 `presentation → domain ← data → network` 입니다. 안쪽(`domain`)은 바깥을 모릅니다.

### 데이터 레이어

`data` 모듈은 이미 신설되어 있다. Repository 구현체를 `core:network`나 `shared`에 두지 않는다.
새 Repository 구현체가 필요하면 `data` 모듈에 추가한다. 모듈을 만들 때는
[새 모듈 추가 절차](#새-모듈-추가-절차)를 따르고, 플랫폼 비의존을 강제하도록 `gitit.jvm.library`를 적용한다.

### 레이어 규칙

- Repository 인터페이스는 `domain`에, 구현체·API 정의·DTO(`@Serializable`)는 `data`에 둡니다.
  `shared`는 Repository 인터페이스에만 의존합니다.
- `data`는 DTO를 도메인 모델로 매핑하고, DTO를 UI나 UseCase까지 흘려보내지 않습니다.
- `core:network`의 공개 API에는 Ktor 타입을 노출하지 않습니다. `data`는
  `implementation(projects.core.network)`만 참조하며 Ktor에 직접 의존하지 않습니다.
- `domain`은 Ktor·Compose·Android를 몰라야 합니다 (`gitit.jvm.library`가 이미 빌드로 강제).
- `core:network`은 Ktor 예외를 자체 네트워크 오류 타입으로 변환합니다. `data`는 HTTP 상태·응답 본문과
  네트워크 오류를 도메인 타입(`Result` 등)으로 변환하며, Ktor 예외가 ViewModel까지 올라오지 않게 합니다.
- 모듈을 새로 나눌지 애매하면 먼저 제안하고 확인을 받습니다.

## 테스트 규칙

- 새 테스트는 `commonTest`를 기본으로 합니다. `androidHostTest` / `jvmTest`는 플랫폼별 동작을
  검증할 때만 씁니다 (`expect`/`actual` 구현 등).
- `kotlin.test`(`kotlin.test.Test`, `assertEquals`)를 씁니다. JUnit을 직접 import 하지 않습니다.
- 테스트 함수는 `대상_조건_기대결과` 형태의 camelCase로 씁니다
  (예: `generateQuestions_레벨이범위밖이면_예외를던진다`). 백틱 함수명은 쓰지 않습니다.
- 테스트 대상은 분기·경계 조건·매핑 로직입니다. `assertEquals(3, 1 + 2)` 같은 자명한 검증은
  추가하지 않습니다.
- Composable 렌더링 테스트는 아직 도입하지 않았습니다. 필요하면 먼저 제안합니다.

## 작업 프로토콜

- **완료 전 검증**: `./gradlew ktlintFormat` → `./gradlew ktlintCheck` → 변경한 모듈의 테스트.
  실행한 명령과 결과를 보고합니다. 실패했으면 실패했다고 그대로 말합니다.
- 첫 빌드가 오래 걸리므로 전체 `./gradlew build` 대신 변경한 모듈의 태스크만 실행합니다
  (예: `:shared:jvmTest`).
- 요청 범위 밖의 리팩터링, 의존성 추가, 라이브러리 버전 업그레이드는 하지 않습니다. 필요해 보이면
  제안만 하고 확인을 받습니다.
- 여러 파일을 건드리는 작업은 먼저 영향 받는 모듈을 확인하고 시작합니다.

## AI 지침 파일 유지

- **`CLAUDE.md`는 `AGENTS.md`를 가리키는 심볼릭 링크입니다. 절대 직접 편집하거나 덮어쓰지
  않습니다.** 덮어쓰면 링크가 끊어지고 지침이 두 벌로 갈라집니다. 수정은 항상 `AGENTS.md`에서 합니다.
- 하위 디렉터리에도 지침이 있습니다 (예: `shared/AGENTS.md`). 해당 디렉터리에서 작업할 때는
  하위 지침이 이 문서보다 우선합니다.
- 모듈을 추가·삭제하면 `settings.gradle.kts`, 이 문서의 [모듈 구조](#모듈-구조), README의
  기술 스택 표를 함께 갱신합니다.

## Git / PR 규칙

- 브랜치: `<type>/Task-<번호>-<요약>` (예: `feature/Task-19-kmp-logging`, `chore/Task-22-ktlint-workflow`)
- 커밋 메시지: `<type>: <한국어 요약>` (`feat`, `fix`, `refactor`, `chore`, `docs`, `test`, `style`, `perf`)
- 커밋 본문: 한 줄을 비운 뒤 `-` 목록으로 변경 배경, 선택 이유, 검증 내용을 작성합니다.
  `Constraint:`, `Rejected:`, `Confidence:`, `Scope-risk:`, `Directive:`, `Tested:`, `Not-tested:` 같은 trailer 패턴은 사용하지 않습니다.
- PR 제목: `Feature: [Task-10] 유저 프로필 스크린 추가` 형식. 템플릿은 `.github/pull_request_template.md`
- PR 대상 브랜치는 `main`이고 CODEOWNERS 리뷰가 붙습니다.
- 커밋과 푸시는 사용자가 요청할 때만 합니다.

## 주의사항

- `local.properties`, `.gradle/`, `build/`, `.idea/`는 건드리지 않습니다.
- Gradle 설정 캐시(`org.gradle.configuration-cache=true`)가 켜져 있습니다. 빌드 스크립트에서
  설정 시점에 환경을 읽는 코드는 캐시를 깨뜨리니 피합니다.
- 빌드 산출물이 남은 디렉터리(예: 소스 없이 `build/`만 있는 모듈)를 실제 모듈로 착각하지 않습니다.
  모듈의 실체는 `settings.gradle.kts`의 `include` 목록입니다.

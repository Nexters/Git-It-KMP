# shared 모듈 지침

`shared`는 Android와 Desktop이 공유하는 **프레젠테이션 레이어**입니다. Compose UI, ViewModel,
UiState가 여기 있습니다. 루트 `AGENTS.md`의 규칙을 전제로 하고, 아래 내용이 우선합니다.

> 현재 `App.kt`, `Greeting.kt`, `GreetingUtil.kt`는 KMP 템플릿 잔재이며 아래 규칙을 따르지
> 않습니다. 참고 예시로 삼지 말고, 해당 화면을 실제로 구현할 때 규칙에 맞게 교체합니다.

## Compose UI 작성 규칙

### Modifier

- `modifier: Modifier = Modifier`를 **첫 번째 옵셔널 파라미터**로 받습니다. 이름은 항상 `modifier`.
- 받은 `modifier`는 최상위 레이아웃에 **가장 먼저** 적용하고, 내부 전용 수식자를 뒤에 이어 붙입니다.
- 자식에게 `modifier`를 그대로 넘기지 않습니다. 크기·배치는 호출하는 쪽이 정합니다.

```kotlin
@Composable
fun QuestionCard(
    question: Question,
    onAnswerClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(16.dp)) { /* ... */ }
}
```

### 상태 호이스팅

- Composable은 상태를 소유하지 않습니다. **상태는 파라미터로 받고, 이벤트는 람다로 올립니다.**
- `remember { mutableStateOf(...) }`는 애니메이션·스크롤처럼 UI에만 의미 있는 상태에 한정합니다.
  화면의 비즈니스 상태는 ViewModel의 UiState에 둡니다.
- Composable 파라미터로 ViewModel을 받지 않습니다. Route에서 상태를 꺼내 stateless Screen에
  넘기는 2단 구조를 씁니다 ([MVI 구조](#mvi-상태-관리) 참고).

### 디자인 토큰

- 색상·타이포·간격은 `core:designsystem` 또는 `MaterialTheme`에서 가져옵니다.
- `Color(0xFF...)`, `16.sp` 같은 값을 화면 코드에 직접 쓰지 않습니다. 필요한 토큰이 없으면
  `core:designsystem`에 추가하고 그걸 씁니다.
- `core:designsystem`은 아직 비어 있습니다. 첫 화면을 만들 때 테마·토큰부터 그쪽에 정의합니다.

### 프리뷰

- 프리뷰는 대상 Composable과 **같은 파일 하단**에 모읍니다.
- `@Preview @Composable private fun XxxPreview()` 형태로 쓰고, 실제 데이터 대신 고정된 샘플 값을 넘깁니다.
- 프리뷰 함수에는 KDoc을 쓰지 않습니다 (루트 지침의 KDoc 예외 항목).

### 재구성

- 람다 파라미터는 매 재구성마다 새로 만들지 말고 안정적인 참조를 넘깁니다.
- `derivedStateOf`, `key()` 등은 실제로 재구성 문제가 확인됐을 때만 씁니다. 예방적으로 두르지 않습니다.
- `LazyColumn` 등 리스트에는 안정적인 `key`를 지정합니다.

## MVI 상태 관리

`shared`에 `lifecycle-viewmodel-compose`와 `lifecycle-runtime-compose`가 이미 들어와 있습니다.

### 파일 배치와 네이밍

기능 단위로 `com.nexters.hytime.gitit.feature.<기능>` 패키지에 모읍니다.

```
feature/quiz/
  QuizViewModel.kt     UiState 보유, Intent 처리
  QuizContract.kt      QuizUiState / QuizIntent / QuizSideEffect
  QuizRoute.kt         ViewModel 주입, 상태 수집 → QuizScreen 호출
  QuizScreen.kt        stateless Composable
```

- 상태: `XxxUiState` (data class, 화면 하나당 하나)
- 사용자 의도: `XxxIntent` (`sealed interface`)
- 1회성 이벤트: `XxxSideEffect` (`sealed interface`) — 토스트, 화면 이동 등

### 데이터 흐름

- ViewModel은 `StateFlow<XxxUiState>` 하나만 노출합니다. 상태 조각을 여러 Flow로 쪼개지 않습니다.
- UI는 `collectAsStateWithLifecycle()`로 구독합니다. `collectAsState()`는 쓰지 않습니다.
- 사용자 입력은 `onIntent(intent: XxxIntent)` **단일 진입점**으로 받습니다. 액션마다 public
  메서드를 만들지 않습니다.
- SideEffect는 `Channel`(또는 `SharedFlow`)로 내보내고 UI에서 `LaunchedEffect`로 소비합니다.
  상태에 담아 소비 후 초기화하는 방식은 쓰지 않습니다.
- 로딩·에러는 별도 Boolean 난립 대신 UiState 안에서 표현합니다
  (예: `sealed interface`로 `Loading` / `Success` / `Error`).
- ViewModel은 `domain`의 UseCase·Repository **인터페이스**에만 의존합니다. Ktor나 DTO를 직접
  건드리지 않습니다.

## 문자열·리소스 규칙

- **공유 문자열은 Compose Resources를 씁니다.**
  `shared/src/commonMain/composeResources/values/strings.xml`에 정의하고 `Res.string.*`로 참조합니다.
  (현재 `composeResources`에는 drawable만 있습니다. 문자열을 처음 추가할 때 `values/strings.xml`을 만듭니다.)
- 이미지·아이콘도 `composeResources/drawable`에 둡니다.
- `androidApp/src/main/res`는 앱 이름, 런처 아이콘처럼 **플랫폼 전용 리소스만** 둡니다.
  화면에 표시되는 문자열을 여기 넣지 않습니다.
- UI 코드에 문자열을 하드코딩하지 않습니다. 로그 메시지와 테스트 픽스처는 예외입니다.
- 생성된 `Res` 클래스(`git_it_kmp.shared.generated.resources`)는 빌드 산출물입니다. 직접 수정하지 않습니다.

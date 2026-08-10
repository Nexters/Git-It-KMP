# Git-It-KMP

개발자가 오픈 소스 코드 구조와 핵심 개발 맥락을 AI가 생성한 질문을 기반으로 학습할 수 있도록 돕는 것을 목표로 하는 Kotlin Multiplatform 앱입니다.

## 지원 플랫폼

이 프로젝트는 Kotlin Multiplatform과 Compose Multiplatform을 기반으로 Android와 Desktop 환경을 함께 지원합니다.

## Pain Point

![Forgetting Curve](https://upload.wikimedia.org/wikipedia/commons/thumb/4/4e/ForgettingCurve.svg/500px-ForgettingCurve.svg.png)

- 복습하지 않으면 빠르게 감소하는 기억 유지율
- 에빙하우스 망각 곡선으로 설명되는 반복 학습의 필요성
- repository 이해를 질문과 피드백으로 다시 떠올리는 학습 경험 필요

## 주요 기능 목표
- 사용자 수준과 프로젝트 이해도 기반 학습 설정
- 프로젝트 구조, 기능 흐름, 기술 개념, 코드 의도 기반 맞춤형 질문 생성
- 객관식, 짧은 서술형 답변 채점 및 코드 출처 기반 피드백
- 틀린 문제 중심 복습

## 기술 스택

| 구분 | 기술 | 상태 |
| --- | --- | --- |
| 아키텍처 | MVI, Clean Architecture | 설계 방향 |
| 상태 관리 | MVI 기반 UI State | 설계 방향 |
| 구조 | Kotlin Multiplatform | 현재 구성 |
| 플랫폼 | Android, Desktop(JVM) | 현재 구성 |
| UI | Compose Multiplatform, Material 3 | 현재 구성 |
| 기능 모듈 | feature:home, feature:onboarding, feature:projectlist, feature:projectdetail | 현재 구성 |
| 빌드 구성 | Gradle Kotlin DSL, Version Catalog | 현재 구성 |
| Android | Android Gradle Plugin, Activity Compose | 현재 구성 |
| Desktop | Compose Desktop | 현재 구성 |
| 권한 | Android 알림 런타임 권한, macOS UserNotifications(JNA 브리지) | 현재 구성 |
| 테스트 | Kotlin Test | 현재 구성 |
| 비동기 | Coroutine, Flow, Kotlinx Coroutines Swing | 일부 구성 / 도입 예정 |
| 의존성 주입 | Koin | 도입 예정 |
| 이미지 로딩 | Coil | 도입 예정 |
| 화면 전환 | Navigation 3 | 현재 구성 |
| 에러 트래킹 | Sentry | 도입 예정 |

## 시작하기

### 요구 사항

- Android Studio 최신 안정 버전
- JDK 21 이상 (Daemon JVM Java 21)
- Kotlin Multiplatform 플러그인

### 실행

Android 앱 실행:

```bash
./gradlew :androidApp:assembleDebug
```

Desktop 앱 실행:

```bash
./gradlew :desktopApp:run
```

Desktop 앱 Hot Reload 실행:

```bash
./gradlew :desktopApp:hotRun --auto
```

### 테스트

Android host test:

```bash
./gradlew :shared:testAndroidHostTest
```

Desktop test:

```bash
./gradlew :shared:jvmTest
```

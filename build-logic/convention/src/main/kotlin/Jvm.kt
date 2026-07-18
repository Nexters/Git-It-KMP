import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

// JVM 버전 설정을 이 파일 한 곳에 모은다. 값은 카탈로그의 javaVersion 하나뿐이다.

/** Java source/target 호환 레벨. Android 모듈의 compileOptions가 이 값을 참조한다. */
val Project.javaVersion: JavaVersion
    get() = JavaVersion.toVersion(the<LibrariesForLibs>().versions.javaVersion.get())

private val Project.jvmTargetVersion: JvmTarget
    get() = JvmTarget.fromTarget(the<LibrariesForLibs>().versions.javaVersion.get())

/**
 * 모든 JVM 컴파일 태스크에 바이트코드 레벨을 적용한다.
 *
 * 태스크 기반이라 플러그인 종류(순수 JVM / Android / KMP)와 무관하게 동일하게 동작한다.
 * 다만 Android의 compileOptions(source/targetCompatibility)만은 AGP가 android 확장에서
 * 직접 읽으므로, Android 모듈은 그 블록에서 [javaVersion]을 따로 선언해야 한다.
 */
fun Project.configureJvmTarget() {
    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(jvmTargetVersion)
        }
    }
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
    }
}

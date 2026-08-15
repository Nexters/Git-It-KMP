package com.nexters.hytime.gitit.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.test.Test
import kotlin.test.assertNotNull

/** 앱 경로의 다형 직렬화 등록을 검증한다. */
class AppNavHostTest {
    /** ProjectDelete 경로를 NavKey로 직렬화할 때 subtype 누락 예외가 발생하지 않는다. */
    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun appRouteSavedStateConfiguration_projectDeleteRoute_returnsSerializer() {
        val serializer =
            appRouteSavedStateConfiguration.serializersModule.getPolymorphic(
                NavKey::class,
                AppRoute.ProjectDelete,
            )

        assertNotNull(serializer)
    }

    /** Quiz 경로를 NavKey로 직렬화할 때 subtype 누락 예외가 발생하지 않는다. */
    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun appRouteSavedStateConfiguration_quizRoute_returnsSerializer() {
        val serializer =
            appRouteSavedStateConfiguration.serializersModule.getPolymorphic(
                NavKey::class,
                AppRoute.Quiz(projectId = "project-1", setId = "set-1"),
            )

        assertNotNull(serializer)
    }
}

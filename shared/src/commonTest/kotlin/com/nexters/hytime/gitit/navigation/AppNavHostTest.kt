package com.nexters.hytime.gitit.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.elementNames
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** 앱 경로의 다형 직렬화 등록을 검증한다. */
@OptIn(ExperimentalSerializationApi::class)
class AppNavHostTest {
    /**
     * [AppRoute]의 모든 서브타입이 NavKey 다형 스코프에 등록되어 있는지 확인한다.
     *
     * 등록이 빠지면 해당 화면이 백스택에 있는 상태로 앱이 백그라운드로 갈 때 크래시하므로,
     * 경로를 추가하고 [appRouteSavedStateConfiguration] 등록을 잊으면 이 테스트가 실패한다.
     */
    @Test
    fun appRouteSavedStateConfiguration_allRoutes_returnsSerializer() {
        // sealed 직렬화 디스크립터는 [type, value] 두 원소이고, value 하위에 서브타입이 나열된다.
        val subclassSerialNames =
            AppRoute
                .serializer()
                .descriptor
                .getElementDescriptor(1)
                .elementNames
                .toList()

        assertTrue(subclassSerialNames.isNotEmpty(), "AppRoute 서브타입 목록을 읽지 못했다")
        subclassSerialNames.forEach { serialName ->
            val serializer =
                appRouteSavedStateConfiguration.serializersModule.getPolymorphic(
                    NavKey::class,
                    serialName,
                )

            assertNotNull(serializer, "$serialName 이 NavKey 다형 스코프에 등록되지 않았다")
        }
    }
}

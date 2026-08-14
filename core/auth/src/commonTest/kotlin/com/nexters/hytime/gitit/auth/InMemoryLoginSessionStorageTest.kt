package com.nexters.hytime.gitit.auth

import com.nexters.hytime.gitit.domain.model.LoginSession
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** [InMemoryLoginSessionStorage]의 저장 수명과 삭제 동작을 검증한다. */
class InMemoryLoginSessionStorageTest {
    /** 세션을 메모리에 저장하고 삭제할 수 있는지 검증한다. */
    @Test
    fun saveAndClear_sessionUpdatesMemory() =
        runBlocking {
            val storage = InMemoryLoginSessionStorage()
            val session = LoginSession("access-token", "refresh-token", false)

            storage.save(session)
            assertEquals(session, storage.load())

            storage.clear()
            assertNull(storage.load())
        }
}

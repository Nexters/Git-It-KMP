package com.nexters.hytime.gitit

import co.touchlab.kermit.Logger
import com.nexters.hytime.gitit.logging.logI

class Greeting(
    private val logger: Logger,
) {
    private val platform = getPlatform()

    fun greet(): String {
        logger.logI { "greet() 호출 - platform: ${platform.name}" }
        return sayHello(platform.name)
    }
}

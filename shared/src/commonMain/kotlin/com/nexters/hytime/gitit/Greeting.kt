package com.nexters.hytime.gitit

import com.nexters.hytime.gitit.logging.AppLogger

class Greeting(
    private val logger: AppLogger,
) {
    private val platform = getPlatform()

    fun greet(): String {
        logger.i { "greet() 호출 - platform: ${platform.name}" }
        return sayHello(platform.name)
    }
}

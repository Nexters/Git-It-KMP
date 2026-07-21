package com.nexters.hytime.gitit

class Greeting {
    private val platform = getPlatform()

    fun greet(): String = sayHello(platform.name)
}

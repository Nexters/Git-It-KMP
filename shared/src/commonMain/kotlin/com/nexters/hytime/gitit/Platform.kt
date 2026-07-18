package com.nexters.hytime.gitit

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
package com.nexters.hytime.gitit.network.logging

/** 네트워크 통신 로그를 기록하는 외부 로거다. */
fun interface NetworkLogger {
    /**
     * 네트워크 통신 로그를 기록한다.
     *
     * @param message 기록할 로그 메시지
     */
    fun log(message: String)
}

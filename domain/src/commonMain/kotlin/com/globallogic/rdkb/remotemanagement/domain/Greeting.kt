package com.globallogic.rdkb.remotemanagement.domain

class Greeting(
    private val platform: Platform
) {
    fun greet(): String = "Hello, ${platform.name}!"
}

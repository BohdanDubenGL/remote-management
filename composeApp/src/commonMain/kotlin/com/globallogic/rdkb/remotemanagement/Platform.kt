package com.globallogic.rdkb.remotemanagement

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
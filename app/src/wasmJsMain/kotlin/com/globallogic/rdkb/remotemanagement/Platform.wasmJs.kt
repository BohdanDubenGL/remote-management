package com.globallogic.rdkb.remotemanagement

import com.globallogic.rdkb.remotemanagement.domain.Platform

actual fun getPlatform(): Platform = object : Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

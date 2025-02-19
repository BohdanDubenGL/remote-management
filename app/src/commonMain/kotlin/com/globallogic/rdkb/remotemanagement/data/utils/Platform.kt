package com.globallogic.rdkb.remotemanagement.data.utils

enum class Platform {
    Android, Ios, Desktop;

    companion object
}

expect val Platform.Companion.current: Platform

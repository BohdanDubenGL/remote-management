package com.globallogic.rdkb.remotemanagement

import platform.UIKit.UIDevice
import com.globallogic.rdkb.remotemanagement.domain.Platform

actual fun getPlatform(): Platform = object : Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

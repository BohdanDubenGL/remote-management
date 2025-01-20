package com.globallogic.rdkb.remotemanagement

import android.os.Build
import com.globallogic.rdkb.remotemanagement.domain.Platform

actual fun getPlatform(): Platform = object : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

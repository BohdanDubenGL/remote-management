package com.globallogic.rdkb.remotemanagement

import com.globallogic.rdkb.remotemanagement.domain.Greeting
import com.globallogic.rdkb.remotemanagement.domain.Platform

expect fun getPlatform(): Platform

fun platformGreeting(): Greeting = Greeting(platform = getPlatform())

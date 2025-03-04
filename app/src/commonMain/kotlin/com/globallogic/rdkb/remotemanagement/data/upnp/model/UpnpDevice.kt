package com.globallogic.rdkb.remotemanagement.data.upnp.model

data class UpnpDevice(
    val ip: String,
    val port: Int,
    val location: String,
)

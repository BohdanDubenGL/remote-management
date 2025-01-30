package com.globallogic.rdkb.remotemanagement.domain.entity

data class RouterDevice(
    val name: String,
    val ip: String,
    val macAddress: String
) {
    companion object {
        val empty: RouterDevice = RouterDevice("", "", "")
    }
}

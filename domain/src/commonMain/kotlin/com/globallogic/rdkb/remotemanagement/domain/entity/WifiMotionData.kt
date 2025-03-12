package com.globallogic.rdkb.remotemanagement.domain.entity

data class WifiMotionData(
    val currentHostMacAddress: String,
    val motionPercent: Int,
    val events: List<WifiMotionEvent>,
    val hosts: List<ConnectedDevice>,
) {
    val selectedHost: ConnectedDevice? get() = hosts.firstOrNull { it.macAddress == currentHostMacAddress }
    val isRunning: Boolean get() = currentHostMacAddress.isNotBlank()
}

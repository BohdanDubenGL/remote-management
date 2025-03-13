package com.globallogic.rdkb.remotemanagement.domain.entity

data class WifiMotionData(
    val currentHostMacAddress: String,
    val motionPercent: Int,
    val events: List<WifiMotionEvent>,
    val clients: List<AccessPointClient>,
) {
    val selectedHost: AccessPointClient? get() = clients.firstOrNull { it.macAddress == currentHostMacAddress }
    val isRunning: Boolean get() = currentHostMacAddress.isNotBlank()
}

package com.globallogic.rdkb.remotemanagement.domain.entity

data class RouterDeviceTopologyData(
    val lanConnected: Boolean,
    val routerDevice: RouterDevice,
    val connectedDevices: List<ConnectedDevice>
) {
    companion object {
        val empty: RouterDeviceTopologyData = RouterDeviceTopologyData(true, RouterDevice.empty, emptyList())
    }
}

package com.globallogic.rdkb.remotemanagement.domain.entity

data class TopologyData(
    val routerDevice: RouterDevice,
    val connectedDevices: List<ConnectedDevice>,
)

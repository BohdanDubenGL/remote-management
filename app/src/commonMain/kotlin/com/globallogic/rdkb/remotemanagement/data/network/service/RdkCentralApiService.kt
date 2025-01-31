package com.globallogic.rdkb.remotemanagement.data.network.service

interface RdkCentralApiService {
    suspend fun getRouterDeviceProperty(
        routerDeviceMacAddress: String = "dca6320eb8bb", // todo: remove real mac
        routerDeviceProperty: RouterDeviceProperty,
    ): String
}

sealed class RouterDeviceProperty(val name: String) {
    data object SoftwareVersion : RouterDeviceProperty(name = "Device.DeviceInfo.SoftwareVersion")
}

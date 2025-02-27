package com.globallogic.rdkb.remotemanagement.data.datasource

import com.globallogic.rdkb.remotemanagement.data.error.IoDeviceError
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

interface LocalRouterDeviceDataSource {

    suspend fun loadRouterDevicesForUser(userEmail: String): Resource<List<RouterDevice>, IoDeviceError.LoadRouterDevicesForUser>

    suspend fun findRouterDeviceByMacAddress(macAddress: String): Resource<RouterDevice, IoDeviceError.FindRouterDeviceByMacAddress>

    suspend fun saveConnectedDevices(device: RouterDevice, connectedDevices: List<ConnectedDevice>): Resource<Unit, IoDeviceError.SaveConnectedDevices>

    suspend fun loadConnectedDevices(device: RouterDevice): Resource<List<ConnectedDevice>, IoDeviceError.LoadConnectedDevices>

    suspend fun saveRouterDevice(device: RouterDevice, userEmail: String): Resource<Unit, IoDeviceError.SaveRouterDevice>

    suspend fun removeRouterDevice(device: RouterDevice, userEmail: String): Resource<Unit, IoDeviceError.RemoveRouterDevice>
}

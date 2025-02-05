package com.globallogic.rdkb.remotemanagement.data.datasource

import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceInfo
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceTopologyData

interface LocalRouterDeviceDataSource {

    suspend fun loadRouterDevicesForUser(userEmail: String): Result<List<RouterDevice>>

    suspend fun findRouterDeviceByMacAddress(macAddress: String): Result<RouterDevice?>

    suspend fun saveConnectedDevices(device: RouterDevice, connectedDevices: List<ConnectedDevice>): Result<Unit>

    suspend fun loadConnectedDevices(device: RouterDevice): Result<List<ConnectedDevice>>

    suspend fun loadDeviceInfo(device: RouterDevice): Result<RouterDeviceInfo?>

    suspend fun loadTopologyData(device: RouterDevice): Result<RouterDeviceTopologyData?>

    suspend fun saveRouterDevice(device: RouterDeviceInfo, userEmail: String): Result<Unit>

    suspend fun removeRouterDevice(device: RouterDevice, userEmail: String): Result<Unit>

    suspend fun findLocalRouterDevice(userEmail: String): Result<RouterDevice?>
}

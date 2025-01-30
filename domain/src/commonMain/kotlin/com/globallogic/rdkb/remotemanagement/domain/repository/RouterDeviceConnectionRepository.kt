package com.globallogic.rdkb.remotemanagement.domain.repository

import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice

interface RouterDeviceConnectionRepository {
    suspend fun connectToRouterDevice(device: FoundRouterDevice): RouterDevice
    suspend fun addRouterDeviceManually(macAddress: String): RouterDevice

    suspend fun getDeviceList(): List<RouterDevice>
    suspend fun searchRouterDevices(): List<FoundRouterDevice>
}

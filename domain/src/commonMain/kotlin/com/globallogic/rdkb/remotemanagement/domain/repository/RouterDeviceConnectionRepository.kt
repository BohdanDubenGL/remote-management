package com.globallogic.rdkb.remotemanagement.domain.repository

import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice

interface RouterDeviceConnectionRepository {
    suspend fun connectToRouterDevice(device: FoundRouterDevice): Unit = Unit

    suspend fun getDeviceList(): List<RouterDevice> = emptyList()

    suspend fun searchRouterDevices(): List<FoundRouterDevice> = emptyList()

    companion object {
        val empty: RouterDeviceConnectionRepository = object : RouterDeviceConnectionRepository { }
    }
}

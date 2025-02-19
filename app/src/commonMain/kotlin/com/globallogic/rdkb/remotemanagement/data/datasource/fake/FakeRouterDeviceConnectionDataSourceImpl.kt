package com.globallogic.rdkb.remotemanagement.data.datasource.fake

import com.globallogic.rdkb.remotemanagement.data.datasource.RouterDeviceConnectionDataSource
import com.globallogic.rdkb.remotemanagement.data.error.IoDeviceError
import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.flatMapData
import com.globallogic.rdkb.remotemanagement.domain.utils.map

fun RouterDeviceConnectionDataSource.fake(): RouterDeviceConnectionDataSource =
    FakeRouterDeviceConnectionDataSourceImpl(this)

private class FakeRouterDeviceConnectionDataSourceImpl(
    private val original: RouterDeviceConnectionDataSource
) : RouterDeviceConnectionDataSource by original {
    private val hardcodedDevice: FakeRouterDevice = FakeRouterDevice(
        name = "Hardcoded Router",
        macAddress = "dc:a6:32:0e:b8:bb",
        ipAddress = "192.168.1.150",
    )

    override suspend fun findAvailableRouterDevices(): Resource<List<FoundRouterDevice>, IoDeviceError.NoAvailableRouterDevices> {
        return Success(listOf(hardcodedDevice.toFoundRouterDevice()))
            .flatMapData { fake ->
                original.findAvailableRouterDevices().map { fake + it }
            }
    }

    override suspend fun connectToRouterDevice(macAddress: String): Resource<RouterDevice, IoDeviceError.CantConnectToRouterDevice> {
        return when (macAddress) {
            hardcodedDevice.macAddress -> Success(hardcodedDevice.toRouterDevice())
            else -> original.connectToRouterDevice(macAddress)
        }
    }

    private data class FakeRouterDevice(
        val name: String = "Controller",
        val macAddress: String = "9a:1a:22:49:73:3c",
        val ipAddress: String = "192.168.1.150",
    ) {
        fun toFoundRouterDevice(): FoundRouterDevice = FoundRouterDevice(name, ipAddress, macAddress)
        fun toRouterDevice(): RouterDevice = RouterDevice(name, ipAddress, macAddress)
    }
}

package com.globallogic.rdkb.remotemanagement.data.datasource.fake

import com.globallogic.rdkb.remotemanagement.data.datasource.RouterDeviceConnectionDataSource
import com.globallogic.rdkb.remotemanagement.domain.entity.FoundRouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.utils.runCatchingSafe

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

    override suspend fun findAvailableRouterDevices(): Result<List<FoundRouterDevice>> = runCatchingSafe {
        buildList {
            add(hardcodedDevice.toFoundRouterDevice())
            addAll(original.findAvailableRouterDevices().getOrThrow())
        }
    }

    override suspend fun connectToRouterDevice(macAddress: String): Result<RouterDevice?> {
        return if (macAddress == hardcodedDevice.macAddress) {
            Result.success(hardcodedDevice.toRouterDevice())
        } else {
            original.connectToRouterDevice(macAddress)
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

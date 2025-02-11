package com.globallogic.rdkb.remotemanagement.data.datasource.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.RemoteRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.error.IoDeviceError
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralApiService
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceInfo
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.buildResource

class RemoteRouterDeviceDataSourceImpl(
    private val centralApiService: RdkCentralApiService
) : RemoteRouterDeviceDataSource {
    override suspend fun loadRouterDeviceInfo(device: RouterDevice): Resource<RouterDeviceInfo, IoDeviceError.NoDeviceInfoFound> = buildResource {
        return failure(IoDeviceError.NoDeviceInfoFound)
    }

    override suspend fun loadConnectedDevicesForRouterDevice(device: RouterDevice): Resource<List<ConnectedDevice>, IoDeviceError.LoadConnectedDevicesForRouterDevice> = buildResource {
        return success(emptyList())
    }

    override suspend fun factoryResetDevice(device: RouterDevice): Resource<Unit, IoDeviceError.FactoryResetDevice> = buildResource {
        return success(Unit)
    }

    override suspend fun restartDevice(device: RouterDevice): Resource<Unit, IoDeviceError.RestartDevice> = buildResource {
        return success(Unit)
    }

    override suspend fun setupDevice(device: RouterDevice, settings: RouterDeviceSettings): Resource<Unit, IoDeviceError.SetupDevice> = buildResource {
        return success(Unit)
    }
}

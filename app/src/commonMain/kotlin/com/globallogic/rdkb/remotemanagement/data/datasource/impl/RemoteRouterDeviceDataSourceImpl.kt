package com.globallogic.rdkb.remotemanagement.data.datasource.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.RemoteRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.error.IoDeviceError
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralApiService
import com.globallogic.rdkb.remotemanagement.data.network.service.RouterDeviceProperty
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceInfo
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Failure
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.component6
import com.globallogic.rdkb.remotemanagement.domain.utils.component7
import com.globallogic.rdkb.remotemanagement.domain.utils.component8
import com.globallogic.rdkb.remotemanagement.domain.utils.flatMapData
import com.globallogic.rdkb.remotemanagement.domain.utils.mapError

class RemoteRouterDeviceDataSourceImpl(
    private val centralApiService: RdkCentralApiService
) : RemoteRouterDeviceDataSource {
    override suspend fun loadRouterDeviceInfo(device: RouterDevice): Resource<RouterDeviceInfo, IoDeviceError.NoDeviceInfoFound> {
        return centralApiService.getDeviceProperties(
            device.macAddress.replace(":", ""),
            RouterDeviceProperty.ModelName,
            RouterDeviceProperty.IpAddressV4,
            RouterDeviceProperty.MacAddress,
            RouterDeviceProperty.SoftwareVersion,
            RouterDeviceProperty.SerialNumber,
            RouterDeviceProperty.OperatingFrequencyBand(10_000),
            RouterDeviceProperty.OperatingFrequencyBand(10_100),
//            RouterDeviceProperty.OperatingFrequencyBand(10_200),
        )
            .flatMapData { response ->
                println(response)
                if (response.parameters?.size != 7) {
                    return@flatMapData Failure<IoDeviceError.NoDeviceInfoFound>(IoDeviceError.NoDeviceInfoFound)
                }
                val (modelName, ip, mac, firmwareVersion, serialNumber, band1, band2) = response.parameters
                val info = RouterDeviceInfo(
                    lanConnected = true,
                    connectedExtender = 0,
                    modelName = modelName?.value.orEmpty(),
                    ipAddress = ip?.value.orEmpty(),
                    macAddress = mac?.value.orEmpty(),
                    firmwareVersion = firmwareVersion?.value.orEmpty(),
                    serialNumber = serialNumber?.value.orEmpty(),
                    processorLoadPercent = 0,
                    memoryUsagePercent = 0,
                    totalDownloadTraffic = 0,
                    totalUploadTraffic = 0,
                    availableBands = setOf(band1?.value.orEmpty(), band2?.value.orEmpty()),
                )
                Success(info)
            }
            .mapError { error -> IoDeviceError.NoDeviceInfoFound }
    }

    override suspend fun loadConnectedDevicesForRouterDevice(device: RouterDevice): Resource<List<ConnectedDevice>, IoDeviceError.LoadConnectedDevicesForRouterDevice> {
        return Success(emptyList())
    }

    override suspend fun setupDevice(device: RouterDevice, settings: RouterDeviceSettings): Resource<Unit, IoDeviceError.SetupDevice> {
        return Success(Unit)
    }

    override suspend fun factoryResetDevice(device: RouterDevice): Resource<Unit, IoDeviceError.FactoryResetDevice> {
        return Success(Unit)
    }

    override suspend fun restartDevice(device: RouterDevice): Resource<Unit, IoDeviceError.RestartDevice> {
        return Success(Unit)
    }
}

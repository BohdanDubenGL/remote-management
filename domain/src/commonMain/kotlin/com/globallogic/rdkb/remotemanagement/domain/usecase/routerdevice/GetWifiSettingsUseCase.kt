package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.WifiSettings
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

class GetWifiSettingsUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(device: RouterDevice): Resource<WifiSettings, DeviceError.WifiSettings> =
        getWifiSettings(device)

    suspend fun getWifiSettings(device: RouterDevice): Resource<WifiSettings, DeviceError.WifiSettings> {
        return routerDeviceRepository.getRouterDeviceWifiSettings(device)
    }
}

package com.globallogic.rdkb.remotemanagement.domain.usecase.wifimotion

import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointClient
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

class StartWifiMotionUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(accessPointClient: AccessPointClient): Resource<Unit, DeviceError.WifiMotion> =
        startWifiMotion(accessPointClient)

    suspend fun startWifiMotion(accessPointClient: AccessPointClient): Resource<Unit, DeviceError.WifiMotion> {
        return routerDeviceRepository.startWifiMotion(accessPointClient)
    }
}

package com.globallogic.rdkb.remotemanagement.domain.usecase.wifimotion

import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

class StopWifiMotionUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(): Resource<Unit, DeviceError.WifiMotion> =
        stopWifiMotion()

    suspend fun stopWifiMotion(): Resource<Unit, DeviceError.WifiMotion> {
        return routerDeviceRepository.stopWifiMotion()
    }
}

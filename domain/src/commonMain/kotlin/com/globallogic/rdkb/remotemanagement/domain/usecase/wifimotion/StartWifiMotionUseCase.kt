package com.globallogic.rdkb.remotemanagement.domain.usecase.wifimotion

import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

class StartWifiMotionUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(connectedDevice: ConnectedDevice): Resource<Unit, DeviceError.WifiMotion> =
        startWifiMotion(connectedDevice)

    suspend fun startWifiMotion(connectedDevice: ConnectedDevice): Resource<Unit, DeviceError.WifiMotion> {
        return routerDeviceRepository.startWifiMotion(connectedDevice)
    }
}

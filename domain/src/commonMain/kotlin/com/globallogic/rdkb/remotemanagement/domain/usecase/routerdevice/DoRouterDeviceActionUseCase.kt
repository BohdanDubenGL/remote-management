package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

class DoRouterDeviceActionUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(device: RouterDevice, action: RouterDeviceAction): Resource<Unit, DeviceError.NoDeviceFound> =
        restartRouterDevice(device, action)

    suspend fun restartRouterDevice(device: RouterDevice, action: RouterDeviceAction): Resource<Unit, DeviceError.NoDeviceFound> =
        routerDeviceRepository.doAction(device, action)
}

enum class RouterDeviceAction {
    Restart, FactoryReset, Remove;
}

package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

class DoRouterDeviceActionUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(action: RouterDeviceAction): Resource<Unit, DeviceError.NoDeviceFound> =
        restartRouterDevice(action)

    suspend fun restartRouterDevice(action: RouterDeviceAction): Resource<Unit, DeviceError.NoDeviceFound> =
        routerDeviceRepository.doAction(action)
}

enum class RouterDeviceAction {
    Restart, FactoryReset, Remove;
}

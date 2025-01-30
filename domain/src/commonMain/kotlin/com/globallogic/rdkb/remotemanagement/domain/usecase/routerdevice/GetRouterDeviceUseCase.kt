package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository

class GetRouterDeviceUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(deviceMacAddress: String): RouterDevice = getRouterDevice(deviceMacAddress)

    suspend fun getRouterDevice(deviceMacAddress: String): RouterDevice = routerDeviceRepository.getRouterDevice(deviceMacAddress)
}

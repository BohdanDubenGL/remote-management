package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceConnectionRepository

class AddRouterDeviceManuallyUseCase(
    private val routerDeviceConnectionRepository: RouterDeviceConnectionRepository
) {
    suspend operator fun invoke(macAddress: String): RouterDevice = addRouterDeviceManually(macAddress)

    suspend fun addRouterDeviceManually(macAddress: String): RouterDevice = routerDeviceConnectionRepository.addRouterDeviceManually(macAddress)
}

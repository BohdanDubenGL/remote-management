package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository

class GetRouterDeviceListUseCase(
    private val routerDeviceRepository: RouterDeviceRepository,
) {
    suspend operator fun invoke(): List<RouterDevice> = getDeviceList()

    suspend fun getDeviceList(): List<RouterDevice> = routerDeviceRepository.getDeviceList()
}

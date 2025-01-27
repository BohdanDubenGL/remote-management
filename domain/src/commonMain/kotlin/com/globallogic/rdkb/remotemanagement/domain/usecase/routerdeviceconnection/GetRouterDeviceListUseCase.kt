package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceConnectionRepository

class GetRouterDeviceListUseCase(
    private val routerDeviceConnectionRepository: RouterDeviceConnectionRepository
) {
    suspend operator fun invoke(): List<RouterDevice> = getDeviceList()

    suspend fun getDeviceList(): List<RouterDevice> = routerDeviceConnectionRepository.getDeviceList()
}

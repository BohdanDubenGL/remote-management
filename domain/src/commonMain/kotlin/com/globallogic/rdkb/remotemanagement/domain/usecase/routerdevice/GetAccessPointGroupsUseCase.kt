package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointGroup
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource

class GetAccessPointGroupsUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(device: RouterDevice): Resource<List<AccessPointGroup>, DeviceError.WifiSettings> =
        getAccessPointGroups(device)

    suspend fun getAccessPointGroups(device: RouterDevice): Resource<List<AccessPointGroup>, DeviceError.WifiSettings> {
        return routerDeviceRepository.loadAccessPointGroups(device)
    }
}

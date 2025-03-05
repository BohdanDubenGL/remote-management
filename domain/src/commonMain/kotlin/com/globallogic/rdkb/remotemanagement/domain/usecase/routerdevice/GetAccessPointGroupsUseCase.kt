package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointGroup
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceState
import kotlinx.coroutines.flow.Flow

class GetAccessPointGroupsUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(device: RouterDevice, forceUpdate: Boolean = true): Flow<ResourceState<List<AccessPointGroup>, DeviceError.WifiSettings>> =
        getAccessPointGroups(device, forceUpdate)

    suspend fun getAccessPointGroups(device: RouterDevice, forceUpdate: Boolean): Flow<ResourceState<List<AccessPointGroup>, DeviceError.WifiSettings>> {
        return routerDeviceRepository.loadAccessPointGroups(device, forceUpdate)
    }
}

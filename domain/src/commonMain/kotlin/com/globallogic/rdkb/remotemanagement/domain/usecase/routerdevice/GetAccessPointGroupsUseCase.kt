package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointGroup
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceState
import kotlinx.coroutines.flow.Flow

class GetAccessPointGroupsUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(forceUpdate: Boolean = true): Flow<ResourceState<List<AccessPointGroup>, DeviceError.WifiSettings>> =
        getAccessPointGroups(forceUpdate)

    suspend fun getAccessPointGroups(forceUpdate: Boolean): Flow<ResourceState<List<AccessPointGroup>, DeviceError.WifiSettings>> {
        return routerDeviceRepository.loadAccessPointGroups(forceUpdate)
    }
}

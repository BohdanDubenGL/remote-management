package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceState
import kotlinx.coroutines.flow.Flow

class GetRouterDeviceListUseCase(
    private val routerDeviceRepository: RouterDeviceRepository,
) {
    suspend operator fun invoke(forceUpdate: Boolean = true): Flow<ResourceState<List<RouterDevice>, DeviceError.NoDevicesFound>> =
        getDeviceList(forceUpdate)

    suspend fun getDeviceList(forceUpdate: Boolean): Flow<ResourceState<List<RouterDevice>, DeviceError.NoDevicesFound>> =
        routerDeviceRepository.getDeviceList(forceUpdate)
}

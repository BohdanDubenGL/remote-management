package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.TopologyData
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceState
import kotlinx.coroutines.flow.Flow

class GetTopologyDataUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(): Flow<ResourceState<TopologyData, DeviceError.Topology>> =
        getTopologyData()

    suspend fun getTopologyData(): Flow<ResourceState<TopologyData, DeviceError.Topology>> =
        routerDeviceRepository.getTopologyData()
}

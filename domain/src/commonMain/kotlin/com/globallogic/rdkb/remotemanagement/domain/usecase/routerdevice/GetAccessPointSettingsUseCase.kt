package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointGroup
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointSettings
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceState
import kotlinx.coroutines.flow.Flow

class GetAccessPointSettingsUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(accessPointGroup: AccessPointGroup, forceUpdate: Boolean = true): Flow<ResourceState<AccessPointSettings, DeviceError.WifiSettings>> =
        getAccessPointSettings(accessPointGroup, forceUpdate)

    suspend fun getAccessPointSettings(accessPointGroup: AccessPointGroup, forceUpdate: Boolean): Flow<ResourceState<AccessPointSettings, DeviceError.WifiSettings>> {
        return routerDeviceRepository.getDeviceAccessPointSettings(accessPointGroup, forceUpdate)
    }
}

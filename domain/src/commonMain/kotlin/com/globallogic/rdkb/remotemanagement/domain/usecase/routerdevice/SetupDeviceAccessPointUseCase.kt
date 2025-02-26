package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointGroup
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.DeviceAccessPointSettings
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success

class SetupDeviceAccessPointUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(
        device: RouterDevice,
        accessPointGroup: AccessPointGroup,
        settings: DeviceAccessPointSettings
    ): Resource<Unit, DeviceError.SetupDevice> = setupDeviceAccessPoint(device, accessPointGroup, settings)

    suspend fun setupDeviceAccessPoint(
        device: RouterDevice,
        accessPointGroup: AccessPointGroup,
        settings: DeviceAccessPointSettings
    ): Resource<Unit, DeviceError.SetupDevice> {
        if (settings.bands.isEmpty()) return Success(Unit)

        val firstBand = settings.bands.first()
        val settingsToSave = settings.copy(
            bands = settings.bands
                .map { band ->
                    if (band.sameAsFirst) band.copy(
                        ssid = firstBand.ssid,
                        password = firstBand.password,
                        securityMode = firstBand.securityMode,
                        sameAsFirst = false
                    )
                    else band
                }
                .filter { band -> band.ssid.isNotBlank() || band.password.isNotBlank() }
        )

        return routerDeviceRepository.setupDeviceAccessPoint(device, accessPointGroup, settingsToSave)
    }
}

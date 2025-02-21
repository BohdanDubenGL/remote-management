package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdevice

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success

class SetupRouterDeviceUseCase(
    private val routerDeviceRepository: RouterDeviceRepository
) {
    suspend operator fun invoke(device: RouterDevice, settings: RouterDeviceSettings): Resource<Unit, DeviceError.SetupDevice> =
        setupRouterDevice(device, settings)

    suspend fun setupRouterDevice(device: RouterDevice, settings: RouterDeviceSettings): Resource<Unit, DeviceError.SetupDevice> {
        if (settings.bands.isEmpty()) return Success(Unit)

        val firstBand = settings.bands.first()
        val settingsToSave = settings.copy(
            bands = settings.bands
                .map { band ->
                    if (band.sameAsFirst) band.copy(ssid = firstBand.ssid, password = firstBand.password, sameAsFirst = false)
                    else band
                }
                .filter { band -> band.ssid.isNotBlank() || band.password.isNotBlank() }
        )

        return routerDeviceRepository.setupRouterDevice(device, settingsToSave)
    }
}

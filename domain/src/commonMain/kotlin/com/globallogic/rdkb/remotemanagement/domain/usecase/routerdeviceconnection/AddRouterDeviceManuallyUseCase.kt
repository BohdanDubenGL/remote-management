package com.globallogic.rdkb.remotemanagement.domain.usecase.routerdeviceconnection

import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.error.DeviceError
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceConnectionRepository
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Failure
import com.globallogic.rdkb.remotemanagement.domain.verification.MacAddressVerifier

class AddRouterDeviceManuallyUseCase(
    private val routerDeviceConnectionRepository: RouterDeviceConnectionRepository,
    private val macAddressVerifier: MacAddressVerifier,
) {
    suspend operator fun invoke(macAddress: String): Resource<RouterDevice, DeviceError.ConnectionError> =
        addRouterDeviceManually(macAddress)

    suspend fun addRouterDeviceManually(macAddress: String): Resource<RouterDevice, DeviceError.ConnectionError> {
        val macAddressErrors = macAddressVerifier.verifyMacAddress(macAddress)
        if (macAddressErrors.isNotEmpty()) {
            val emailError = when (macAddressErrors.first()) {
                MacAddressVerifier.Error.EmptyMacAddress -> DeviceError.WrongMacAddressFormat
                MacAddressVerifier.Error.InvalidFormat -> DeviceError.WrongMacAddressFormat
            }
            return Failure(emailError)
        }
        return routerDeviceConnectionRepository.addRouterDeviceManually(macAddress)
    }
}

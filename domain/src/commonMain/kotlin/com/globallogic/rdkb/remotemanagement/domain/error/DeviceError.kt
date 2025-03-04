package com.globallogic.rdkb.remotemanagement.domain.error

import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceError

sealed interface DeviceError : ResourceError {
    data object NoAvailableRouterDevices : DeviceError
    sealed interface ConnectionError: DeviceError
    data object CantConnectToRouterDevice : DeviceError, ConnectionError
    data object WrongMacAddressFormat : DeviceError, ConnectionError

    data object SetupDevice : DeviceError

    data object NoDeviceFound : DeviceError
    data object NoDevicesFound : DeviceError
    data object NoConnectedDevicesFound : DeviceError
    data object WifiSettings : DeviceError
}

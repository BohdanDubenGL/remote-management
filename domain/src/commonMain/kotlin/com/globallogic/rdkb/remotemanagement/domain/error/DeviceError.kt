package com.globallogic.rdkb.remotemanagement.domain.error

import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceError

sealed interface DeviceError : ResourceError {
    data object NoAvailableRouterDevices : DeviceError
    data object CantConnectToRouterDevice : DeviceError

    data object SetupDevice : DeviceError

    data object NoDeviceFound : DeviceError
    data object NoDevicesFound : DeviceError
    data object NoConnectedDevicesFound : DeviceError
    data object WifiSettings : DeviceError
}

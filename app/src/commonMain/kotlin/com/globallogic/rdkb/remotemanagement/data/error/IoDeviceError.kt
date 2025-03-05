package com.globallogic.rdkb.remotemanagement.data.error

import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceError

sealed interface IoDeviceError : ResourceError {
    data object NoAvailableRouterDevices : IoDeviceError
    data object CantConnectToRouterDevice : IoDeviceError

    data object LoadConnectedDevicesForRouterDevice : IoDeviceError
    data object FactoryResetDevice : IoDeviceError
    data object RestartDevice : IoDeviceError
    data object SetupDevice : IoDeviceError

    interface LoadRouterDevicesForUser : IoDeviceError
    interface FindRouterDeviceByMacAddress : IoDeviceError
    interface SaveConnectedDevices : IoDeviceError
    interface LoadConnectedDevices : IoDeviceError
    interface LoadDeviceInfo : IoDeviceError
    interface SaveRouterDevice : IoDeviceError
    interface RemoveRouterDevice : IoDeviceError
    interface FindLocalRouterDevice : IoDeviceError
    interface AccessPointGroups : IoDeviceError
    interface AccessPoints : IoDeviceError

    data object NoDeviceFound : FindRouterDeviceByMacAddress, FindLocalRouterDevice, LoadDeviceInfo
    data object WifiSettings : FindRouterDeviceByMacAddress, FindLocalRouterDevice, LoadDeviceInfo

    data class DatabaseError(val throwable: Throwable) : LoadRouterDevicesForUser, FindRouterDeviceByMacAddress,
        SaveConnectedDevices, LoadConnectedDevices, LoadDeviceInfo,
        SaveRouterDevice, RemoveRouterDevice, FindLocalRouterDevice,
        AccessPointGroups, AccessPoints
}

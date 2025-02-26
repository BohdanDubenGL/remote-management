package com.globallogic.rdkb.remotemanagement.data.network.service.exception

import com.globallogic.rdkb.remotemanagement.data.network.service.model.DeviceParameter
import com.globallogic.rdkb.remotemanagement.data.network.service.model.DeviceProperty


class CantLoadDevicesException() : Exception("Can't load devices from server")

class CantLoadDevicePropertyException(
    val macAddress: String,
    val property: DeviceProperty.Property<*>,
) : Exception("Can't load '${property.name}' for device with mac address '$macAddress'")

class CantChangeDevicePropertyException(
    val macAddress: String,
    vararg val parameters: DeviceParameter<*>
) : Exception("Can't change '${parameters.joinToString { it.property.name }}' for device with mac address '$macAddress''")

class CantExecuteDeviceActionException(
    val macAddress: String,
    val action: DeviceProperty.Action
) : Exception("Can't execute action '${action.name}' for device with mac address '$macAddress''")

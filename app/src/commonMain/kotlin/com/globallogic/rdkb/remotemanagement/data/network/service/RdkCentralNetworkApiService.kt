package com.globallogic.rdkb.remotemanagement.data.network.service

import com.globallogic.rdkb.remotemanagement.data.network.service.model.GetDevicesResponse
import com.globallogic.rdkb.remotemanagement.data.network.service.model.GetNamespaceResponse
import com.globallogic.rdkb.remotemanagement.data.network.service.model.GetPropertyResponse
import com.globallogic.rdkb.remotemanagement.data.network.service.model.SetPropertyResponse
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError

interface RdkCentralApiService {
    suspend fun getAvailableDevices(): Resource <GetDevicesResponse, ThrowableResourceError>

    suspend fun getDeviceProperties(
        deviceMacAddress: String,
        vararg properties: RouterDeviceProperty.Property,
    ): Resource<GetPropertyResponse, ThrowableResourceError>

    suspend fun getDeviceNamespace(
        deviceMacAddress: String,
        vararg properties: RouterDeviceProperty.Namespace,
    ): Resource<GetNamespaceResponse, ThrowableResourceError>

    suspend fun setDeviceProperty(
        deviceMacAddress: String,
        deviceProperty: RouterDeviceProperty.Property,
        value: String,
    ): Resource<SetPropertyResponse, ThrowableResourceError>

    suspend fun doDeviceAction(
        deviceMacAddress: String,
        deviceAction: RouterDeviceProperty.Action,
    ): Resource<Unit, ThrowableResourceError>
}

sealed interface RouterDeviceProperty {
    val name: String

    abstract class Namespace(override val name: String) : RouterDeviceProperty
    abstract class Property(override val name: String) : RouterDeviceProperty
    abstract class Action(override val name: String, val value: String): RouterDeviceProperty

    data object ModelName : Property(name = "Device.DeviceInfo.ModelName")
    data object SoftwareVersion : Property(name = "Device.DeviceInfo.SoftwareVersion")
    data object AdditionalSoftwareVersion : Property(name = "Device.DeviceInfo.AdditionalSoftwareVersion")
    data object IpAddressV4 : Property(name = "Device.DeviceInfo.X_COMCAST-COM_WAN_IP")
    data object IpAddressV6 : Property(name = "Device.DeviceInfo.X_COMCAST-COM_WAN_IPv6")
    data object MacAddress : Property(name = "Device.DeviceInfo.X_COMCAST-COM_WAN_MAC")
    data object SerialNumber : Property(name = "Device.DeviceInfo.SerialNumber")
    data class OperatingFrequencyBand(val radio: Int) : Property(name = "Device.WiFi.Radio.$radio.OperatingFrequencyBand") // 10000, 10100, 10200
    data object KeyPassphrase : Property(name = "Device.WiFi.AccessPoint.{i}.Security.KeyPassphrase")
    data object PreSharedKey : Property(name = "Device.WiFi.AccessPoint.{i}.Security.PreSharedKey")
    data object SecurityModeEnabled : Property(name = "Device.WiFi.AccessPoint.{i}.Security.ModeEnabled")

    data class WifiEnabled(val index: Int) : Property(name = "Device.WiFi.SSID.$index.Enable")
    data class WifiSsid(val index: Int) : Property(name = "Device.WiFi.SSID.$index.SSID")

    data object ConnectedDeviceCount : Property(name = "Device.Hosts.HostNumberOfEntries")
    data class ConnectedDeviceActive(val index: Int) : Property(name = "Device.Hosts.Host.$index.Active")
    data class ConnectedDeviceHostName(val index: Int) : Property(name = "Device.Hosts.Host.$index.HostName")
    data class ConnectedDeviceMacAddress(val index: Int) : Property(name = "Device.Hosts.Host.$index.PhysAddress")

    data class ChangeBandSsid(val band: Int) : Property(name = "Device.WiFi.SSID.$band.SSID")
    data class ChangeBandPassword(val band: Int) : Property(name = "Device.WiFi.AccessPoint.$band.Security.KeyPassphrase")

    data object ActionReboot : Action(name = "Device.X_CISCO_COM_DeviceControl.RebootDevice", "Device")
    data object ActionFactoryReset : Action(name = "Device.X_CISCO_COM_DeviceControl.FactoryReset", "Router,Wifi,Firewall,VoIP,Docsis")
}

package com.globallogic.rdkb.remotemanagement.data.network.service

import com.globallogic.rdkb.remotemanagement.data.network.service.model.GetDevicesResponse
import com.globallogic.rdkb.remotemanagement.data.network.service.model.GetNamespaceResponse
import com.globallogic.rdkb.remotemanagement.data.network.service.model.GetPropertyResponse
import com.globallogic.rdkb.remotemanagement.data.network.service.model.SetPropertyResponse
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError

// test mac = dca6320eb8bb
interface RdkCentralApiService {
    suspend fun getAvailableDevices(): Resource <GetDevicesResponse, ThrowableResourceError>

    suspend fun <T> getDeviceProperties(
        deviceMacAddress: String,
        vararg properties: RouterDeviceProperty.Get<T>,
    ): Resource<GetPropertyResponse, ThrowableResourceError>

    suspend fun <T> getDeviceNamespace(
        deviceMacAddress: String,
        vararg properties: RouterDeviceProperty.Get<T>,
    ): Resource<GetNamespaceResponse, ThrowableResourceError>

    suspend fun <T> setDeviceProperty(
        deviceMacAddress: String,
        deviceProperty: RouterDeviceProperty.Set<T>,
        value: T,
    ): Resource<SetPropertyResponse, ThrowableResourceError>

    suspend fun doDeviceAction(
        deviceMacAddress: String,
        deviceAction: RouterDeviceProperty.Action,
    ): Resource<Boolean, ThrowableResourceError>
}

sealed class RouterDeviceProperty(val name: String) {
    interface Set<T> { val name: String }
    interface Get<T> { val name: String }
    interface Property<T> : Set<T>, Get<T>
    interface Action

    data object ModelName : RouterDeviceProperty(name = "Device.DeviceInfo.ModelName"), Get<String>
    data object SoftwareVersion : RouterDeviceProperty(name = "Device.DeviceInfo.SoftwareVersion"), Get<String>
    data object AdditionalSoftwareVersion : RouterDeviceProperty(name = "Device.DeviceInfo.AdditionalSoftwareVersion"), Get<String>
    data object IpAddressV4 : RouterDeviceProperty(name = "Device.DeviceInfo.X_COMCAST-COM_WAN_IP"), Get<String>
    data object IpAddressV6 : RouterDeviceProperty(name = "Device.DeviceInfo.X_COMCAST-COM_WAN_IPv6"), Get<String>
    data object MacAddress : RouterDeviceProperty(name = "Device.DeviceInfo.X_COMCAST-COM_WAN_MAC"), Get<String>
    data object SerialNumber : RouterDeviceProperty(name = "Device.DeviceInfo.SerialNumber"), Get<String>
    data class OperatingFrequencyBand(val radio: Int) : RouterDeviceProperty(name = "Device.WiFi.Radio.$radio.OperatingFrequencyBand"), Get<String> // 10000, 10100, 10200
    data object Ssid : RouterDeviceProperty(name = "Device.WiFi.SSID.{i}.SSID"), Get<String>
    data object KeyPassphrase : RouterDeviceProperty(name = "Device.WiFi.AccessPoint.{i}.Security.KeyPassphrase"), Get<String>
    data object PreSharedKey : RouterDeviceProperty(name = "Device.WiFi.AccessPoint.{i}.Security.PreSharedKey"), Get<String>
    data object SecurityModeEnabled : RouterDeviceProperty(name = "Device.WiFi.AccessPoint.{i}.Security.ModeEnabled"), Get<String>
    data object ActionRestart : RouterDeviceProperty(name = "Device.X_CISCO_COM_DeviceControl.RebootDevice \"true\""), Action
    data object ActionFactoryReset : RouterDeviceProperty(name = "Device.X_CISCO_COM_DeviceControl.FactoryReset \"Router,Wifi,Firewall,VoIP,Docsis\""), Action
    data object ActiveInstancesCount : RouterDeviceProperty(name = "Device.Hosts.Host.{i}.Active"), Get<String>
    data object ActiveInstance : RouterDeviceProperty(name = "Device.Hosts.Host.{i}."), Get<String>
}

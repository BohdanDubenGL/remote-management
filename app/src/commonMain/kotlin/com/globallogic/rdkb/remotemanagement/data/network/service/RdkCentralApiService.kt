package com.globallogic.rdkb.remotemanagement.data.network.service

import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.typeInfo

interface RdkCentralApiService {
    suspend fun <T> getDeviceProperty(
        deviceMacAddress: String = "dca6320eb8bb", // todo: remove real mac
        deviceProperty: RouterDevice.Get<T>,
        typeInfo: TypeInfo,
    ): T?

    suspend fun <T> setDeviceProperty(
        deviceMacAddress: String = "dca6320eb8bb", // todo: remove real mac
        deviceProperty: RouterDevice.Set<T>,
        typeInfo: TypeInfo,
        value: T,
    ): Boolean

    suspend fun doDeviceAction(
        deviceMacAddress: String = "dca6320eb8bb", // todo: remove real mac
        deviceAction: RouterDevice.Action,
    ): Boolean
}

suspend inline fun <reified T> RdkCentralApiService.getDeviceProperty(
    deviceMacAddress: String = "dca6320eb8bb", // todo: remove real mac
    deviceProperty: RouterDevice.Get<T>,
): T? = getDeviceProperty(deviceMacAddress, deviceProperty, typeInfo<T>())

suspend inline fun <reified T> RdkCentralApiService.setDeviceProperty(
    deviceMacAddress: String = "dca6320eb8bb", // todo: remove real mac
    deviceProperty: RouterDevice.Set<T>,
    value: T,
): Boolean = setDeviceProperty(deviceMacAddress, deviceProperty, typeInfo<T>(), value)

sealed class RouterDevice(val name: String) {
    interface Set<T> { val name: String }
    interface Get<T> { val name: String }
    interface Property<T> : Set<T>, Get<T>
    interface Action

    data object ModelName : RouterDevice(name = "Device.DeviceInfo.ModelName"), Get<String>
    data object SoftwareVersion : RouterDevice(name = "Device.DeviceInfo.SoftwareVersion"), Get<String>
    data object AdditionalSoftwareVersion : RouterDevice(name = "Device.DeviceInfo.AdditionalSoftwareVersion"), Get<String>
    data object IpAddressV4 : RouterDevice(name = "Device.DeviceInfo.X_COMCAST-COM_WAN_IP"), Get<String>
    data object IpAddressV6 : RouterDevice(name = "Device.DeviceInfo.X_COMCAST-COM_WAN_IPv6"), Get<String>
    data object MacAddress : RouterDevice(name = "Device.DeviceInfo.X_COMCAST-COM_WAN_MAC"), Get<String>
    data object SerialNumber : RouterDevice(name = "Device.DeviceInfo.SerialNumber"), Get<String>
    data object OperatingFrequencyBand : RouterDevice(name = "Device.WiFi.Radio.{1}.OperatingFrequencyBand"), Get<String>
    data object Ssid : RouterDevice(name = "Device.WiFi.SSID.{i}.SSID"), Get<String>
    data object KeyPassphrase : RouterDevice(name = "Device.WiFi.AccessPoint.{i}.Security.KeyPassphrase"), Get<String>
    data object PreSharedKey : RouterDevice(name = "Device.WiFi.AccessPoint.{i}.Security.PreSharedKey"), Get<String>
    data object SecurityModeEnabled : RouterDevice(name = "Device.WiFi.AccessPoint.{i}.Security.ModeEnabled"), Get<String>
    data object ActionRestart : RouterDevice(name = "Device.X_CISCO_COM_DeviceControl.RebootDevice \"true\""), Action
    data object ActionFactoryReset : RouterDevice(name = "Device.X_CISCO_COM_DeviceControl.FactoryReset \"Router,Wifi,Firewall,VoIP,Docsis\""), Action
    data object ActiveInstancesCount : RouterDevice(name = "Device.Hosts.Host.{i}.Active"), Get<String>
    data object ActiveInstance : RouterDevice(name = "Device.Hosts.Host.{i}."), Get<String>
}

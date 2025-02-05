package com.globallogic.rdkb.remotemanagement.data.repository.fake

import com.globallogic.rdkb.remotemanagement.data.datasource.fake.FakeRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.preferences.AppPreferences
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceInfo
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceTopologyData
import com.globallogic.rdkb.remotemanagement.domain.repository.RouterDeviceRepository
import kotlinx.coroutines.flow.firstOrNull

class FakeRouterDeviceRepository(
    private val appPreferences: AppPreferences,
    private val routedDeviceDataSource: FakeRouterDeviceDataSource,
) : RouterDeviceRepository {
    override suspend fun getDeviceList(): List<RouterDevice> {
        return routedDeviceDataSource.findSavedRouterDevices()
    }

    override suspend fun factoryResetRouterDevice(device: RouterDevice) {
        routedDeviceDataSource.actionFactoryResetDevice(device.macAddress)
    }

    override suspend fun getRouterDeviceConnectedDevices(device: RouterDevice): List<ConnectedDevice> {
        return routedDeviceDataSource.findConnectedDevicesForRouterByMacAddress(device.macAddress)
    }

    override suspend fun getRouterDeviceInfo(device: RouterDevice): RouterDeviceInfo {
        return routedDeviceDataSource.findDeviceInfoByMacAddress(device.macAddress) ?: RouterDeviceInfo.empty
    }

    override suspend fun getRouterDeviceTopologyData(device: RouterDevice): RouterDeviceTopologyData {
        return routedDeviceDataSource.findTopologyDataByMacAddress(device.macAddress) ?: RouterDeviceTopologyData.empty
    }

    override suspend fun restartRouterDevice(device: RouterDevice) {
        routedDeviceDataSource.actionRestartDevice(device.macAddress)
    }

    override suspend fun removeRouterDevice(device: RouterDevice) {
        routedDeviceDataSource.removeRouterDevice(device.macAddress)
        val currentRouterDeviceMacAddress = appPreferences.currentRouterDeviceMacAddressPref.get() ?: return
        if (device.macAddress == currentRouterDeviceMacAddress) {
            appPreferences.currentRouterDeviceMacAddressPref.reset()
        }
    }

    override suspend fun setupRouterDevice(device: RouterDevice, settings: RouterDeviceSettings) {
        routedDeviceDataSource.actionSetupRouterDevice(device.macAddress, settings)
    }

    override suspend fun selectRouterDevice(device: RouterDevice) {
        appPreferences.currentRouterDeviceMacAddressPref.set(device.macAddress)
    }

    override suspend fun getSelectRouterDevice(): RouterDevice {
        val currentDeviceMacAddress = appPreferences.currentRouterDeviceMacAddressPref.get() ?: return RouterDevice.empty
        return routedDeviceDataSource.findSavedRouterDeviceByMacAddress(currentDeviceMacAddress) ?: RouterDevice.empty
    }

    override suspend fun getLocalRouterDevice(): RouterDevice {
        return routedDeviceDataSource.findLocalRouterDevice() ?: RouterDevice.empty
    }
}

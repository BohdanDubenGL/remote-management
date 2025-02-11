package com.globallogic.rdkb.remotemanagement.data.datasource.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.LocalRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.db.RouterDeviceDao
import com.globallogic.rdkb.remotemanagement.data.db.dto.ConnectedDeviceDto
import com.globallogic.rdkb.remotemanagement.data.db.dto.RouterDeviceDto
import com.globallogic.rdkb.remotemanagement.data.db.dto.UserRouterDeviceDto
import com.globallogic.rdkb.remotemanagement.data.error.IoDeviceError
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceInfo
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDeviceTopologyData
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.buildResource
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.runCatchingSafe
import kotlinx.datetime.Clock

class LocalRouterDeviceDataSourceImpl(
    private val deviceDao: RouterDeviceDao,
) : LocalRouterDeviceDataSource {

    override suspend fun loadRouterDevicesForUser(userEmail: String): Resource<List<RouterDevice>, IoDeviceError.LoadRouterDevicesForUser> = buildResource {
        val devices = runCatchingSafe { deviceDao.findRouterDevicesForUser(userEmail) }
            .getOrElse { error -> return failure(IoDeviceError.DatabaseError(error)) }

        return success(devices.map(RouterDeviceMapper::toRouterDevice))
    }

    override suspend fun findRouterDeviceByMacAddress(macAddress: String): Resource<RouterDevice, IoDeviceError.FindRouterDeviceByMacAddress> = buildResource {
        val device = runCatchingSafe { deviceDao.findRouterDeviceByMacAddress(macAddress) }
            .getOrElse { error -> return failure(IoDeviceError.DatabaseError(error)) }
            ?: return failure(IoDeviceError.NoDeviceFound)

        return success(RouterDeviceMapper.toRouterDevice(device))
    }

    override suspend fun saveConnectedDevices(device: RouterDevice, connectedDevices: List<ConnectedDevice>): Resource<Unit, IoDeviceError.SaveConnectedDevices> = buildResource {
        val connectedDevicesDto = connectedDevices.map { ConnectedDeviceMapper.toConnectedDevice(device, it) }
        runCatchingSafe { deviceDao.insertConnectedDevices(*connectedDevicesDto.toTypedArray()) }
            .getOrElse { error -> return failure(IoDeviceError.DatabaseError(error)) }

        return success(Unit)
    }

    override suspend fun loadConnectedDevices(device: RouterDevice): Resource<List<ConnectedDevice>, IoDeviceError.LoadConnectedDevices> = buildResource {
        val connectedDevices = runCatchingSafe { deviceDao.findConnectedDevices(device.macAddress) }
            .getOrElse { error -> return failure(IoDeviceError.DatabaseError(error)) }

        return success(connectedDevices.map(ConnectedDeviceMapper::toConnectedDevice))
    }

    override suspend fun loadDeviceInfo(device: RouterDevice): Resource<RouterDeviceInfo, IoDeviceError.LoadDeviceInfo> = buildResource {
        val routerDevice = runCatchingSafe { deviceDao.findRouterDeviceByMacAddress(device.macAddress) }
            .getOrElse { error -> return failure(IoDeviceError.DatabaseError(error)) }
            ?: return failure(IoDeviceError.NoDeviceFound)

        return success(RouterDeviceMapper.toRouterDeviceInfo(routerDevice))
    }

    override suspend fun loadTopologyData(device: RouterDevice): Resource<RouterDeviceTopologyData, IoDeviceError.NoTopologyDataFound> = buildResource {
        val routerDevice = findRouterDeviceByMacAddress(device.macAddress)
            .dataOrElse { error -> return failure(IoDeviceError.NoTopologyDataFound) }
        val deviceInfo = loadDeviceInfo(device)
            .dataOrElse { error -> return failure(IoDeviceError.NoTopologyDataFound) }
        val connectedDevices = loadConnectedDevices(device)
            .dataOrElse { error -> return failure(IoDeviceError.NoTopologyDataFound) }

        return success(RouterDeviceTopologyData(
            lanConnected = deviceInfo.lanConnected,
            routerDevice = routerDevice,
            connectedDevices = connectedDevices
        ))
    }

    override suspend fun saveRouterDevice(device: RouterDeviceInfo, userEmail: String): Resource<Unit, IoDeviceError.SaveRouterDevice> = buildResource {
        val deviceDto = RouterDeviceMapper.toRouterDeviceInfo(device)

        runCatchingSafe { deviceDao.insertRouterDevice(deviceDto) }
            .getOrElse { error -> return failure(IoDeviceError.DatabaseError(error)) }
        runCatchingSafe { deviceDao.insertUserRouterDevice(UserRouterDeviceDto(userEmail, device.macAddress)) }
            .getOrElse { error -> return failure(IoDeviceError.DatabaseError(error)) }

        return success(Unit)
    }

    override suspend fun removeRouterDevice(device: RouterDevice, userEmail: String): Resource<Unit, IoDeviceError.RemoveRouterDevice> = buildResource {
        runCatchingSafe { deviceDao.deleteUserRouterDevice(UserRouterDeviceDto(userEmail, device.macAddress)) }
            .getOrElse { error -> return failure(IoDeviceError.DatabaseError(error)) }
        runCatchingSafe { deviceDao.deleteRouterDeviceByMacAddress(device.macAddress) }
            .getOrElse { error -> return failure(IoDeviceError.DatabaseError(error)) }

        return success(Unit)
    }

    override suspend fun findLocalRouterDevice(userEmail: String): Resource<RouterDevice, IoDeviceError.FindLocalRouterDevice> = buildResource {
        val device = runCatchingSafe { deviceDao.findRouterDevicesForUser(userEmail).firstOrNull { it.lanConnected } }
            .getOrElse { error -> return failure(IoDeviceError.DatabaseError(error)) }
            ?: return failure(IoDeviceError.NoDeviceFound)

        return success(RouterDeviceMapper.toRouterDevice(device))
    }
}

private object RouterDeviceMapper {
    fun toRouterDevice(device: RouterDeviceDto): RouterDevice = RouterDevice(
        name = device.modelName,
        ip = device.ipAddress,
        macAddress = device.macAddress
    )
    fun toRouterDeviceInfo(device: RouterDeviceDto): RouterDeviceInfo = RouterDeviceInfo(
        lanConnected = device.lanConnected,
        connectedExtender = device.connectedExtender,
        modelName = device.modelName,
        ipAddress = device.ipAddress,
        macAddress = device.macAddress,
        firmwareVersion = device.firmwareVersion,
        serialNumber = device.serialNumber,
        processorLoadPercent = device.processorLoadPercent,
        memoryUsagePercent = device.memoryUsagePercent,
        totalDownloadTraffic = device.totalDownloadTraffic,
        totalUploadTraffic = device.totalUploadTraffic,
        availableBands = device.availableBands.split(",").toSet()
    )
    fun toRouterDeviceInfo(device: RouterDeviceInfo): RouterDeviceDto = RouterDeviceDto(
        lanConnected = device.lanConnected,
        connectedExtender = device.connectedExtender,
        modelName = device.modelName,
        ipAddress = device.ipAddress,
        macAddress = device.macAddress,
        firmwareVersion = device.firmwareVersion,
        serialNumber = device.serialNumber,
        processorLoadPercent = device.processorLoadPercent,
        memoryUsagePercent = device.memoryUsagePercent,
        totalDownloadTraffic = device.totalDownloadTraffic,
        totalUploadTraffic = device.totalUploadTraffic,
        availableBands = device.availableBands.joinToString(separator = ","),
        updatedAt = Clock.System.now().toEpochMilliseconds()
    )
}

private object ConnectedDeviceMapper {
    fun toConnectedDevice(device: ConnectedDeviceDto): ConnectedDevice = ConnectedDevice(
        macAddress = device.macAddress,
        hostName = device.hostName,
        ssid = device.ssid,
        channel = device.channel,
        rssi = device.rssi,
        bandWidth = device.bandWidth
    )

    fun toConnectedDevice(routerDevice: RouterDevice, device: ConnectedDevice): ConnectedDeviceDto = ConnectedDeviceDto(
        routerDeviceMacAddress = routerDevice.macAddress,
        macAddress = device.macAddress,
        hostName = device.hostName,
        ssid = device.ssid,
        channel = device.channel,
        rssi = device.rssi,
        bandWidth = device.bandWidth
    )
}

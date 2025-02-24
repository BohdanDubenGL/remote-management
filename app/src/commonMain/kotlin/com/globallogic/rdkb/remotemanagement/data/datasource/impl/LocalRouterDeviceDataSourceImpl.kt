package com.globallogic.rdkb.remotemanagement.data.datasource.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.LocalRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.db.RouterDeviceDao
import com.globallogic.rdkb.remotemanagement.data.db.dto.ConnectedDeviceDto
import com.globallogic.rdkb.remotemanagement.data.db.dto.RouterDeviceDto
import com.globallogic.rdkb.remotemanagement.data.db.dto.UserRouterDeviceDto
import com.globallogic.rdkb.remotemanagement.data.error.IoDeviceError
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Failure
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.runCatchingSafe
import kotlinx.datetime.Clock

class LocalRouterDeviceDataSourceImpl(
    private val deviceDao: RouterDeviceDao,
) : LocalRouterDeviceDataSource {

    override suspend fun loadRouterDevicesForUser(userEmail: String): Resource<List<RouterDevice>, IoDeviceError.LoadRouterDevicesForUser> {
        val devices = runCatchingSafe { deviceDao.findRouterDevicesForUser(userEmail) }
            .getOrElse { error -> return Failure(IoDeviceError.DatabaseError(error)) }

        return Success(devices.map(RouterDeviceMapper::toRouterDeviceInfo))
    }

    override suspend fun findRouterDeviceByMacAddress(macAddress: String): Resource<RouterDevice, IoDeviceError.FindRouterDeviceByMacAddress> {
        val device = runCatchingSafe { deviceDao.findRouterDeviceByMacAddress(macAddress) }
            .getOrElse { error -> return Failure(IoDeviceError.DatabaseError(error)) }
            ?: return Failure(IoDeviceError.NoDeviceFound)

        return Success(RouterDeviceMapper.toRouterDeviceInfo(device))
    }

    override suspend fun saveConnectedDevices(device: RouterDevice, connectedDevices: List<ConnectedDevice>): Resource<Unit, IoDeviceError.SaveConnectedDevices> {
        val connectedDevicesDto =
            connectedDevices.map { ConnectedDeviceMapper.toConnectedDevice(device, it) }
        runCatchingSafe { deviceDao.insertConnectedDevices(*connectedDevicesDto.toTypedArray()) }
            .getOrElse { error -> return Failure(IoDeviceError.DatabaseError(error)) }

        return Success(Unit)
    }

    override suspend fun loadConnectedDevices(device: RouterDevice): Resource<List<ConnectedDevice>, IoDeviceError.LoadConnectedDevices> {
        val connectedDevices = runCatchingSafe { deviceDao.findConnectedDevices(device.macAddress) }
            .getOrElse { error -> return Failure(IoDeviceError.DatabaseError(error)) }

        return Success(connectedDevices.map(ConnectedDeviceMapper::toConnectedDevice))
    }

    override suspend fun saveRouterDevice(device: RouterDevice, userEmail: String): Resource<Unit, IoDeviceError.SaveRouterDevice> {
        val deviceDto = RouterDeviceMapper.toRouterDeviceInfo(device)

        runCatchingSafe { deviceDao.insertRouterDevice(deviceDto) }
            .getOrElse { error -> return Failure(IoDeviceError.DatabaseError(error)) }
        runCatchingSafe { deviceDao.insertUserRouterDevice(UserRouterDeviceDto(userEmail, device.macAddress)) }
            .getOrElse { error -> return Failure(IoDeviceError.DatabaseError(error)) }

        return Success(Unit)
    }

    override suspend fun removeRouterDevice(device: RouterDevice, userEmail: String): Resource<Unit, IoDeviceError.RemoveRouterDevice> {
        runCatchingSafe {
            deviceDao.deleteUserRouterDevice(
                UserRouterDeviceDto(
                    userEmail,
                    device.macAddress
                )
            )
        }
            .getOrElse { error -> return Failure(IoDeviceError.DatabaseError(error)) }
        runCatchingSafe { deviceDao.deleteRouterDeviceByMacAddress(device.macAddress) }
            .getOrElse { error -> return Failure(IoDeviceError.DatabaseError(error)) }

        return Success(Unit)
    }

    override suspend fun findLocalRouterDevice(userEmail: String): Resource<RouterDevice, IoDeviceError.FindLocalRouterDevice> {
        val device = runCatchingSafe {
            deviceDao.findRouterDevicesForUser(userEmail).firstOrNull { it.lanConnected }
        }
            .getOrElse { error -> return Failure(IoDeviceError.DatabaseError(error)) }
            ?: return Failure(IoDeviceError.NoDeviceFound)

        return Success(RouterDeviceMapper.toRouterDeviceInfo(device))
    }
}

private object RouterDeviceMapper {
    fun toRouterDeviceInfo(device: RouterDeviceDto): RouterDevice = RouterDevice(
        lanConnected = device.lanConnected,
        modelName = device.modelName,
        manufacturer = device.manufacturer,
        ipAddressV4 = device.ipAddressV4,
        ipAddressV6 = device.ipAddressV6,
        macAddress = device.macAddress,
        firmwareVersion = device.firmwareVersion,
        serialNumber = device.serialNumber,
        totalMemory = device.totalMemory,
        freeMemory = device.freeMemory,
        availableBands = device.availableBands.split(",").toSet()
    )
    fun toRouterDeviceInfo(device: RouterDevice): RouterDeviceDto = RouterDeviceDto(
        lanConnected = device.lanConnected,
        modelName = device.modelName,
        manufacturer = device.manufacturer,
        ipAddressV4 = device.ipAddressV4,
        ipAddressV6 = device.ipAddressV6,
        macAddress = device.macAddress,
        firmwareVersion = device.firmwareVersion,
        serialNumber = device.serialNumber,
        totalMemory = device.totalMemory,
        freeMemory = device.freeMemory,
        availableBands = device.availableBands.joinToString(separator = ","),
        updatedAt = Clock.System.now().toEpochMilliseconds()
    )
}

private object ConnectedDeviceMapper {
    fun toConnectedDevice(device: ConnectedDeviceDto): ConnectedDevice = ConnectedDevice(
        isActive = device.isActive,
        macAddress = device.macAddress,
        hostName = device.hostName,
        ipAddress = device.ipAddress,
        vendorClassId = device.vendorClassId,
    )

    fun toConnectedDevice(routerDevice: RouterDevice, device: ConnectedDevice): ConnectedDeviceDto = ConnectedDeviceDto(
        routerDeviceMacAddress = routerDevice.macAddress,
        isActive = device.isActive,
        macAddress = device.macAddress,
        hostName = device.hostName,
        ipAddress = device.ipAddress,
        vendorClassId = device.vendorClassId,
    )
}

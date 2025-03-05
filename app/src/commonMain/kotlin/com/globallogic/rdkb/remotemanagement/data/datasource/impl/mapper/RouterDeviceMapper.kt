package com.globallogic.rdkb.remotemanagement.data.datasource.impl.mapper

import com.globallogic.rdkb.remotemanagement.data.db.dto.RouterDeviceDto
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import kotlinx.datetime.Clock

object RouterDeviceMapper {
    fun toRouterDeviceInfo(device: RouterDeviceDto): RouterDevice = RouterDevice(
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

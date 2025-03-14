package com.globallogic.rdkb.remotemanagement.data.datasource.impl.mapper

import com.globallogic.rdkb.remotemanagement.data.db.dto.ConnectedDeviceDto
import com.globallogic.rdkb.remotemanagement.domain.entity.Band
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDeviceStats
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice

object ConnectedDeviceMapper {
    fun toConnectedDevice(device: ConnectedDeviceDto): ConnectedDevice = ConnectedDevice(
        isActive = device.isActive,
        macAddress = device.macAddress,
        hostName = device.hostName,
        ipAddress = device.ipAddress,
        vendorClassId = device.vendorClassId,
        band = Band.entries.firstOrNull { it.radio == device.radio } ?: Band.Band_2_4,
        stats = ConnectedDeviceStats(
            bytesSent = device.bytesSent,
            bytesReceived = device.bytesReceived,
            packetsSent = device.packetsSent,
            packetsReceived = device.packetsReceived,
            errorsSent = device.errorsSent,
        )
    )

    fun toConnectedDevice(routerDevice: RouterDevice, device: ConnectedDevice): ConnectedDeviceDto = ConnectedDeviceDto(
        routerDeviceMacAddress = routerDevice.macAddress,
        isActive = device.isActive,
        macAddress = device.macAddress,
        hostName = device.hostName,
        ipAddress = device.ipAddress,
        vendorClassId = device.vendorClassId,
        radio = device.band.radio,
        bytesSent = device.stats.bytesSent,
        bytesReceived = device.stats.bytesReceived,
        packetsSent = device.stats.packetsSent,
        packetsReceived = device.stats.packetsReceived,
        errorsSent = device.stats.errorsSent,
    )
}

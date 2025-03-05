package com.globallogic.rdkb.remotemanagement.data.datasource.impl.mapper

import com.globallogic.rdkb.remotemanagement.data.db.dto.AccessPointDto
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPoint
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointGroup
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice

object AccessPointMapper {
    fun toAccessPoint(accessPoint: AccessPointDto): AccessPoint = AccessPoint(
        enabled = accessPoint.enabled,
        band = accessPoint.band,
        ssid = accessPoint.ssid,
        availableSecurityModes = accessPoint.availableSecurityModes.split(","),
        securityMode = accessPoint.securityMode,
        clientsCount = accessPoint.clientsCount,
    )

    fun toAccessPoint(device: RouterDevice, accessPointGroup: AccessPointGroup, accessPoint: AccessPoint): AccessPointDto = AccessPointDto(
        routerDeviceMacAddress = device.macAddress,
        accessPointId = accessPointGroup.id,
        enabled = accessPoint.enabled,
        band = accessPoint.band,
        ssid = accessPoint.ssid,
        availableSecurityModes = accessPoint.availableSecurityModes.joinToString(separator = ","),
        securityMode = accessPoint.securityMode,
        clientsCount = accessPoint.clientsCount,
    )
}

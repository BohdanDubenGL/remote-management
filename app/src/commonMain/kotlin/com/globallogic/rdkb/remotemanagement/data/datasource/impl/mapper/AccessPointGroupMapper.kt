package com.globallogic.rdkb.remotemanagement.data.datasource.impl.mapper

import com.globallogic.rdkb.remotemanagement.data.db.dto.AccessPointGroupDto
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointGroup
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice

object AccessPointGroupMapper {
    fun toAccessPointGroup(accessPointGroup: AccessPointGroupDto): AccessPointGroup = AccessPointGroup(
        id = accessPointGroup.id,
        name = accessPointGroup.name,
    )

    fun toAccessPointGroup(device: RouterDevice, accessPointGroup: AccessPointGroup): AccessPointGroupDto = AccessPointGroupDto(
        routerDeviceMacAddress = device.macAddress,
        id = accessPointGroup.id,
        name = accessPointGroup.name,
    )
}

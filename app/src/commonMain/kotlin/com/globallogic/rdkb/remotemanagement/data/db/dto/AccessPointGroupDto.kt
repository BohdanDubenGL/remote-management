package com.globallogic.rdkb.remotemanagement.data.db.dto

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "access_point_group",
    primaryKeys = ["routerDeviceMacAddress", "id"],
    foreignKeys = [
        ForeignKey(
            entity = RouterDeviceDto::class,
            parentColumns = ["macAddress"],
            childColumns = ["routerDeviceMacAddress"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
class AccessPointGroupDto(
    val routerDeviceMacAddress: String,
    val id: Int,
    val name: String,
)

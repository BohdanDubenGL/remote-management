package com.globallogic.rdkb.remotemanagement.data.db.dto

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "access_point",
    primaryKeys = ["routerDeviceMacAddress", "accessPointId", "band"],
    foreignKeys = [
        ForeignKey(
            entity = RouterDeviceDto::class,
            parentColumns = ["macAddress"],
            childColumns = ["routerDeviceMacAddress"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = AccessPointGroupDto::class,
            parentColumns = ["routerDeviceMacAddress", "id"],
            childColumns = ["routerDeviceMacAddress", "accessPointId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
class AccessPointDto(
    val routerDeviceMacAddress: String,
    val accessPointId: Int,
    val band: String,
    val ssid: String,
    val enabled: Boolean,
    val availableSecurityModes: String,
    val securityMode: String,
    val clientsCount: Int,
)

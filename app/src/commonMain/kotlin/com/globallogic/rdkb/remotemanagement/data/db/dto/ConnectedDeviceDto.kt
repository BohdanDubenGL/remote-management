package com.globallogic.rdkb.remotemanagement.data.db.dto

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "connected_device",
    foreignKeys = [
        ForeignKey(
            entity = RouterDeviceDto::class,
            parentColumns = ["macAddress"],
            childColumns = ["routerDeviceMacAddress"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
class ConnectedDeviceDto(
    @PrimaryKey val macAddress: String,
    val routerDeviceMacAddress: String,
    val hostName: String,
    val ssid: String,
    val channel: Int,
    val rssi: Int,
    val bandWidth: String,
)

package com.globallogic.rdkb.remotemanagement.data.db.dto

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity(
    tableName = "router_device_info",
    foreignKeys = [
        ForeignKey(
            entity = RouterDeviceDto::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class RouterDeviceInfoDto
@OptIn(ExperimentalUuidApi::class) constructor(
    @PrimaryKey val id: String = Uuid.random().toString(),
    val macAddress: String,
    val lanConnected: Boolean,
    val connectedExtender: Boolean,
    val modelName: String,
    val ipAddress: String,
    val firmwareVersion: String,
    val serialNumber: String,
    val processorLoadPercent: Int,
    val memoryUsagePercent: Int,
    val totalDownloadTraffic: Long,
    val totalUploadTraffic: Long,
    val availableBands: String,
)

package com.globallogic.rdkb.remotemanagement.data.db.dto

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "user_router_device",
    primaryKeys = ["userEmail", "routerDeviceMacAddress"],
    foreignKeys = [
        ForeignKey(
            entity = UserDto::class,
            parentColumns = ["email"],
            childColumns = ["userEmail"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = RouterDeviceDto::class,
            parentColumns = ["macAddress"],
            childColumns = ["routerDeviceMacAddress"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class UserRouterDeviceDto(
    val userEmail: String,
    val routerDeviceMacAddress: String
)

package com.globallogic.rdkb.remotemanagement.data.db.dto

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "user_router_device",
    primaryKeys = ["userId", "routerDeviceId"],
    foreignKeys = [
        ForeignKey(
            entity = UserDto::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = RouterDeviceDto::class,
            parentColumns = ["id"],
            childColumns = ["routerDeviceId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class UserRouterDeviceDto(
    val userId: String,
    val routerDeviceId: String
)

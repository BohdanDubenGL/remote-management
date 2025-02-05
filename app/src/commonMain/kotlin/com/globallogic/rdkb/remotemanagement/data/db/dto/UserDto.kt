package com.globallogic.rdkb.remotemanagement.data.db.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserDto(
    @PrimaryKey val email: String,
    val name: String,
    val password: String,
)

package com.globallogic.rdkb.remotemanagement.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.globallogic.rdkb.remotemanagement.data.db.dto.RouterDeviceDto
import com.globallogic.rdkb.remotemanagement.data.db.dto.RouterDeviceInfoDto
import com.globallogic.rdkb.remotemanagement.data.db.dto.UserDto
import com.globallogic.rdkb.remotemanagement.data.db.dto.UserRouterDeviceDto

@ConstructedBy(AppDatabaseConstructor::class)
@Database(
    entities = [
        UserDto::class,
        RouterDeviceDto::class,
        RouterDeviceInfoDto::class,
        UserRouterDeviceDto::class,
    ],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getUserDao(): UserDao
    abstract fun getRouterDeviceDao(): RouterDeviceDao

    companion object {
        val fileName: String = "app.db"
    }
}

expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>

package com.globallogic.rdkb.remotemanagement.data.db

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

inline fun <reified DB: RoomDatabase> createDatabaseBuilder(
    fileName: String,
): RoomDatabase.Builder<DB> = Room.databaseBuilder<DB>(
    name = File(System.getProperty("user.home") + "/.remoteManagement", fileName).absolutePath,
)

package com.globallogic.rdkb.remotemanagement.data.db

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

fun <DB: RoomDatabase> createRoomDatabase(
    builder: RoomDatabase.Builder<DB>
): DB = builder
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
//    .fallbackToDestructiveMigration(true)
    .build()

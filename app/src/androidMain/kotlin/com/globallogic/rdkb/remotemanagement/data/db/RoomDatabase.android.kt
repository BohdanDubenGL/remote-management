package com.globallogic.rdkb.remotemanagement.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

inline fun <reified DB: RoomDatabase> createDatabaseBuilder(
    context: Context,
    fileName: String,
): RoomDatabase.Builder<DB> = Room.databaseBuilder(
    context = context.applicationContext,
    name = context.applicationContext.getDatabasePath(fileName).absolutePath,
    klass = DB::class.java
)

package com.globallogic.rdkb.remotemanagement.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlin.reflect.KClass

fun <DB: RoomDatabase> createDatabaseBuilder(
    context: Context,
    fileName: String,
    dbClass: KClass<DB>,
): RoomDatabase.Builder<DB> = Room.databaseBuilder(
    context = context.applicationContext,
    name = context.applicationContext.getDatabasePath(fileName).absolutePath,
    klass = dbClass.java
)

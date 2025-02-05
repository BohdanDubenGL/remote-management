package com.globallogic.rdkb.remotemanagement.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.globallogic.rdkb.remotemanagement.data.db.dto.RouterDeviceDto
import com.globallogic.rdkb.remotemanagement.data.db.dto.RouterDeviceInfoDto

@Dao
interface RouterDeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRouterDevice(router: RouterDeviceDto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRouterDeviceInfo(info: RouterDeviceInfoDto)

    @Transaction
    @Query("SELECT * FROM router_device_info WHERE macAddress = :macAddress")
    suspend fun getRouterDeviceWithInfo(macAddress: String): RouterDeviceInfoDto?
}

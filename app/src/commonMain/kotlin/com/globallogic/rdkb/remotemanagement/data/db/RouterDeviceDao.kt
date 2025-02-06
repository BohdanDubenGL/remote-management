package com.globallogic.rdkb.remotemanagement.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.globallogic.rdkb.remotemanagement.data.db.dto.ConnectedDeviceDto
import com.globallogic.rdkb.remotemanagement.data.db.dto.RouterDeviceDto
import com.globallogic.rdkb.remotemanagement.data.db.dto.UserRouterDeviceDto

@Dao
interface RouterDeviceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRouterDevice(router: RouterDeviceDto)

    @Upsert
    suspend fun updateRouterDevice(router: RouterDeviceDto)

    @Query("DELETE FROM router_device WHERE macAddress = :macAddress")
    suspend fun deleteRouterDeviceByMacAddress(macAddress: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserRouterDevice(userRouterDevice: UserRouterDeviceDto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConnectedDevices(vararg connectedDevices: ConnectedDeviceDto)

    @Delete
    suspend fun deleteUserRouterDevice(userRouterDevice: UserRouterDeviceDto)

    @Transaction
    @Query("SELECT * FROM router_device WHERE macAddress = :macAddress LIMIT 1")
    suspend fun findRouterDeviceByMacAddress(macAddress: String): RouterDeviceDto?

    @Query("""SELECT r.* FROM user_router_device ur
        JOIN router_device r ON ur.routerDeviceMacAddress = r.macAddress WHERE ur.userEmail = :userEmail""")
    suspend fun findRouterDevicesForUser(userEmail: String): List<RouterDeviceDto>

    @Query("SELECT * FROM connected_device WHERE routerDeviceMacAddress = :macAddress")
    suspend fun findConnectedDevices(macAddress: String): List<ConnectedDeviceDto>
}

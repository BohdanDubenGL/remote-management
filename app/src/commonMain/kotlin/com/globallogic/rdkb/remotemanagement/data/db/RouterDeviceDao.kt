package com.globallogic.rdkb.remotemanagement.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.globallogic.rdkb.remotemanagement.data.db.dto.ConnectedDeviceDto
import com.globallogic.rdkb.remotemanagement.data.db.dto.RouterDeviceDto
import com.globallogic.rdkb.remotemanagement.data.db.dto.UserRouterDeviceDto

@Dao
interface RouterDeviceDao {
    @Upsert
    suspend fun upsertRouterDevice(router: RouterDeviceDto)

    @Query("DELETE FROM router_device WHERE macAddress = :macAddress")
    suspend fun deleteRouterDeviceByMacAddress(macAddress: String)

    @Upsert
    suspend fun upsertUserRouterDevice(userRouterDevice: UserRouterDeviceDto)

    @Upsert
    suspend fun upsertConnectedDevices(vararg connectedDevices: ConnectedDeviceDto)

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

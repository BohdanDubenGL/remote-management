package com.globallogic.rdkb.remotemanagement.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.globallogic.rdkb.remotemanagement.data.db.dto.AccessPointDto
import com.globallogic.rdkb.remotemanagement.data.db.dto.AccessPointGroupDto
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

    @Delete
    suspend fun deleteUserRouterDevice(userRouterDevice: UserRouterDeviceDto)

    @Transaction
    @Query("SELECT * FROM router_device WHERE macAddress = :macAddress LIMIT 1")
    suspend fun findRouterDeviceByMacAddress(macAddress: String): RouterDeviceDto?

    @Query("""SELECT r.* FROM user_router_device ur
        JOIN router_device r ON ur.routerDeviceMacAddress = r.macAddress WHERE ur.userEmail = :userEmail""")
    suspend fun findRouterDevicesForUser(userEmail: String): List<RouterDeviceDto>


    @Upsert
    suspend fun upsertConnectedDevices(vararg connectedDevices: ConnectedDeviceDto)

    @Query("SELECT * FROM connected_device WHERE routerDeviceMacAddress = :macAddress")
    suspend fun findConnectedDevices(macAddress: String): List<ConnectedDeviceDto>


    @Upsert
    suspend fun upsertAccessPointGroups(vararg accessPointGroup: AccessPointGroupDto)

    @Query("SELECT * FROM access_point_group WHERE routerDeviceMacAddress = :macAddress")
    suspend fun findAccessPointGroups(macAddress: String): List<AccessPointGroupDto>

    @Query("DELETE FROM access_point_group WHERE routerDeviceMacAddress = :macAddress AND id NOT IN (:ids)")
    suspend fun deleteAccessPointGroupsForDevice(macAddress: String, ids: List<Int>)


    @Upsert
    suspend fun upsertAccessPoints(vararg accessPointGroup: AccessPointDto)

    @Query("SELECT * FROM access_point WHERE routerDeviceMacAddress = :macAddress AND accessPointId = :accessPointGroupId")
    suspend fun findAccessPoints(macAddress: String, accessPointGroupId: Int): List<AccessPointDto>

    @Query("""DELETE FROM access_point
        WHERE routerDeviceMacAddress = :macAddress AND accessPointId = :accessPointGroupId AND band NOT IN (:bands)""")
    suspend fun deleteAccessPointsForDevice(macAddress: String, accessPointGroupId: Int, bands: List<String>)
}

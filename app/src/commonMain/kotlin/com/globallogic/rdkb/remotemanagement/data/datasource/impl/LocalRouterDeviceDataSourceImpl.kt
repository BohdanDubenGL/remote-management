package com.globallogic.rdkb.remotemanagement.data.datasource.impl

import com.globallogic.rdkb.remotemanagement.data.datasource.LocalRouterDeviceDataSource
import com.globallogic.rdkb.remotemanagement.data.datasource.impl.mapper.AccessPointGroupMapper
import com.globallogic.rdkb.remotemanagement.data.datasource.impl.mapper.AccessPointMapper
import com.globallogic.rdkb.remotemanagement.data.datasource.impl.mapper.ConnectedDeviceMapper
import com.globallogic.rdkb.remotemanagement.data.datasource.impl.mapper.RouterDeviceMapper
import com.globallogic.rdkb.remotemanagement.data.db.RouterDeviceDao
import com.globallogic.rdkb.remotemanagement.data.db.dto.UserRouterDeviceDto
import com.globallogic.rdkb.remotemanagement.data.error.IoDeviceError
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointGroup
import com.globallogic.rdkb.remotemanagement.domain.entity.AccessPointSettings
import com.globallogic.rdkb.remotemanagement.domain.entity.ConnectedDevice
import com.globallogic.rdkb.remotemanagement.domain.entity.RouterDevice
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Failure
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.runCatchingSafe

class LocalRouterDeviceDataSourceImpl(
    private val deviceDao: RouterDeviceDao,
) : LocalRouterDeviceDataSource {

    override suspend fun saveRouterDevice(device: RouterDevice, userEmail: String): Resource<Unit, IoDeviceError.SaveRouterDevice> {
        val deviceDto = RouterDeviceMapper.toRouterDeviceInfo(device)

        runCatchingSafe { deviceDao.upsertRouterDevice(deviceDto) }
            .getOrElse { error -> return Failure(IoDeviceError.DatabaseError(error)) }
        runCatchingSafe { deviceDao.upsertUserRouterDevice(UserRouterDeviceDto(userEmail, device.macAddress)) }
            .getOrElse { error -> return Failure(IoDeviceError.DatabaseError(error)) }

        return Success(Unit)
    }

    override suspend fun removeRouterDevice(device: RouterDevice, userEmail: String): Resource<Unit, IoDeviceError.RemoveRouterDevice> {
        runCatchingSafe { deviceDao.deleteUserRouterDevice(UserRouterDeviceDto(userEmail, device.macAddress)) }
            .getOrElse { error -> return Failure(IoDeviceError.DatabaseError(error)) }
        runCatchingSafe { deviceDao.deleteRouterDeviceByMacAddress(device.macAddress) }
            .getOrElse { error -> return Failure(IoDeviceError.DatabaseError(error)) }

        return Success(Unit)
    }

    override suspend fun loadRouterDevicesForUser(userEmail: String): Resource<List<RouterDevice>, IoDeviceError.LoadRouterDevicesForUser> {
        val devices = runCatchingSafe { deviceDao.findRouterDevicesForUser(userEmail) }
            .getOrElse { error -> return Failure(IoDeviceError.DatabaseError(error)) }

        return Success(devices.map(RouterDeviceMapper::toRouterDeviceInfo))
    }

    override suspend fun findRouterDeviceByMacAddress(macAddress: String): Resource<RouterDevice, IoDeviceError.FindRouterDeviceByMacAddress> {
        val device = runCatchingSafe { deviceDao.findRouterDeviceByMacAddress(macAddress) }
            .getOrElse { error -> return Failure(IoDeviceError.DatabaseError(error)) }
            ?: return Failure(IoDeviceError.NoDeviceFound)

        return Success(RouterDeviceMapper.toRouterDeviceInfo(device))
    }


    override suspend fun saveConnectedDevices(device: RouterDevice, connectedDevices: List<ConnectedDevice>): Resource<Unit, IoDeviceError.SaveConnectedDevices> {
        val connectedDevicesDto = connectedDevices.map { ConnectedDeviceMapper.toConnectedDevice(device, it) }
        runCatchingSafe { deviceDao.upsertConnectedDevices(*connectedDevicesDto.toTypedArray()) }
            .getOrElse { error -> return Failure(IoDeviceError.DatabaseError(error)) }

        return Success(Unit)
    }

    override suspend fun loadConnectedDevices(device: RouterDevice): Resource<List<ConnectedDevice>, IoDeviceError.LoadConnectedDevices> {
        val connectedDevices = runCatchingSafe { deviceDao.findConnectedDevices(device.macAddress) }
            .getOrElse { error -> return Failure(IoDeviceError.DatabaseError(error)) }

        return Success(connectedDevices.map(ConnectedDeviceMapper::toConnectedDevice))
    }


    override suspend fun saveAccessPointGroups(
        device: RouterDevice,
        accessPointGroups: List<AccessPointGroup>
    ): Resource<Unit, IoDeviceError.AccessPointGroups> {
        runCatchingSafe { deviceDao.deleteAccessPointGroupsForDevice(device.macAddress, accessPointGroups.map { it.id }) }
            .getOrElse { error -> return Failure(IoDeviceError.DatabaseError(error)) }

        val accessPointGroupsDto = accessPointGroups.map { AccessPointGroupMapper.toAccessPointGroup(device, it) }
        runCatchingSafe { deviceDao.upsertAccessPointGroups(*accessPointGroupsDto.toTypedArray()) }
            .getOrElse { error -> return Failure(IoDeviceError.DatabaseError(error)) }

        return Success(Unit)
    }

    override suspend fun loadAccessPointGroups(
        device: RouterDevice
    ): Resource<List<AccessPointGroup>, IoDeviceError.AccessPointGroups> {
        val connectedDevices = runCatchingSafe { deviceDao.findAccessPointGroups(device.macAddress) }
            .getOrElse { error -> return Failure(IoDeviceError.DatabaseError(error)) }

        return Success(connectedDevices.map(AccessPointGroupMapper::toAccessPointGroup))
    }


    override suspend fun saveDeviceAccessPointSettings(
        device: RouterDevice,
        accessPointSettings: AccessPointSettings
    ): Resource<Unit, IoDeviceError.AccessPoints> {
        val accessPoints = accessPointSettings.accessPoints
        val accessPointGroup = accessPointSettings.accessPointGroup

        runCatchingSafe { deviceDao.deleteAccessPointsForDevice(device.macAddress, accessPointGroup.id, accessPoints.map { it.band }) }
            .getOrElse { error -> return Failure(IoDeviceError.DatabaseError(error)) }

        val accessPointGroupsDto = accessPoints.map { AccessPointMapper.toAccessPoint(device, accessPointGroup, it) }
        runCatchingSafe { deviceDao.upsertAccessPoints(*accessPointGroupsDto.toTypedArray()) }
            .getOrElse { error -> return Failure(IoDeviceError.DatabaseError(error)) }

        return Success(Unit)
    }

    override suspend fun loadDeviceAccessPointSettings(
        device: RouterDevice,
        accessPointGroup: AccessPointGroup
    ): Resource<AccessPointSettings, IoDeviceError.AccessPoints> {
        val connectedDevices = runCatchingSafe { deviceDao.findAccessPoints(device.macAddress, accessPointGroup.id) }
            .getOrElse { error -> return Failure(IoDeviceError.DatabaseError(error)) }

        return Success(AccessPointSettings(
            accessPointGroup,
            connectedDevices.map(AccessPointMapper::toAccessPoint)
        ))
    }
}

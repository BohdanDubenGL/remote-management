package com.globallogic.rdkb.remotemanagement.data.network.service.impl

import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralAccessorService
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralPropertyService
import com.globallogic.rdkb.remotemanagement.data.network.service.model.DeviceProperty
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError
import com.globallogic.rdkb.remotemanagement.domain.utils.map

class WifiMotionAccessor(
    private val rdkCentralPropertyService: RdkCentralPropertyService,
    private val macAddress: String,
) : RdkCentralAccessorService.WifiMotionAccessor {
    override suspend fun getSensingDeviceMacAddress(): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.MotionDetectionDeviceMacAddress)
    }

    override suspend fun getSensingEventCount(): Resource<Int, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.MotionDetectionNumberOfEntries)
            .map { it.toInt() }
    }

    override suspend fun getMotionPercent(): Resource<Int, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.MotionDetectionPercent)
            .map { it.toInt() }
    }

    override suspend fun getSensingEvents(): Resource<List<RdkCentralAccessorService.WifiMotionEventAccessor>, ThrowableResourceError> {
        return getSensingEventCount().map { count ->
            List(count) { eventId -> event(eventId + 1) }
        }
    }


    override suspend fun setSensingDeviceMacAddress(clientMacAddress: String): Resource<Unit, ThrowableResourceError> {
        return rdkCentralPropertyService.setDeviceProperty(macAddress, DeviceProperty.MotionDetectionDeviceMacAddress, clientMacAddress)
    }


    override fun event(eventId: Int): RdkCentralAccessorService.WifiMotionEventAccessor {
        return WifiMotionEventAccessor(rdkCentralPropertyService, macAddress, eventId)
    }
}

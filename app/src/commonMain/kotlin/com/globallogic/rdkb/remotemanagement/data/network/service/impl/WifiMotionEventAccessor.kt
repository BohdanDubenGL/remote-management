package com.globallogic.rdkb.remotemanagement.data.network.service.impl

import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralAccessorService
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralPropertyService
import com.globallogic.rdkb.remotemanagement.data.network.service.model.DeviceProperty
import com.globallogic.rdkb.remotemanagement.domain.entity.WifiMotionEvent
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError
import com.globallogic.rdkb.remotemanagement.domain.utils.map

class WifiMotionEventAccessor(
    private val rdkCentralPropertyService: RdkCentralPropertyService,
    private val macAddress: String,
    override val eventId: Int,
) : RdkCentralAccessorService.WifiMotionEventAccessor {
    override suspend fun getDeviceMacAddress(): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.MotionDetectionSensingDevice(eventId))
    }

    override suspend fun getType(): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.MotionDetectionType(eventId))
    }

    override suspend fun getTime(): Resource<String, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperty(macAddress, DeviceProperty.MotionDetectionTime(eventId))
    }

    override suspend fun getMotionEvent(): Resource<WifiMotionEvent, ThrowableResourceError> {
        return rdkCentralPropertyService.getDeviceProperties(
            macAddress,
            DeviceProperty.MotionDetectionSensingDevice(eventId),
            DeviceProperty.MotionDetectionType(eventId),
            DeviceProperty.MotionDetectionTime(eventId),
        ).map { (macAddress, type, time) -> WifiMotionEvent(eventId, macAddress, type, time) }
    }
}

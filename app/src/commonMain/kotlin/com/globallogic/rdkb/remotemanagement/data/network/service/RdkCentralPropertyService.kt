package com.globallogic.rdkb.remotemanagement.data.network.service

import com.globallogic.rdkb.remotemanagement.data.network.service.model.DeviceParameter
import com.globallogic.rdkb.remotemanagement.data.network.service.model.DeviceProperty
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError

interface RdkCentralPropertyService {
    suspend fun getAvailableDevices(): Resource <List<String>, ThrowableResourceError>

    suspend fun <T: Any> getDeviceProperty(
        macAddress: String,
        property: DeviceProperty.Property<T>,
    ): Resource<T, ThrowableResourceError>

    suspend fun <T: Any> getDeviceProperties(
        macAddress: String,
        vararg properties: DeviceProperty.Property<T>,
    ): Resource<List<T>, ThrowableResourceError>

    suspend fun <T: Any> setDeviceProperty(
        macAddress: String,
        property: DeviceProperty.Property<T>,
        value: T,
    ): Resource<Unit, ThrowableResourceError>

    suspend fun setDeviceProperties(
        macAddress: String,
        vararg parameters: DeviceParameter<*>,
    ): Resource<Unit, ThrowableResourceError>

    suspend fun doDeviceAction(
        macAddress: String,
        action: DeviceProperty.Action,
    ): Resource<Unit, ThrowableResourceError>
}

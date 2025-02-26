package com.globallogic.rdkb.remotemanagement.data.network.service.impl

import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralPropertyService
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralNetworkApiService
import com.globallogic.rdkb.remotemanagement.data.network.service.exception.CantChangeDevicePropertyException
import com.globallogic.rdkb.remotemanagement.data.network.service.exception.CantExecuteDeviceActionException
import com.globallogic.rdkb.remotemanagement.data.network.service.exception.CantLoadDevicePropertyException
import com.globallogic.rdkb.remotemanagement.data.network.service.exception.CantLoadDevicesException
import com.globallogic.rdkb.remotemanagement.data.network.service.model.DeviceParameter
import com.globallogic.rdkb.remotemanagement.data.network.service.model.DeviceProperty
import com.globallogic.rdkb.remotemanagement.data.network.service.model.request.ParametersRequest
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Failure
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource.Success
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError
import com.globallogic.rdkb.remotemanagement.domain.utils.flatMapData
import com.globallogic.rdkb.remotemanagement.domain.utils.map

class RdkCentralPropertyServiceImpl(
    private val rdkCentralNetworkApiService: RdkCentralNetworkApiService,
) : RdkCentralPropertyService {

    override suspend fun getAvailableDevices(): Resource<List<String>, ThrowableResourceError> {
        return rdkCentralNetworkApiService.getAvailableDevices()
            .flatMapData { response ->
                when {
                    response.devices == null -> Failure(ThrowableResourceError(CantLoadDevicesException()))
                    else -> response.devices
                        .mapNotNull { device -> device?.id?.split(":")?.getOrNull(1) }
                        .let(::Success)
                }
            }
    }

    override suspend fun <T : Any> getDeviceProperties(
        macAddress: String,
        vararg properties: DeviceProperty.Property<T>
    ): Resource<List<T>, ThrowableResourceError> {
        return rdkCentralNetworkApiService.getDeviceProperties(macAddress, *properties.map { it.name }.toTypedArray())
            .map { response ->
                response.parameters
                    ?.mapIndexedNotNull { index, parameter ->
                        val property = properties.getOrNull(index) ?: return@mapIndexedNotNull null
                        if (
                            property.name != parameter?.name ||
                            property.dataType.value != parameter.dataType ||
                            parameter.value == null
                        ) return@mapIndexedNotNull null
                        return@mapIndexedNotNull property.dataType.fromMapper(parameter.value)
                    }
                    .orEmpty()
            }
    }

    override suspend fun <T : Any> getDeviceProperty(
        macAddress: String,
        property: DeviceProperty.Property<T>
    ): Resource<T, ThrowableResourceError> {
        return rdkCentralNetworkApiService.getDeviceProperties(macAddress, property.name)
            .flatMapData { response ->
                val parameter = response.parameters?.singleOrNull()
                    ?: return@flatMapData Failure(ThrowableResourceError(CantLoadDevicePropertyException(macAddress, property)))
                if (
                    property.name != parameter.name ||
                    property.dataType.value != parameter.dataType ||
                    parameter.value == null
                ) return@flatMapData Failure(ThrowableResourceError(CantLoadDevicePropertyException(macAddress, property)))

                val result = property.dataType.fromMapper(parameter.value)
                return@flatMapData when {
                    result != null -> Success(result)
                    else -> Failure(ThrowableResourceError(CantLoadDevicePropertyException(macAddress, property)))
                }
            }
    }

    override suspend fun <T : Any> setDeviceProperty(
        macAddress: String,
        property: DeviceProperty.Property<T>,
        value: T
    ): Resource<Unit, ThrowableResourceError> = setDeviceProperties(macAddress, DeviceParameter(property, value))

    override suspend fun setDeviceProperties(
        macAddress: String,
        vararg parameters: DeviceParameter<*>
    ): Resource<Unit, ThrowableResourceError> {
        val params = parameters.map { parameter ->
            ParametersRequest.Parameter(
                dataType = parameter.property.dataType.value,
                name = parameter.property.name,
                value = parameter.valueToString(),
            )
        }
        return rdkCentralNetworkApiService.setDeviceProperties(macAddress, ParametersRequest(params))
            .flatMapData { response ->
                return@flatMapData when {
                    response.parameters == null -> Failure(ThrowableResourceError(CantChangeDevicePropertyException(macAddress, *parameters)))
                    response.parameters.all { it?.message == "Success" } -> Success(Unit)
                    else -> Failure(ThrowableResourceError(CantChangeDevicePropertyException(macAddress, *parameters)))
                }
            }
    }

    override suspend fun doDeviceAction(
        macAddress: String,
        action: DeviceProperty.Action
    ): Resource<Unit, ThrowableResourceError> {
        val parameter = ParametersRequest.Parameter(
            dataType = action.dataType.value,
            name = action.name,
            value = action.value,
        )
        return rdkCentralNetworkApiService.setDeviceProperties(macAddress, ParametersRequest(listOf(parameter)))
            .flatMapData { response ->
                return@flatMapData when {
                    response.parameters == null -> Failure(ThrowableResourceError(CantExecuteDeviceActionException(macAddress, action)))
                    response.parameters.all { it?.message == "Success" } -> Success(Unit)
                    else -> Failure(ThrowableResourceError(CantExecuteDeviceActionException(macAddress, action)))
                }
            }
    }
}

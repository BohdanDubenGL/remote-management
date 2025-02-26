package com.globallogic.rdkb.remotemanagement.data.network.service

import com.globallogic.rdkb.remotemanagement.data.network.service.model.response.GetDevicesResponse
import com.globallogic.rdkb.remotemanagement.data.network.service.model.response.GetNamespaceResponse
import com.globallogic.rdkb.remotemanagement.data.network.service.model.response.GetParametersResponse
import com.globallogic.rdkb.remotemanagement.data.network.service.model.request.ParametersRequest
import com.globallogic.rdkb.remotemanagement.data.network.service.model.response.SetParametersResponse
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError

interface RdkCentralNetworkApiService {
    suspend fun getAvailableDevices(): Resource<GetDevicesResponse, ThrowableResourceError>

    suspend fun getDeviceProperties(
        macAddress: String,
        vararg parameters: String,
    ): Resource<GetParametersResponse, ThrowableResourceError>

    suspend fun getDeviceNamespaces(
        macAddress: String,
        vararg namespaces: String,
    ): Resource<GetNamespaceResponse, ThrowableResourceError>

    suspend fun setDeviceProperties(
        macAddress: String,
        parameters: ParametersRequest,
    ): Resource<SetParametersResponse, ThrowableResourceError>
}

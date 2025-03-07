package com.globallogic.rdkb.remotemanagement.data.network.service.impl

import RdkBRemoteManagement.app.BuildConfig
import com.globallogic.rdkb.remotemanagement.data.network.safeGet
import com.globallogic.rdkb.remotemanagement.data.network.safePatch
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralNetworkApiService
import com.globallogic.rdkb.remotemanagement.data.network.service.model.response.GetDevicesResponse
import com.globallogic.rdkb.remotemanagement.data.network.service.model.response.GetNamespaceResponse
import com.globallogic.rdkb.remotemanagement.data.network.service.model.response.GetParametersResponse
import com.globallogic.rdkb.remotemanagement.data.network.service.model.request.ParametersRequest
import com.globallogic.rdkb.remotemanagement.data.network.service.model.response.SetParametersResponse
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.client.request.port
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

class RdkCentralNetworkApiServiceImpl(
    private val httpClient: HttpClient,
) : RdkCentralNetworkApiService {
    override suspend fun getAvailableDevices(): Resource<GetDevicesResponse, ThrowableResourceError> {
        return httpClient.safeGet<GetDevicesResponse> {
            url("/api/v2/devices")
            port = BuildConfig.webPaDevicesPort
            contentType(ContentType.Application.Json)
        }
    }

    override suspend fun getDeviceProperties(
        macAddress: String,
        vararg parameters: String
    ): Resource<GetParametersResponse, ThrowableResourceError> {
        return httpClient.safeGet<GetParametersResponse> {
            url("/api/v2/device/mac:${macAddress.formatMac()}/config")
            contentType(ContentType.Application.Json)
            parameter("names", parameters.joinToString(separator = ","))
        }
    }

    override suspend fun getDeviceNamespaces(
        macAddress: String,
        vararg namespaces: String
    ): Resource<GetNamespaceResponse, ThrowableResourceError> {
        return httpClient.safeGet<GetNamespaceResponse> {
            url("/api/v2/device/mac:${macAddress.formatMac()}/config")
            contentType(ContentType.Application.Json)
            parameter("names", namespaces.joinToString(separator = ","))
        }
    }

    override suspend fun setDeviceProperties(
        macAddress: String,
        parameters: ParametersRequest
    ): Resource<SetParametersResponse, ThrowableResourceError> {
        return httpClient.safePatch {
            url("/api/v2/device/mac:${macAddress.formatMac()}/config")
            contentType(ContentType.Application.Json)
            setBody(parameters)
        }
    }

    companion object {
        private fun String.formatMac(): String = replace(":", "").lowercase()
    }
}

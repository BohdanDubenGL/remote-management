package com.globallogic.rdkb.remotemanagement.data.network.service.impl

import com.globallogic.rdkb.remotemanagement.data.network.safeGet
import com.globallogic.rdkb.remotemanagement.data.network.safePatch
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralApiService
import com.globallogic.rdkb.remotemanagement.data.network.service.RouterDeviceProperty
import com.globallogic.rdkb.remotemanagement.data.network.service.model.GetDevicesResponse
import com.globallogic.rdkb.remotemanagement.data.network.service.model.GetNamespaceResponse
import com.globallogic.rdkb.remotemanagement.data.network.service.model.GetPropertyResponse
import com.globallogic.rdkb.remotemanagement.data.network.service.model.SetPropertyResponse
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.client.request.port
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

class RdkCentralApiServiceImpl(
    private val httpClient: HttpClient
) : RdkCentralApiService {

    override suspend fun getAvailableDevices(): Resource <GetDevicesResponse, ThrowableResourceError> {
        return httpClient.safeGet<GetDevicesResponse> {
            url("/api/v2/devices")
            port = 8080
            contentType(ContentType.Application.Json)
        }
    }

    override suspend fun <T> getDeviceProperties(
        deviceMacAddress: String,
        vararg properties: RouterDeviceProperty.Get<T>
    ): Resource<GetPropertyResponse, ThrowableResourceError> {
        return httpClient.safeGet<GetPropertyResponse> {
            url("/api/v2/device/mac:$deviceMacAddress/config")
            contentType(ContentType.Application.Json)
            parameter("names", properties.joinToString(separator = ",") { it.name })
        }
    }

    override suspend fun <T> getDeviceNamespace(
        deviceMacAddress: String,
        vararg properties: RouterDeviceProperty.Get<T>
    ): Resource<GetNamespaceResponse, ThrowableResourceError> {
        return httpClient.safeGet<GetNamespaceResponse> {
            url("/api/v2/device/mac:$deviceMacAddress/config")
            contentType(ContentType.Application.Json)
            parameter("names", properties.joinToString(separator = ",") { it.name })
        }
    }

    // curl -X PATCH http://webpa.rdkcentral.com:9003/api/v2/device/mac:4e07b781a3b8/config -d '{"parameters": [ {"dataType": 0, "name": "Device.WiFi.SSID.10001.SSID", "value": "Filogic_5G2"}]}' -H 'Authorization:Basic d3B1c2VyOndlYnBhQDEyMzQ1Njc4OTAK'
    // {"parameters":[{"name":"Device.WiFi.SSID.10001.SSID","message":"Success"}],"statusCode":200}
    override suspend fun <T> setDeviceProperty(
        deviceMacAddress: String,
        deviceProperty: RouterDeviceProperty.Set<T>,
        value: T,
    ): Resource<SetPropertyResponse, ThrowableResourceError> {
        return httpClient.safePatch {
            url("/api/v2/device/mac:$deviceMacAddress/config")
            setBody("{\"parameters\": [ {\"dataType\": 0, \"name\": \"${deviceProperty.name}\", \"value\": \"$value\"}]}")
        }
    }

    override suspend fun doDeviceAction(
        deviceMacAddress: String,
        deviceAction: RouterDeviceProperty.Action
    ): Resource<Boolean, ThrowableResourceError> {
        TODO("Not yet implemented")
    }
}

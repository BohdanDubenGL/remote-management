package com.globallogic.rdkb.remotemanagement.data.network.service.impl

import com.globallogic.rdkb.remotemanagement.data.network.safeGet
import com.globallogic.rdkb.remotemanagement.data.network.safePatch
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralApiService
import com.globallogic.rdkb.remotemanagement.data.network.service.RouterDevice
import com.globallogic.rdkb.remotemanagement.data.network.service.model.GetPropertyResponse
import com.globallogic.rdkb.remotemanagement.data.network.service.model.SetPropertyResponse
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

class RdkCentralApiServiceImpl(
    private val httpClient: HttpClient
) : RdkCentralApiService {

    // curl -X GET 'http://webpa.rdkcentral.com:9003/api/v2/device/mac:dca6320eb8bb/config?names=Device.DeviceInfo.SoftwareVersion' -H 'Authorization:Basic d3B1c2VyOndlYnBhQDEyMzQ1Njc4OTAK'
    // {"parameters":[{"name":"Device.DeviceInfo.SoftwareVersion","value":"rdkb-generic-broadband-image_rdk-next_20240710125200","dataType":0,"parameterCount":1,"message":"Success"}],"statusCode":200}
    override suspend fun <T> getDeviceProperty(
        deviceMacAddress: String,
        deviceProperty: RouterDevice.Get<T>
    ): Resource<GetPropertyResponse, ThrowableResourceError> {
        return httpClient.safeGet<GetPropertyResponse> {
            url("/api/v2/device/mac:$deviceMacAddress/config")
            contentType(ContentType.Application.Json)
            parameter("names", deviceProperty.name)
        }
    }

    // curl -X PATCH http://webpa.rdkcentral.com:9003/api/v2/device/mac:4e07b781a3b8/config -d '{"parameters": [ {"dataType": 0, "name": "Device.WiFi.SSID.10001.SSID", "value": "Filogic_5G2"}]}' -H 'Authorization:Basic d3B1c2VyOndlYnBhQDEyMzQ1Njc4OTAK'
    // {"parameters":[{"name":"Device.WiFi.SSID.10001.SSID","message":"Success"}],"statusCode":200}
    override suspend fun <T> setDeviceProperty(
        deviceMacAddress: String,
        deviceProperty: RouterDevice.Set<T>,
        value: T,
    ): Resource<SetPropertyResponse, ThrowableResourceError> {
        return httpClient.safePatch {
            url("/api/v2/device/mac:$deviceMacAddress/config")
            setBody("{\"parameters\": [ {\"dataType\": 0, \"name\": \"${deviceProperty.name}\", \"value\": \"$value\"}]}")
        }
    }

    override suspend fun doDeviceAction(
        deviceMacAddress: String,
        deviceAction: RouterDevice.Action
    ): Resource<Boolean, ThrowableResourceError> {
        TODO("Not yet implemented")
    }
}

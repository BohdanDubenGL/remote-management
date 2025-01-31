package com.globallogic.rdkb.remotemanagement.data.network.service.impl

import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralApiService
import com.globallogic.rdkb.remotemanagement.data.network.service.RouterDevice
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.reflect.TypeInfo

class RdkCentralApiServiceImpl(
    private val httpClient: HttpClient
) : RdkCentralApiService {

    // curl -X GET 'http://webpa.rdkcentral.com:9003/api/v2/device/mac:dca6320eb8bb/config?names=Device.DeviceInfo.SoftwareVersion' -H 'Authorization:Basic d3B1c2VyOndlYnBhQDEyMzQ1Njc4OTAK'
    override suspend fun <T> getDeviceProperty(
        deviceMacAddress: String,
        deviceProperty: RouterDevice.Get<T>,
        typeInfo: TypeInfo,
    ): T? {
        return httpClient.get {
            url("/api/v2/device/mac:$deviceMacAddress/config")
            contentType(ContentType.Application.Json)
            parameter("names", deviceProperty.name)
        }.body(typeInfo)
    }

    // curl -X PATCH http://webpa.rdkcentral.com:9003/api/v2/device/mac:4e07b781a3b8/config -d '{"parameters": [ {"dataType": 0, "name": "Device.WiFi.SSID.10001.SSID", "value": "Filogic_5G2"}]}' -H 'Authorization:Basic d3B1c2VyOndlYnBhQDEyMzQ1Njc4OTAK'
    // {"parameters":[{"name":"Device.WiFi.SSID.10001.SSID","message":"Success"}],"statusCode":200}%
    override suspend fun <T> setDeviceProperty(
        deviceMacAddress: String,
        deviceProperty: RouterDevice.Set<T>,
        typeInfo: TypeInfo,
        value: T,
    ): Boolean {
        return httpClient.patch {
            url("/api/v2/device/mac:$deviceMacAddress/config")
            setBody("{\"parameters\": [ {\"dataType\": 0, \"name\": \"${deviceProperty.name}\", \"value\": \"$value\"}]}")
        }.body(typeInfo)
    }

    override suspend fun doDeviceAction(
        deviceMacAddress: String,
        deviceAction: RouterDevice.Action
    ): Boolean {
        TODO("Not yet implemented")
    }
}

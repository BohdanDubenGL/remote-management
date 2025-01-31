package com.globallogic.rdkb.remotemanagement.data.network.service.impl

import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralApiService
import com.globallogic.rdkb.remotemanagement.data.network.service.RouterDeviceProperty
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

class RdkCentralApiServiceImpl(
    private val httpClient: HttpClient
) : RdkCentralApiService {

    // curl -X GET 'http://webpa.rdkcentral.com:9003/api/v2/device/mac:dca6320eb8bb/config?names=Device.DeviceInfo.SoftwareVersion' -H 'Authorization:Basic d3B1c2VyOndlYnBhQDEyMzQ1Njc4OTAK'
    override suspend fun getRouterDeviceProperty(
        routerDeviceMacAddress: String,
        routerDeviceProperty: RouterDeviceProperty,
    ): String {
        return httpClient.get {
            url("/api/v2/device/mac:$routerDeviceMacAddress/config")
            contentType(ContentType.Application.Json)
            parameter("names", routerDeviceProperty.name)
        }.body<String>()
    }
}

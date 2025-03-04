package com.globallogic.rdkb.remotemanagement.data.network.service.impl

import com.globallogic.rdkb.remotemanagement.data.network.safePost
import com.globallogic.rdkb.remotemanagement.data.network.service.NetworkUpnpDeviceDataService
import com.globallogic.rdkb.remotemanagement.data.network.service.model.upnp.response.Envelope
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

class NetworkUpnpDeviceDataServiceImpl(
    private val httpClient: HttpClient,
) : NetworkUpnpDeviceDataService {
    override suspend fun getMacAddress(ip: String, port: Int): Resource<Envelope, ThrowableResourceError> {
        return httpClient.safePost<Envelope> {
            url(
                scheme = "http",
                host = ip,
                port = port,
                path = "/upnp/control/WANIPConnection0"
            )
            contentType(ContentType.Text.Xml)
            header("SOAPAction", "\"urn:schemas-upnp-org:service:WANIPConnection:1#GetMACAddress\"")
            setBody(getMACAddressRequestBody)
        }
    }

    companion object {
        private val getMACAddressRequestBody: String = """
            <?xml version="1.0"?>
            <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"><s:Body><u:GetMACAddress xmlns:u="urn:schemas-upnp-org:service:WANIPConnection:1"></u:GetMACAddress></s:Body></s:Envelope>
        """.trimIndent()
    }
}

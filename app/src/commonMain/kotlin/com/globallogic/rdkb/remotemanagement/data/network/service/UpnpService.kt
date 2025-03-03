package com.globallogic.rdkb.remotemanagement.data.network.service

import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

class UpnpService(
    private val httpClient: HttpClient,
) {
    suspend fun getExternalIpAddress(): String {
        return runCatching {
            httpClient.post {
                url(
                    scheme = "http",
                    host = "10.0.0.1",
                    port = 49152,
                    path = "/upnp/control/WANIPConnection0"
                )
                contentType(ContentType.Text.Xml)
                header("SOAPAction", "\"urn:schemas-upnp-org:service:WANIPConnection:1#GetExternalIPAddress\"")
                setBody("""
                <?xml version="1.0"?>
                <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"><s:Body><u:GetExternalIPAddress xmlns:u="urn:schemas-upnp-org:service:WANIPConnection:1"></u:GetExternalIPAddress></s:Body></s:Envelope>
            """.trimIndent())
            }
                .bodyAsText()
        }
            .getOrNull()
            .orEmpty()
    }
}

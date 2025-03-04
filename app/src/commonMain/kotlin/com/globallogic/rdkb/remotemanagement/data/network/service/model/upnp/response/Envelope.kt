package com.globallogic.rdkb.remotemanagement.data.network.service.model.upnp.response

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
@XmlSerialName("Envelope", namespace = "http://schemas.xmlsoap.org/soap/envelope/", prefix = "s")
data class Envelope(
    val encodingStyle: String,
    val body: Body
) {
    @Serializable
    @XmlSerialName("Body", namespace = "http://schemas.xmlsoap.org/soap/envelope/", prefix = "s")
    data class Body(
        val getMACAddressResponse: GetMACAddressResponse
    ) {
        @Serializable
        @XmlSerialName("GetMACAddressResponse", namespace = "urn:schemas-upnp-org:service:WANIPConnection:1", prefix = "u")
        data class GetMACAddressResponse(
            val newMACAddress: NewMACAddress
        ) {
            @Serializable
            @XmlSerialName("NewMACAddress", namespace = "", prefix = "")
            data class NewMACAddress(
                @XmlValue val macAddress: String,
            )
        }
    }
}

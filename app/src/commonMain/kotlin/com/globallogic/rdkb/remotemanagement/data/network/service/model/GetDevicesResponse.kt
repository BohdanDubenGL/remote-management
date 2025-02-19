package com.globallogic.rdkb.remotemanagement.data.network.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetDevicesResponse(
    @SerialName("devices") val devices: List<Device?>? = null
) {
    @Serializable
    data class Device(
        @SerialName("id") val id: String? = null,
        @SerialName("pending") val pending: Int? = null,
        @SerialName("statistics") val statistics: Statistics? = null
    )

    @Serializable
    data class Statistics(
        @SerialName("bytesReceived") val bytesReceived: Int? = null,
        @SerialName("bytesSent") val bytesSent: Int? = null,
        @SerialName("connectedAt") val connectedAt: String? = null,
        @SerialName("duplications") val duplications: Int? = null,
        @SerialName("messagesReceived") val messagesReceived: Int? = null,
        @SerialName("messagesSent") val messagesSent: Int? = null,
        @SerialName("upTime") val upTime: String? = null
    )
}

package com.globallogic.rdkb.remotemanagement.data.network.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetPropertyResponse(
    @SerialName("parameters") val parameters: List<Parameter?>? = null,
    @SerialName("statusCode") val statusCode: Int? = null,
    @SerialName("code") val code: Int? = null,
    @SerialName("message") val message: String? = null,
) {
    @Serializable
    data class Parameter(
        @SerialName("dataType") val dataType: Int? = null,
        @SerialName("message") val message: String? = null,
        @SerialName("name") val name: String? = null,
        @SerialName("parameterCount") val parameterCount: Int? = null,
        @SerialName("value") val value: String? = null,
    )
}

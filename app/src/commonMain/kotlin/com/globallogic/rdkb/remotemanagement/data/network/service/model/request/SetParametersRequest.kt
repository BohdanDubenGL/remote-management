package com.globallogic.rdkb.remotemanagement.data.network.service.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParametersRequest(
    @SerialName("parameters") val parameters: List<Parameter>,
) {
    @Serializable
    data class Parameter(
        @SerialName("dataType") val dataType: Int,
        @SerialName("name") val name: String,
        @SerialName("value") val value: String,
    )
}

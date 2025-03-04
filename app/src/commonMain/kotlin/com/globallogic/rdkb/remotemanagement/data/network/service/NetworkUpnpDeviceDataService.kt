package com.globallogic.rdkb.remotemanagement.data.network.service

import com.globallogic.rdkb.remotemanagement.data.network.service.model.upnp.response.Envelope
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError

interface NetworkUpnpDeviceDataService {
    suspend fun getMacAddress(
        ip: String,
        port: Int
    ): Resource<Envelope, ThrowableResourceError>
}

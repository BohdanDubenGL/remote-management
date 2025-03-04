package com.globallogic.rdkb.remotemanagement.data.upnp.impl

import com.globallogic.rdkb.remotemanagement.data.upnp.UpnpService
import com.globallogic.rdkb.remotemanagement.data.upnp.model.UpnpDevice

// todo: not implemented yet
class UpnpServiceImpl : UpnpService {
    override suspend fun getDevices(): List<UpnpDevice> = emptyList()

    override suspend fun getDeviceMac(device: UpnpDevice): String = ""
}

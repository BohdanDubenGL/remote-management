package com.globallogic.rdkb.remotemanagement.data.upnp

import com.globallogic.rdkb.remotemanagement.data.upnp.model.UpnpDevice

interface UpnpService {
    suspend fun getDevices(): List<UpnpDevice>

    suspend fun getDeviceMac(device: UpnpDevice): String
}

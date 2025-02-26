package com.globallogic.rdkb.remotemanagement.data.network.service.impl

import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralAccessorService
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralPropertyService
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError

class RdkCentralAccessorServiceImpl(
    private val rdkCentralPropertyService: RdkCentralPropertyService,
): RdkCentralAccessorService {
    override suspend fun getAvailableDevices(): Resource<List<String>, ThrowableResourceError> {
        return rdkCentralPropertyService.getAvailableDevices()
    }

    override fun device(macAddress: String): RdkCentralAccessorService.DeviceAccessor =
        DeviceAccessor(rdkCentralPropertyService, macAddress)
}

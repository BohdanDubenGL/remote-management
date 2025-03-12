package com.globallogic.rdkb.remotemanagement.data.network.service.impl

import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralAccessorService
import com.globallogic.rdkb.remotemanagement.data.network.service.RdkCentralPropertyService
import com.globallogic.rdkb.remotemanagement.domain.entity.Band

class AccessPointGroupAccessor(
    private val rdkCentralPropertyService: RdkCentralPropertyService,
    private val macAddress: String,
    override val accessPointGroupId: Int,
) : RdkCentralAccessorService.AccessPointGroupAccessor {

    override fun accessPoints(): List<RdkCentralAccessorService.AccessPointAccessor> =
        Band.entries.map { band -> accessPoint(band) }

    override fun accessPoint(band: Band): RdkCentralAccessorService.AccessPointAccessor {
        return AccessPointAccessor(
            rdkCentralPropertyService = rdkCentralPropertyService,
            macAddress = macAddress,
            accessPointId = accessPointGroupId + band.radio,
            band = band,
        )
    }
}

package com.globallogic.rdkb.remotemanagement.data.network.service.model

data class DeviceParameter<T: Any>(
    val property: DeviceProperty.Property<T>,
    val value: T,
) {
    fun valueToString() = property.dataType.toMapper(value)
}

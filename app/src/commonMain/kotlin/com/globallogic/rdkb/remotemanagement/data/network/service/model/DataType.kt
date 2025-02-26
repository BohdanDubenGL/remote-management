package com.globallogic.rdkb.remotemanagement.data.network.service.model

sealed class DataType<T: Any>(
    val value: Int,
    val fromMapper: (String) -> T?,
    val toMapper: (T) -> String = Any::toString
) {
    data object Text : DataType<String>(0, { it })
    data object Integer : DataType<Int>(1, String::toIntOrNull)
    data object LongInt : DataType<Long>(2, String::toLongOrNull)
    data object Bool : DataType<Boolean>(3, String::toBooleanStrictOrNull)
    data object DateTime : DataType<String>(4, { it })
    data object Zeros : DataType<String>(5, { it })
    data object UInteger : DataType<UInt>(7, String::toUIntOrNull)
    data object PropertyArray : DataType<String>(11, { it })
}

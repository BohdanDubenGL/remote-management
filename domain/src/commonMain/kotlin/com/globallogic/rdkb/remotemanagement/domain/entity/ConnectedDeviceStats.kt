package com.globallogic.rdkb.remotemanagement.domain.entity

data class ConnectedDeviceStats(
    val bytesSent: Long,
    val bytesReceived: Long,
    val packetsSent: Long,
    val packetsReceived: Long,
    val errorsSent: Long,
) {
    fun hasData(): Boolean =
        arrayOf(bytesSent, bytesReceived, packetsSent, packetsReceived, errorsSent)
            .none { it == 0L }
}

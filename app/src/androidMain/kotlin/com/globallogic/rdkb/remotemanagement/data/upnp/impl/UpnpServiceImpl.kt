package com.globallogic.rdkb.remotemanagement.data.upnp.impl

import com.globallogic.rdkb.remotemanagement.data.network.service.NetworkUpnpDeviceDataService
import com.globallogic.rdkb.remotemanagement.data.upnp.UpnpService
import com.globallogic.rdkb.remotemanagement.data.upnp.model.UpnpDevice
import com.globallogic.rdkb.remotemanagement.domain.utils.dataOrElse
import com.globallogic.rdkb.remotemanagement.domain.utils.map
import io.ktor.http.Url
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.MulticastSocket
import java.nio.charset.Charset
import kotlin.time.Duration.Companion.seconds

class UpnpServiceImpl(
    private val networkUpnpDeviceDataService: NetworkUpnpDeviceDataService,
    private val loadingTimeoutMillis: Long = 5.seconds.inWholeMilliseconds,
) : UpnpService {
    override suspend fun getDevices(): List<UpnpDevice> = coroutineScope {
        var unicastSocket: DatagramSocket? = null
        var multicastSocket: MulticastSocket? = null
        try {
            unicastSocket = DatagramSocket(0)
            multicastSocket = MulticastSocket(ssdpPort)
                .apply { joinGroup(InetAddress.getByName(ssdpIpAddress)) }

            val multicastPackets = async { multicastSocket.collectPackets() }
            val unicastPackets = async { unicastSocket.collectPackets() }

            unicastSocket.sendPacket(createMSearchRequest())

            delay(loadingTimeoutMillis)
            unicastSocket.close()
            multicastSocket.close()

            val upnpDevices = listOf(multicastPackets, unicastPackets)
                .awaitAll()
                .flatten()
                .map(DatagramPacket::toUpnpDevice)
                .distinctBy { it.ip }

            return@coroutineScope upnpDevices
        } catch (e: Exception) {
            return@coroutineScope emptyList()
        } finally {
            unicastSocket?.close()
            multicastSocket?.close()
        }
    }

    override suspend fun getDeviceMac(device: UpnpDevice): String {
        val locationUrl = Url(device.location)
        return networkUpnpDeviceDataService.getMacAddress(locationUrl.host, locationUrl.port)
            .map { it.body.getMACAddressResponse.newMACAddress.macAddress }
            .dataOrElse { error -> "" }
    }

    private fun createMSearchRequest(): DatagramPacket {
        val searchData = mSearchRequest.toByteArray(Charset.defaultCharset())
        return DatagramPacket(
            searchData,
            searchData.size,
            InetAddress.getByName(ssdpIpAddress),
            ssdpPort,
        )
    }

    companion object {
        private const val ssdpIpAddress: String = "239.255.255.250"
        private const val ssdpPort: Int = 1900

        // Android Studio shows error, but this code works ok
        private val mSearchRequest: String = buildString {
            append("M-SEARCH * HTTP/1.1\r\n")
            append("HOST: $ssdpIpAddress:$ssdpPort\r\n")
            append("MAN: \"ssdp:discover\"\r\n")
            append("MX: 3\r\n")
            append("ST: ssdp:rootdevice\r\n")
            append("\r\n")
        }
    }
}

private fun DatagramPacket.toUpnpDevice(): UpnpDevice {
    val body = String(data, 0, length, Charset.defaultCharset())
    return UpnpDevice(
        ip = address.hostName,
        port = port,
        location = body.split("\r\n")
            .firstOrNull { it.startsWith("LOCATION:", ignoreCase = true) }
            ?.removePrefix("LOCATION:")
            ?.trim()
            .orEmpty()
    )
}

private suspend fun DatagramSocket.collectPackets(bufferSize: Int = 4096): List<DatagramPacket> = buildList {
    withContext(Dispatchers.IO) {
        val buffer = ByteArray(bufferSize)
        while (isActive && !isClosed) {
            try {
                val responsePacket = DatagramPacket(buffer, buffer.size)
                receive(responsePacket)

                add(responsePacket)
            } catch (e: Exception) {
                break
            }
        }
    }
}

private suspend fun DatagramSocket.sendPacket(datagram: DatagramPacket) = withContext(Dispatchers.IO) {
    send(datagram)
}

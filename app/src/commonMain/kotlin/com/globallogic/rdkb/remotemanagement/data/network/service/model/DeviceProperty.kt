package com.globallogic.rdkb.remotemanagement.data.network.service.model

sealed interface DeviceProperty<T: Any> {
    val name: String
    val dataType: DataType<T>

    abstract class Namespace(override val name: String) : DeviceProperty<String> {
        override val dataType: DataType<String> = DataType.PropertyArray
    }
    abstract class Property<T: Any>(override val name: String, override val dataType: DataType<T>) : DeviceProperty<T>
    abstract class StringProperty(name: String) : Property<String>(name, DataType.Text)
    abstract class IntProperty(name: String) : Property<Int>(name, DataType.Integer)
    abstract class LongProperty(name: String) : Property<Long>(name, DataType.LongInt)
    abstract class BooleanProperty(name: String) : Property<Boolean>(name, DataType.Bool)
    abstract class DataTimeProperty(name: String) : Property<String>(name, DataType.DateTime)
    abstract class Action(override val name: String, val value: String): DeviceProperty<String> {
        override val dataType: DataType<String> = DataType.Text
    }

    data object ModelName : StringProperty(name = "Device.DeviceInfo.ModelName")
    data object Manufacturer : StringProperty(name = "Device.DeviceInfo.Manufacturer")
    data object SoftwareVersion : StringProperty(name = "Device.DeviceInfo.SoftwareVersion")
    data object IpAddressV4 : StringProperty(name = "Device.DeviceInfo.X_COMCAST-COM_WAN_IP")
    data object IpAddressV6 : StringProperty(name = "Device.DeviceInfo.X_COMCAST-COM_WAN_IPv6")
    data object MacAddress : StringProperty(name = "Device.DeviceInfo.X_COMCAST-COM_WAN_MAC")
    data object SerialNumber : StringProperty(name = "Device.DeviceInfo.SerialNumber")
    data class OperatingFrequencyBand(val radio: Int) : StringProperty(name = "Device.WiFi.Radio.$radio.OperatingFrequencyBand") // 10000, 10100, 10200
    data class PreSharedKey(val accessPoint: Int) : StringProperty(name = "Device.WiFi.AccessPoint.$accessPoint.Security.PreSharedKey")

    data object TotalMemory : LongProperty(name = "Device.DeviceInfo.MemoryStatus.Total")
    data object FreeMemory : LongProperty(name = "Device.DeviceInfo.MemoryStatus.Total")

    data object ConnectedDeviceCount : LongProperty(name = "Device.Hosts.HostNumberOfEntries")

    data class ConnectedDeviceActive(val host: Int) : BooleanProperty(name = "Device.Hosts.Host.$host.Active")
    data class ConnectedDeviceHostName(val host: Int) : StringProperty(name = "Device.Hosts.Host.$host.HostName")
    data class ConnectedDeviceMacAddress(val host: Int) : StringProperty(name = "Device.Hosts.Host.$host.PhysAddress")
    data class ConnectedDeviceIpAddress(val host: Int) : StringProperty(name = "Device.Hosts.Host.$host.IPAddress")
    data class ConnectedDeviceVendorClassId(val host: Int) : StringProperty(name = "Device.Hosts.Host.$host.VendorClassID")
    data class ConnectedDeviceLayer1Interface(val host: Int) : StringProperty(name = "Device.Hosts.Host.$host.Layer1Interface")
    data class ConnectedDeviceAssociatedDevice(val host: Int) : StringProperty(name = "Device.Hosts.Host.$host.AssociatedDevice")

    data class WifiName(val accessPoint: Int) : StringProperty(name = "Device.WiFi.SSID.$accessPoint.Name")
    data class WifiEnabled(val accessPoint: Int) : BooleanProperty(name = "Device.WiFi.SSID.$accessPoint.Enable")
    data class WifiSsid(val accessPoint: Int) : StringProperty(name = "Device.WiFi.SSID.$accessPoint.SSID")
    data class WifiPassword(val accessPoint: Int) : StringProperty(name = "Device.WiFi.AccessPoint.$accessPoint.Security.KeyPassphrase")
    data class WifiSecurityMode(val accessPoint: Int) : StringProperty(name = "Device.WiFi.AccessPoint.$accessPoint.Security.ModeEnabled")
    data class WifiAvailableSecurityModes(val accessPoint: Int) : StringProperty(name = "Device.WiFi.AccessPoint.$accessPoint.Security.ModesSupported")
    data class WifiClientsCount(val accessPoint: Int) : LongProperty(name = "Device.WiFi.AccessPoint.$accessPoint.AssociatedDeviceNumberOfEntries")

    data class WifiClientsMacAddress(val accessPoint: Int, val deviceId: Int) : StringProperty(name = "Device.WiFi.AccessPoint.$accessPoint.AssociatedDevice.$deviceId.MACAddress")
    data class WifiClientsActive(val accessPoint: Int, val deviceId: Int) : BooleanProperty(name = "Device.WiFi.AccessPoint.$accessPoint.AssociatedDevice.$deviceId.Active")

    data class WifiClientBytesSent(val accessPoint: Int, val clientId: Int) : LongProperty(name = "Device.WiFi.AccessPoint.$accessPoint.AssociatedDevice.$clientId.Stats.BytesSent")
    data class WifiClientBytesReceived(val accessPoint: Int, val clientId: Int) : LongProperty(name = "Device.WiFi.AccessPoint.$accessPoint.AssociatedDevice.$clientId.Stats.BytesReceived")
    data class WifiClientPacketsSent(val accessPoint: Int, val clientId: Int) : LongProperty(name = "Device.WiFi.AccessPoint.$accessPoint.AssociatedDevice.$clientId.Stats.PacketsSent")
    data class WifiClientPacketsReceived(val accessPoint: Int, val clientId: Int) : LongProperty(name = "Device.WiFi.AccessPoint.$accessPoint.AssociatedDevice.$clientId.Stats.PacketsReceived")
    data class WifiClientErrorsSent(val accessPoint: Int, val clientId: Int) : LongProperty(name = "Device.WiFi.AccessPoint.$accessPoint.AssociatedDevice.$clientId.Stats.ErrorsSent")

    data object MotionDetectionDeviceMacAddress : StringProperty(name = "Device.WiFi.X_RDK_MotionDetection.SensingDeviceMACAddress")
    data object MotionDetectionNumberOfEntries : LongProperty(name = "Device.WiFi.X_RDK_MotionDetection.SensingEventsNumberOfEntries")
    data object MotionDetectionPercent : LongProperty(name = "Device.WiFi.X_RDK_MotionDetection.Motion")
    data class MotionDetectionSensingDevice(val eventId: Int) : StringProperty(name = "Device.WiFi.X_RDK_MotionDetection.SensingEvents.$eventId.SensingDevice")
    data class MotionDetectionType(val eventId: Int) : StringProperty(name = "Device.WiFi.X_RDK_MotionDetection.SensingEvents.$eventId.Type")
    data class MotionDetectionTime(val eventId: Int) : DataTimeProperty(name = "Device.WiFi.X_RDK_MotionDetection.SensingEvents.$eventId.Time")

    data object ActionReboot : Action(name = "Device.X_CISCO_COM_DeviceControl.RebootDevice", "Device")
    data object ActionFactoryReset : Action(name = "Device.X_CISCO_COM_DeviceControl.FactoryReset", "Router,Wifi,Firewall,VoIP,Docsis")
}

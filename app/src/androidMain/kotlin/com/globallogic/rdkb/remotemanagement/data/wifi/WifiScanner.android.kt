package com.globallogic.rdkb.remotemanagement.data.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import androidx.core.content.getSystemService
import com.globallogic.rdkb.remotemanagement.data.permission.Permission
import com.globallogic.rdkb.remotemanagement.data.permission.checkPermission
import com.globallogic.rdkb.remotemanagement.data.wifi.model.WifiInfo
import com.globallogic.rdkb.remotemanagement.domain.utils.runCatchingSafe
import kotlinx.coroutines.CompletableDeferred

actual class WifiScanner(
    private val applicationContext: Context,
) {
    private val wifiManager: WifiManager? = applicationContext.getSystemService<WifiManager>()

    actual suspend fun getCurrentWifi(): WifiInfo? {
        val wifiManager = wifiManager ?: return null
        val connectionInfo = wifiManager.connectionInfo
//        return WifiInfo(connectionInfo.ssid, connectionInfo.bssid)
        return WifiInfo(ssid = "", bssid = "dc:a6:32:0e:b8:bb") // fake
    }

    actual suspend fun scanWifi(): List<WifiInfo> {
        val wifiInfosReferred = CompletableDeferred<List<WifiInfo>>()

        val wifiScanReceiver = wifiScanReceiver(wifiInfosReferred)
        val intentFilter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        runCatchingSafe { applicationContext.registerReceiver(wifiScanReceiver, intentFilter) }
            .onFailure { if (wifiInfosReferred.isActive) wifiInfosReferred.complete(emptyList()) }

        return wifiInfosReferred.await()
    }

    private fun wifiScanReceiver(wifiInfosReferred: CompletableDeferred<List<WifiInfo>>): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                val result = when {
                    !applicationContext.checkPermission(Permission.Location) -> emptyList()
                    !success -> emptyList()
                    wifiManager == null -> emptyList()
                    else -> wifiManager.scanResults
                        .map { result -> WifiInfo(result.SSID, result.BSSID) }
                }
                applicationContext.unregisterReceiver(this)
                if (wifiInfosReferred.isActive) wifiInfosReferred.complete(result)
            }
        }
    }
}

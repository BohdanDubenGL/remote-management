package com.globallogic.rdkb.remotemanagement.data.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.globallogic.rdkb.remotemanagement.domain.utils.runCatchingSafe
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.minutes

actual class PermissionController(
    private val activity: ComponentActivity
) {
    private var resultDeferred: CompletableDeferred<RequestResult>? = null

    private val requestPermissionLauncher = activity.registerForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        callback = { isGranted ->
            val result = when {
                isGranted -> RequestResult.Granted
                else -> RequestResult.Denied
            }
            val resultDeferred = resultDeferred ?: error("resultDeferred = null")
            if (resultDeferred.isActive) resultDeferred.complete(result)
        }
    )

    actual fun checkPermission(permission: Permission): RequestResult {
        return when {
            activity.checkPermission(permission) -> RequestResult.Granted
            else -> RequestResult.Denied
        }
    }

    actual suspend fun requestPermission(permission: Permission): RequestResult {
        return when {
            checkPermission(permission) == RequestResult.Granted -> RequestResult.Granted
            activity.shouldShowRequestPermissionRationale(permission.nativePermission) -> RequestResult.Rationale
            else -> requestNativePermissionAsync(permission)
        }
    }

    private suspend fun requestNativePermissionAsync(permission: Permission): RequestResult {
        return withTimeoutOrNull(1.minutes.inWholeMilliseconds) {
            val result = CompletableDeferred<RequestResult>()
            resultDeferred = result
            runCatchingSafe { requestPermissionLauncher.launch(permission.nativePermission) }
                .onFailure { result.complete(RequestResult.Denied) }
            return@withTimeoutOrNull result.await()
        } ?: RequestResult.Denied
    }
}

fun Context.checkPermission(permission: Permission): Boolean = ContextCompat
    .checkSelfPermission(this, permission.nativePermission) == PackageManager.PERMISSION_GRANTED

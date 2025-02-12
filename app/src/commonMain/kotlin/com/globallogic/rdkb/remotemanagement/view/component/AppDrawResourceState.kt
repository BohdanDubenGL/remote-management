package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.runtime.Composable
import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceError
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceState
import com.globallogic.rdkb.remotemanagement.view.error.UiResourceError
import kotlin.jvm.JvmName

@Composable
fun <Data, Error: ResourceError> AppDrawResourceState(
    resourceState: ResourceState<Data, Error>,
    onNone: @Composable () -> Unit = { },
    onCancelled: @Composable () -> Unit = { },
    onLoading: @Composable () -> Unit = { AppLoading() },
    onFailure: @Composable (Error) -> Unit = { },
    onSuccess: @Composable (Data) -> Unit = { },
) {
    when (resourceState) {
        is ResourceState.None -> onNone()
        is ResourceState.Cancelled -> onCancelled()
        is ResourceState.Loading -> onLoading()
        is Resource.Failure -> onFailure(resourceState.error)
        is Resource.Success -> onSuccess(resourceState.data)
    }
}

@JvmName("AppDrawResourceStateWithUiResourceError")
@Composable
fun <Data> AppDrawResourceState(
    resourceState: ResourceState<Data, UiResourceError>,
    onNone: @Composable () -> Unit = { },
    onCancelled: @Composable () -> Unit = { },
    onLoading: @Composable () -> Unit = { AppLoading() },
    onFailure: @Composable (UiResourceError) -> Unit = { error -> AppError(error = error) },
    onSuccess: @Composable (Data) -> Unit = { },
) {
    AppDrawResourceState<Data, UiResourceError>(
        resourceState = resourceState,
        onNone = onNone,
        onCancelled = onCancelled,
        onLoading = onLoading,
        onFailure = onFailure,
        onSuccess = onSuccess
    )
}

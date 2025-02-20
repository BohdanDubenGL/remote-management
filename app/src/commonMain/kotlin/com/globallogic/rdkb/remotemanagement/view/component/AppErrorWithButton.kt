package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.globallogic.rdkb.remotemanagement.view.error.UiResourceError

@Composable
fun AppErrorWithButton(
    modifier: Modifier = Modifier,
    errorMessage: String = "",
    errorDescription: String = "",
    errorIcon: ImageVector = Icons.Default.Error,
    buttonText: String = "",
    onClick: () -> Unit = { },
) {
    AppError(
        errorMessage = errorMessage,
        errorDescription = errorDescription,
        errorIcon = errorIcon,
        content = {
            AppButton(
                text = buttonText,
                onClick = onClick
            )
        },
        modifier = modifier,
    )
}

@Composable
fun AppErrorWithButton(
    modifier: Modifier = Modifier,
    error: UiResourceError,
    buttonText: String = "",
    onClick: () -> Unit = { },
) {
    AppErrorWithButton(
        modifier = modifier,
        errorMessage = error.errorMessage,
        errorDescription = error.errorDescription,
        errorIcon = error.errorIcon,
        buttonText = buttonText,
        onClick =  onClick
    )
}

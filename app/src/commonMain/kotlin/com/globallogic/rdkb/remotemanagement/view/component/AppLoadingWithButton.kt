package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppLoadingWithButton(
    modifier: Modifier = Modifier,
    buttonText: String = "",
    onClick: () -> Unit = {},
) {
    AppLoading(
        content = {
            AppButton(text = buttonText, onClick = onClick)
        },
        modifier = modifier,
    )
}

package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppLoading(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    AppLayoutVerticalSections(
        topSection = {
            LoadingSpinner(
                modifier = Modifier.size(256.dp)
            )
        },
        bottomSection = content,
        modifier = modifier.padding(vertical = 16.dp),
    )
}

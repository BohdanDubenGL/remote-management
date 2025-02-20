package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AppTextProperty(
    modifier: Modifier = Modifier,
    name: String = "",
    value: String = "",
) {
    Row(
        modifier = modifier
    ) {
        Text(text = name, fontWeight = FontWeight.Bold)
        Text(text = value)
    }
}

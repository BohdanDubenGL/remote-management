package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globallogic.rdkb.remotemanagement.view.theme.UbuntuMono

@Composable
fun AppTitleText(
    modifier: Modifier = Modifier,
    text: String = "",
    fontSize: TextUnit = 24.sp,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        fontFamily = FontFamily.UbuntuMono,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppTitleTextWithIcon(
    modifier: Modifier = Modifier,
    text: String = "",
    imageVector: ImageVector,
    iconColor: Color = MaterialTheme.colorScheme.tertiary,
    textColor: Color = MaterialTheme.colorScheme.tertiary,
    fontSize: TextUnit = 24.sp,
    iconSize: Dp = 32.dp,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
        modifier = modifier.padding(top = 8.dp, bottom = 2.dp),
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = text,
            tint = iconColor,
            modifier = Modifier.size(iconSize),
        )
        AppTitleText(
            text = text,
            color = textColor,
            fontSize = fontSize
        )
    }
}

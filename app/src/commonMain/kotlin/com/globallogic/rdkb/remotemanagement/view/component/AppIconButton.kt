package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun AppIconButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String = "",
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
) {
    IconButton(
        content = {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                tint = contentColor,
            )
        },
        onClick = onClick,
        modifier = modifier
            .size(56.dp)
            .background(contentColor.copy(alpha = 0.25F), shape = RoundedCornerShape(32.dp))
            .padding(1.dp)
            .background(color, shape = RoundedCornerShape(32.dp)),
    )
}

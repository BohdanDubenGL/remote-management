package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    text: String = "",
    fontSize: TextUnit = 16.sp,
    fontWeight: FontWeight = FontWeight.Bold,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    minHeight: Dp = 56.dp,
    minWidth: Dp = 200.dp,
    cornerRadius: Dp = 24.dp,
    enabled: Boolean = true,
    onClick: () -> Unit = { },
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
            .background(contentColor.copy(alpha = 0.25F), shape = RoundedCornerShape(cornerRadius))
            .padding(1.dp),
        shape = RoundedCornerShape(cornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        elevation = ButtonDefaults.buttonElevation(16.dp),
        enabled = enabled,
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = fontWeight,
        )
    }
}

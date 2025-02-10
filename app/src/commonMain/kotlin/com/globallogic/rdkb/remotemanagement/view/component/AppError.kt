package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globallogic.rdkb.remotemanagement.view.theme.RobotoMono
import com.globallogic.rdkb.remotemanagement.view.theme.UbuntuMono

@Composable
fun AppError(
    modifier: Modifier = Modifier,
    errorMessage: String = "",
    errorDescription: String = "",
    errorIcon: ImageVector = Icons.Default.Error,
    content: @Composable () -> Unit = { },
) {
    AppLayoutVerticalSections(
        topSection = {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
                ) {
                    Icon(
                        imageVector = errorIcon,
                        contentDescription = "Error icon",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 24.sp,
                        fontFamily = FontFamily.RobotoMono,
                    )
                }
                Text(
                    text = errorDescription,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.UbuntuMono,
                )
            }
        },
        bottomSection = content,
        modifier = modifier,
    )
}

package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
fun AppDefaultError(
    errorMessage: String = "",
    errorDescription: String = "",
    errorIcon: ImageVector = Icons.Default.Error,
    buttonText: String = "",
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppError(
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                modifier = Modifier.fillMaxSize().padding(bottom = 256.dp, start = 32.dp, end = 32.dp)
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
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier.fillMaxSize().padding(vertical = 192.dp)
            ) {
                AppButton(
                    text = buttonText,
                    onClick = onClick
                )
            }
        },
        modifier = modifier,
    )
}
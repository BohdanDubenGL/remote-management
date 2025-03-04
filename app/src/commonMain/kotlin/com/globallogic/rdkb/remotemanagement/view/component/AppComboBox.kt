package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T: Any> AppComboBox(
    modifier: Modifier = Modifier,
    label: String,
    options: List<T>,
    selectedOption: T,
    optionMapper: (T) -> String = Any::toString,
    onSelected: (T) -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.onPrimary,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    cornerRadius: Dp = 24.dp,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }

    val backgroundColor = when {
        enabled -> containerColor
        else -> MaterialTheme.colorScheme.surface
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = enabled && it },
    ) {
        Button(
            onClick = { },
            shape = RoundedCornerShape(cornerRadius),
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = contentColor,
            ),
            elevation = ButtonDefaults.buttonElevation(16.dp),
            modifier = modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .background(contentColor.copy(alpha = 0.5F), shape = RoundedCornerShape(cornerRadius))
                .padding(2.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = optionMapper(selectedOption),
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Normal,
                    )
                }
                Spacer(modifier = Modifier.weight(1F))
                AnimatedVisibility(enabled) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = optionMapper(option)) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = MaterialTheme.colorScheme.onSurface,
                    )
                )
            }
        }
    }
}

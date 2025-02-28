package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun AppCheckBox(
    modifier: Modifier = Modifier,
    text: String = "",
    fontSize: TextUnit = 16.sp,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.tertiary,
                uncheckedColor = MaterialTheme.colorScheme.primary,
            )
        )
        AppTitleText(text = text, fontSize = fontSize, color = MaterialTheme.colorScheme.onSurface)
    }
}

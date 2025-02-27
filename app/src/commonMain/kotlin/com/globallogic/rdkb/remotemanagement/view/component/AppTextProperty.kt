package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppTextProperty(
    modifier: Modifier = Modifier,
    name: String = "",
    value: Any = "",
    vertical: Boolean = false,
) {
    if (value.toString().isBlank()) return
    val content = movableContentOf {
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
        )
        Text(
            text = value.toString(),
            maxLines = if (vertical) 20 else 1,
            overflow = if (vertical) TextOverflow.Clip else TextOverflow.Ellipsis,
            color = Color.White,
        )
    }
    if (vertical) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start,
            content = { content() },
        )
    } else {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
            content = { content() }
        )
    }
}

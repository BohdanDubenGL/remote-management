package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun <T: Any> AppHorizontalPivots(
    modifier: Modifier = Modifier,
    pivots: List<T>,
    selectedPivot: T,
    pivotMapper: (T) -> String = Any::toString,
    onPivotClick: (T) -> Unit,
    selectedContentColor: Color = MaterialTheme.colorScheme.primaryContainer,
    selectedContainerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
) {
    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        modifier = modifier.fillMaxWidth()
    ) {
        item { Spacer(modifier = Modifier.width(16.dp)) }
        items(pivots) { pivot ->
            val selected = selectedPivot == pivot
            AppButton(
                text = pivotMapper(pivot),
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                minHeight = 40.dp,
                contentColor = if (selected) selectedContentColor else contentColor,
                containerColor = if (selected) selectedContainerColor else containerColor,
                onClick = { if (!selected) onPivotClick(pivot) },
                modifier = Modifier.height(40.dp)
            )
        }
        item { Spacer(modifier = Modifier.width(16.dp)) }
    }
}
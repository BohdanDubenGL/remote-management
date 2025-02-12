package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String = "",
    placeholder: String = "",
    isError: Boolean = false,
    errorMessage: String = "",
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit,
) {
    val borderColor = when {
        isError -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5F)
    }
    val backgroundColor = when {
        enabled -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.surface
    }
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .background(borderColor, shape = RoundedCornerShape(12.dp))
            .padding(2.dp)
            .background(color = backgroundColor, shape = RoundedCornerShape(12.dp))
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(color = MaterialTheme.colorScheme.primary, fontSize = 18.sp),
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    fontSize = 16.sp,
                )
            },
            label = {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledTextColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                cursorColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                errorTextColor = MaterialTheme.colorScheme.error,
            ),
            visualTransformation = visualTransformation,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp)
        )
        AnimatedVisibility(
            visible = isError || errorMessage.isNotBlank(),
            enter = fadeIn() + slideInVertically(),
        ) {
            Text(
                text = errorMessage,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 32.dp, bottom = 8.dp)
            )
        }
    }
}

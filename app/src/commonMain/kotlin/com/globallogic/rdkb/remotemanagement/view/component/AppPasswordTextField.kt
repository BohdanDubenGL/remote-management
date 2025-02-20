package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun AppPasswordTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String = "",
    placeholder: String = "",
    isError: Boolean = false,
    errorMessage: String = "",
    enabled: Boolean = true,
    onValueChange: (String) -> Unit,
) {
    AppTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        isError = isError,
        errorMessage = errorMessage,
        enabled = enabled,
        visualTransformation = PasswordVisualTransformation(),
        modifier = modifier
    )
}

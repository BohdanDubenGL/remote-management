package com.globallogic.rdkb.remotemanagement.view.error

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.ui.graphics.vector.ImageVector
import com.globallogic.rdkb.remotemanagement.domain.utils.ResourceError

data class UiResourceError(
    val errorMessage: String,
    val errorDescription: String,
    val errorIcon: ImageVector = Icons.Default.Error,
) : ResourceError

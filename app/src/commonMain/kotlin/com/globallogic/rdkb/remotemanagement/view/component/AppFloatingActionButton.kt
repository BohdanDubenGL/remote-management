package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import com.globallogic.rdkb.remotemanagement.view.navigation.FloatingActionButtonState
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalScaffoldController
import com.globallogic.rdkb.remotemanagement.view.navigation.ScaffoldController

@Composable
fun AppFloatingActionButton(
    scaffoldController: ScaffoldController = LocalScaffoldController.current,
) {
    val floatingActionButtonState by scaffoldController.floatingActionButtonState.collectAsStateWithLifecycle()

    when (val state = floatingActionButtonState) {
        is FloatingActionButtonState.Shown -> {
            FloatingActionButton(
                onClick = state.buttonAction,
                shape = CircleShape
            ) {
                AppIcon(
                    imageVector = state.buttonIcon,
                    contentDescription = state.iconDescription,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary,
                )
            }
        }
        is FloatingActionButtonState.Hidden -> Unit
    }
}

@Composable
fun SetupFloatingActionButton(
    floatingActionButtonState: FloatingActionButtonState = FloatingActionButtonState.Hidden
) {
    val scaffoldController = LocalScaffoldController.current
    val navController = LocalNavController.current
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val rememberedButtonAction by rememberUpdatedState(floatingActionButtonState)

    LifecycleStartEffect(currentNavBackStackEntry, rememberedButtonAction) {
        val disposable = scaffoldController.setFloatingActionButtonState(rememberedButtonAction)
        onStopOrDispose {
            disposable()
        }
    }
}

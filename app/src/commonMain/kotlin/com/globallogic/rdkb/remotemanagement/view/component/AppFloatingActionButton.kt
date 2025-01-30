package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import com.globallogic.rdkb.remotemanagement.view.FloatingActionButtonState
import com.globallogic.rdkb.remotemanagement.view.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.LocalScaffoldController
import com.globallogic.rdkb.remotemanagement.view.ScaffoldController

@Composable
fun AppFloatingActionButton(
    scaffoldController: ScaffoldController = LocalScaffoldController.current,
) {
    val floatingActionButtonState by scaffoldController.floatingActionButtonState.collectAsStateWithLifecycle()

    when (val state = floatingActionButtonState) {
        is FloatingActionButtonState.Shown -> {
            FloatingActionButton(
                onClick = state.buttonAction
            ) {
                Icon(imageVector = state.buttonIcon, contentDescription = state.iconDescription)
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

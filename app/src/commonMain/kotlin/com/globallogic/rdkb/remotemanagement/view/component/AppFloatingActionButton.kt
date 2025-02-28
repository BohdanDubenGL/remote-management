package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
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

    val state = floatingActionButtonState
    AnimatedVisibility(
        visible = state is FloatingActionButtonState.Shown,
        enter = slideInHorizontally(animationSpec = tween(200), initialOffsetX = { it }),
        exit = fadeOut(animationSpec = tween(500)) + slideOutHorizontally(animationSpec = tween(500), targetOffsetX = { it }),
    ) {
        FloatingActionButton(
            onClick = (state as? FloatingActionButtonState.Shown)?.buttonAction ?: { },
            shape = CircleShape,
            contentColor = MaterialTheme.colorScheme.secondary,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ) {
            if (state is FloatingActionButtonState.Shown) AppIcon(
                imageVector = state.buttonIcon,
                contentDescription = state.iconDescription,
                color = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
            )
        }
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

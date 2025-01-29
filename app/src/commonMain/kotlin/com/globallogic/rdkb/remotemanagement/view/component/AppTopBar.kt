package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.globallogic.rdkb.remotemanagement.view.ScaffoldController
import com.globallogic.rdkb.remotemanagement.view.Screen
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import rdkbremotemanagement.app.generated.resources.Res
import rdkbremotemanagement.app.generated.resources.title

@Composable
fun AppTopBar(navController: NavController, scaffoldController: ScaffoldController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val previousBackStackEntry by remember(currentBackStackEntry) { mutableStateOf(navController.previousBackStackEntry) }

    AppTopBar(
        titleRes = Res.string.title,
        navigateUpAction = if (previousBackStackEntry == null) {
            NavigateUpAction.Hidden
        } else {
            NavigateUpAction.Visible(onClick = navController::navigateUp)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    titleRes: StringResource,
    navigateUpAction: NavigateUpAction,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = stringResource(titleRes))
        },
        navigationIcon = {
            if (navigateUpAction is NavigateUpAction.Visible) {
                IconButton(
                    onClick = navigateUpAction.onClick,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        )
    )
}

sealed class NavigateUpAction {
    data object Hidden : NavigateUpAction()
    data class Visible(
        val onClick: () -> Unit,
    ) : NavigateUpAction()
}

package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalScaffoldController
import com.globallogic.rdkb.remotemanagement.view.navigation.ScaffoldController
import com.globallogic.rdkb.remotemanagement.view.navigation.getRouteTitle
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppTopBar(
    navController: NavController = LocalNavController.current,
    scaffoldController: ScaffoldController = LocalScaffoldController.current,
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val previousBackStackEntry by remember(currentBackStackEntry) { mutableStateOf(navController.previousBackStackEntry) }
    val routeTitle by remember(currentBackStackEntry) { mutableStateOf(getRouteTitle(currentBackStackEntry?.destination?.route)) }

    AppTopBar(
        titleRes = routeTitle,
        navigateUpAction = when (previousBackStackEntry) {
            null -> null
            else -> fun() { navController.navigateUp() }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    titleRes: StringResource?,
    navigateUpAction: (() -> Unit)? = null,
) {
    AnimatedVisibility(
        visible = titleRes != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        CenterAlignedTopAppBar(
            title = {
                if (titleRes != null) Text(text = stringResource(titleRes))
            },
            navigationIcon = {
                if (navigateUpAction != null) {
                    IconButton(
                        onClick = navigateUpAction,
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
}

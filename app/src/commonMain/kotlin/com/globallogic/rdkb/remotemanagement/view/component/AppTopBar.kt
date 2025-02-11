package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
    height: Dp,
    navController: NavController = LocalNavController.current,
    scaffoldController: ScaffoldController = LocalScaffoldController.current,
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val previousBackStackEntry by remember(currentBackStackEntry) { mutableStateOf(navController.previousBackStackEntry) }
    val routeTitle by remember(currentBackStackEntry) { mutableStateOf(getRouteTitle(currentBackStackEntry?.destination?.route)) }

    AppTopBar(
        height = height,
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
    height: Dp,
    titleRes: StringResource?,
    navigateUpAction: (() -> Unit)? = null,
) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier.fillMaxWidth().height(height)
    ) {
        AnimatedVisibility(
            visible = titleRes != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            CenterAlignedTopAppBar(
                title = {
                    if (titleRes != null) {
                        Box(
                            modifier = Modifier.fillMaxHeight(),
                            contentAlignment = Alignment.Center,
                        ) {
                            AppTitleText(text = stringResource(titleRes), color = Color.White)
                        }
                    }
                },
                navigationIcon = {
                    if (navigateUpAction != null) {
                        Box(
                            modifier = Modifier.fillMaxHeight(),
                            contentAlignment = Alignment.Center,
                        ) {
                            IconButton(
                                onClick = navigateUpAction,
                                content = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                        contentDescription = null,
                                        tint = Color.White,
                                    )
                                },
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                modifier = Modifier.height(height),
            )
        }
    }
}

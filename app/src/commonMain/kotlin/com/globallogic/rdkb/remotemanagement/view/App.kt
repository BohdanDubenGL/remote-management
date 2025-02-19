package com.globallogic.rdkb.remotemanagement.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.globallogic.rdkb.remotemanagement.data.permission.PermissionController
import com.globallogic.rdkb.remotemanagement.view.component.AppBottomNavigation
import com.globallogic.rdkb.remotemanagement.view.component.AppFloatingActionButton
import com.globallogic.rdkb.remotemanagement.view.component.AppTopBar
import com.globallogic.rdkb.remotemanagement.view.component.WaveBackground
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalScaffoldController
import com.globallogic.rdkb.remotemanagement.view.navigation.appScaffoldController
import com.globallogic.rdkb.remotemanagement.view.navigation.rememberApplicationNavGraph
import com.globallogic.rdkb.remotemanagement.view.permission.LocalPermissionController
import com.globallogic.rdkb.remotemanagement.view.theme.AppTheme
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App(
    permissionController: PermissionController? = null,
    topBarHeight: Dp = 64.dp,
) {
    AppTheme(
        dynamicColor = false,
        darkTheme = true,
    ) {
        KoinContext {
            val scaffoldController = appScaffoldController()
            val navController = rememberNavController()
            val permissionController = permissionController ?: koinInject<PermissionController>()

            CompositionLocalProvider(
                LocalNavController provides navController,
                LocalScaffoldController provides scaffoldController,
                LocalPermissionController provides permissionController,
            ) {
                val navGraph = rememberApplicationNavGraph()

                Scaffold(
                    topBar = { AppTopBar(height = topBarHeight) },
                    bottomBar = { AppBottomNavigation() },
                    floatingActionButton = { AppFloatingActionButton() },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    WaveBackground()
                    NavHost(navController, navGraph, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

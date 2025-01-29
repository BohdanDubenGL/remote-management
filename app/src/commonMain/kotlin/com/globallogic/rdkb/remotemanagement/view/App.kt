package com.globallogic.rdkb.remotemanagement.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.globallogic.rdkb.remotemanagement.view.component.AppBottomNavigation
import com.globallogic.rdkb.remotemanagement.view.component.AppFloatingActionButton
import com.globallogic.rdkb.remotemanagement.view.component.AppTopBar
import org.koin.compose.KoinContext

@Composable
fun App() {
    MaterialTheme {
        KoinContext {
            val scaffoldController = appScaffoldController()
            val navController = rememberNavController()
            val navGraph = rememberApplicationNavGraph(navController)

            Scaffold(
                topBar = { AppTopBar(navController, scaffoldController) },
                bottomBar = { AppBottomNavigation(navController, scaffoldController) },
                floatingActionButton = { AppFloatingActionButton(navController, scaffoldController) },
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                NavHost(navController, navGraph, Modifier.padding(innerPadding))
            }
        }
    }
}

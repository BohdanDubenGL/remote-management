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
import org.koin.compose.KoinContext

@Composable
fun App() {
    MaterialTheme {
        KoinContext {
            val bottomBarController = appScaffoldController()
            val navController = rememberNavController()
            val navGraph = rememberApplicationNavGraph(navController)

            Scaffold(
                bottomBar = { AppBottomNavigation(navController, bottomBarController) },
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                NavHost(navController, navGraph, Modifier.padding(innerPadding))
            }
        }
    }
}

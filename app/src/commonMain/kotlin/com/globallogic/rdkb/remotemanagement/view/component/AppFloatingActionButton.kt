package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import com.globallogic.rdkb.remotemanagement.view.ScaffoldController
import com.globallogic.rdkb.remotemanagement.view.Screen

@Composable
fun AppFloatingActionButton(navController: NavController, scaffoldController: ScaffoldController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    if (currentBackStackEntry?.destination?.hasRoute<Screen.HomeGraph.Topology>() == true) {
        FloatingActionButton(
            onClick = {  }
        ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        }
    }
}

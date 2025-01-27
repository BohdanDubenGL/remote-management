package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.globallogic.rdkb.remotemanagement.view.ScaffoldController

@Composable
fun AppBottomNavigation(navController: NavController, scaffoldController: ScaffoldController) {
    var visibility by remember { mutableStateOf(false) }
    BottomNavigation(
        modifier = Modifier.alpha(if (visibility) 1F else 0F)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val currentGraphName = remember(currentDestination, navController) { currentDestination?.parent?.route.orEmpty() }
        val routes = scaffoldController.bottomBarItemsFor(currentGraphName)
        visibility = routes.isNotEmpty()
        routes.forEach { route ->
            BottomNavigationItem(
                icon = { Icon(route.icon, contentDescription = route.name) },
                label = { Text(route.name) },
                selected = currentDestination?.hierarchy?.any { it.hasRoute(route.route::class) } == true,
                onClick = {
                    navController.navigate(route.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

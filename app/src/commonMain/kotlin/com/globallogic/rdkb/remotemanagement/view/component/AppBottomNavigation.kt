package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.globallogic.rdkb.remotemanagement.view.ScaffoldController
import com.globallogic.rdkb.remotemanagement.view.Screen

@Composable
fun AppBottomNavigation(navController: NavController, scaffoldController: ScaffoldController) {
    var visibility by remember { mutableStateOf(false) }
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.alpha(if (visibility) 1F else 0F).height(75.dp),
    ) {
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStackEntry?.destination
        val currentRouteParent = currentDestination?.route?.substringBeforeLast(".").orEmpty()
        val routes = scaffoldController.bottomBarItemsFor(currentRouteParent)

        visibility = routes.isNotEmpty()

        routes.forEach { route ->
            BottomNavigationItem(
                selected = currentDestination?.hierarchy?.any { it.hasRoute(route.route::class) } == true,
                icon = { Icon(route.icon, contentDescription = route.name) },
                label = { Text(route.name) },
                onClick = {
                    navController.navigate(route.route) {
                        popUpTo<Screen.RootGraph>()
                    }
                },
                selectedContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                unselectedContentColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        }
    }
}

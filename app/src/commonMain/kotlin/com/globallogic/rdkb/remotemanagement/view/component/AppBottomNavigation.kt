package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.globallogic.rdkb.remotemanagement.view.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.LocalScaffoldController
import com.globallogic.rdkb.remotemanagement.view.ScaffoldController
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppBottomNavigation(
    navController: NavController = LocalNavController.current,
    scaffoldController: ScaffoldController = LocalScaffoldController.current,
) {
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
                icon = { if (route.name != null) Icon(route.icon, contentDescription = stringResource(route.name)) },
                label = { if (route.name != null) Text(text = stringResource(route.name)) },
                onClick = {
                    if (currentDestination?.hierarchy?.any { it.hasRoute(route.route::class) } != true) {
                        navController.navigate(route.route) {
                            popUpTo(route.graph)
                        }
                    }
                },
                selectedContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                unselectedContentColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        }
    }
}

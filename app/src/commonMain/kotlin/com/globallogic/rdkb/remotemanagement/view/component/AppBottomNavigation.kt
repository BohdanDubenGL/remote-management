package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.globallogic.rdkb.remotemanagement.view.navigation.BottomBarRoute
import com.globallogic.rdkb.remotemanagement.view.navigation.BottomNavigationState
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalNavController
import com.globallogic.rdkb.remotemanagement.view.navigation.LocalScaffoldController
import com.globallogic.rdkb.remotemanagement.view.navigation.ScaffoldController
import com.globallogic.rdkb.remotemanagement.view.navigation.Screen
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppBottomNavigation(
    navController: NavController = LocalNavController.current,
    scaffoldController: ScaffoldController = LocalScaffoldController.current,
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination by remember(currentBackStackEntry) { mutableStateOf(currentBackStackEntry?.destination) }
    val bottomNavigationState by scaffoldController.bottomNavigationState.collectAsStateWithLifecycle()

    val routes by remember(currentDestination, bottomNavigationState) { mutableStateOf(
        when (val state = bottomNavigationState) {
            is BottomNavigationState.Hidden -> emptyList()
            is BottomNavigationState.Default -> scaffoldController.bottomBarItemsFor(currentDestination?.route?.substringBeforeLast(".").orEmpty())
            is BottomNavigationState.Shown -> state.routes
        }
    ) }
    val visibility by remember(routes) { mutableStateOf(routes.isNotEmpty()) }
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxWidth().height(80.dp),
    ) {
        AnimatedVisibility(
            visible = visibility,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
        ) {
            BottomNavigation(
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxSize(),
            ) {
                routes.forEach { route ->
                    AppBottomNavigationItem(
                        route = route,
                        selected = currentDestination?.hierarchy?.any { it.hasRoute(route.route::class) } == true,
                        onClick = {
                            navController.navigate(route.route) {
                                popUpTo(route.graph)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RowScope.AppBottomNavigationItem(
    route: BottomBarRoute<Screen>,
    selected: Boolean,
    onClick: () -> Unit,
) {
    if (route.name != null) {
        val animatedWeight by animateFloatAsState(targetValue = if (selected) 2f else 1f)
        BottomNavigationItem(
            selected = selected,
            icon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                    modifier = Modifier
                        .height(40.dp)
                        .shadow(
                            ambientColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            spotColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            elevation = if (selected) 24.dp else 0.dp,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(20.dp)
                        )
                ) {
                    Spacer(Modifier.width(8.dp))
                    val icon = if (selected) route.activeIcon else route.inactiveIcon
                    Icon(icon, contentDescription = stringResource(route.name))
                    AnimatedVisibility(
                        visible = selected,
                        enter = fadeIn() + expandVertically(),
                    ) {
                        Text(text = stringResource(route.name))
                    }
                    Spacer(Modifier.width(8.dp))
                }
            },
            onClick = {
                if (!selected) onClick()
            },
            selectedContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            unselectedContentColor = MaterialTheme.colorScheme.tertiaryContainer,
            modifier = Modifier.weight(animatedWeight),
        )
    }
}

@Composable
fun SetupBottomNavigation(
    bottomNavigationState: BottomNavigationState = BottomNavigationState.Default
) {
    val scaffoldController = LocalScaffoldController.current
    val navController = LocalNavController.current
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val rememberedState by rememberUpdatedState(bottomNavigationState)

    LifecycleStartEffect(currentNavBackStackEntry, rememberedState) {
        val disposable = scaffoldController.setBottomNavigationState(rememberedState)
        onStopOrDispose {
            disposable()
        }
    }
}

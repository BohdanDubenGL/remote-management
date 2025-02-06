package com.globallogic.rdkb.remotemanagement.view.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jetbrains.compose.resources.StringResource
import kotlin.reflect.KClass

class ScaffoldController(
    routes: Map<KClass<out Graph>, List<BottomBarRoute<Screen>>> = emptyMap()
) : ViewModel() {
    constructor(vararg routes : Pair<KClass<out Graph>, List<BottomBarRoute<Screen>>>) : this(routes.toMap())

    private val routes: Map<String, List<BottomBarRoute<Screen>>> = routes.mapKeys { it.key.qualifiedName.orEmpty() }

    fun bottomBarItemsFor(graphName: String): List<BottomBarRoute<Screen>> = routes.getOrElse(graphName, ::emptyList)


    private val _floatingActionButtonState: MutableStateFlow<FloatingActionButtonState> = MutableStateFlow(
        FloatingActionButtonState.Hidden
    )
    val floatingActionButtonState: StateFlow<FloatingActionButtonState> get() = _floatingActionButtonState.asStateFlow()

    fun setFloatingActionButtonState(state: FloatingActionButtonState): () -> Unit {
        if (_floatingActionButtonState.value != state) {
            _floatingActionButtonState.value = state
        }
        return {
            if (_floatingActionButtonState.value == state) {
                _floatingActionButtonState.value = FloatingActionButtonState.Hidden
            }
        }
    }
}

sealed interface FloatingActionButtonState {
    data object Hidden : FloatingActionButtonState
    data class Shown(
        val buttonIcon: ImageVector = Icons.Default.Search,
        val iconDescription: String = "",
        val buttonAction: () -> Unit = {},
    ) : FloatingActionButtonState
}

data class BottomBarRoute<T : Any>(
    val name: StringResource?,
    val route: T,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector,
    val graph: Graph
) {
    constructor(route: T, graph: Graph) : this(getRouteTitle(route::class.qualifiedName), route, Icons.Filled.Settings, Icons.Outlined.Settings, graph)
}

package com.globallogic.rdkb.remotemanagement.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource
import kotlin.reflect.KClass

class ScaffoldController(
    routes: Map<KClass<out Graph>, List<BottomBarRoute<Screen>>> = emptyMap()
) {
    constructor(vararg routes : Pair<KClass<out Graph>, List<BottomBarRoute<Screen>>>) : this(routes.toMap())

    private val routes: Map<String, List<BottomBarRoute<Screen>>> = routes.mapKeys { it.key.qualifiedName.orEmpty() }

    fun bottomBarItemsFor(graphName: String): List<BottomBarRoute<Screen>> = routes.getOrElse(graphName, ::emptyList)
}

data class BottomBarRoute<T : Any>(
    val name: StringResource?,
    val route: T,
    val icon: ImageVector,
    val graph: Graph
) {
    constructor(route: T, graph: Graph) : this(getRouteTitle(route::class.qualifiedName), route, Icons.Default.Settings, graph)
}

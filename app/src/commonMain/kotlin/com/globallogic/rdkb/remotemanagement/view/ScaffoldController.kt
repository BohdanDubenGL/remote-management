package com.globallogic.rdkb.remotemanagement.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

class ScaffoldController(
    routes: Map<Graph, List<BottomBarRoute<Screen>>> = emptyMap()
) {
    constructor(vararg routes : Pair<Graph, List<BottomBarRoute<Screen>>>) : this(routes.toMap())

    private val routes: Map<String, List<BottomBarRoute<Screen>>> = routes.mapKeys { it.key::class.qualifiedName.orEmpty() }

    fun bottomBarItemsFor(graphName: String): List<BottomBarRoute<Screen>> = routes.getOrElse(graphName, ::emptyList)
}

data class BottomBarRoute<T : Any>(val name: String, val route: T, val icon: ImageVector) {
    constructor(route: T) : this(route::class.simpleName.orEmpty(), route, Icons.Default.Settings)
}

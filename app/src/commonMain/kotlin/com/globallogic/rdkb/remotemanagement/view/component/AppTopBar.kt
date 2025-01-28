package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.navigation.NavController
import com.globallogic.rdkb.remotemanagement.view.ScaffoldController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(navController: NavController, scaffoldController: ScaffoldController) {
    var visibility by remember { mutableStateOf(false) }

    SideEffect {
        visibility = true
    }

    TopAppBar(
        title = { Text(text = "title") },
        modifier = Modifier.alpha(if (visibility) 1F else 0F)
    )
}

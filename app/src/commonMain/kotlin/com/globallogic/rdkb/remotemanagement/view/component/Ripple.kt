package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

data class Ripple(
    val color: Color,
    val target: Float = 600F,
    private val positionFn: () -> Offset,
    private val animationDurationMillis: Int = 10_000,
    private val initialDelay: Long = 0,
    private val initialValue: Float = 60.dp.value,
    private val infinite: Boolean = true,
) {
    var position: Offset = positionFn(); private set
    var radius by mutableStateOf(0F); private set
    var alpha by mutableStateOf(1F); private set
    var isFinished by mutableStateOf(false); private set

    suspend fun startAnimation() {
        delay(initialDelay)
        val anim = Animatable(initialValue)
        do {
            position = positionFn()
            anim.animateTo(target, animationSpec = tween((animationDurationMillis * (1F - anim.value / target)).toInt(), easing = LinearEasing)) {
                radius = value
                alpha = (1F - (value / target)) * 0.7F
            }
            anim.snapTo(0F)
        } while (infinite)
        isFinished = true
    }
}

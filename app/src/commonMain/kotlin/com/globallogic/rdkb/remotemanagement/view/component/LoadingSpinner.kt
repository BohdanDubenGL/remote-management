package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun LoadingSpinner(modifier: Modifier = Modifier) {
    val animation = rememberInfiniteTransition()
    val rotation = animation.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20_000)
        ),
        label = "rotation",
    )
    Box(modifier = modifier.graphicsLayer { rotationZ = rotation.value }) {
        arrayOf(
            Color(0xff1AD4D5),
            Color(0xff1ABFD5),
            Color(0xff1A7FD5),
            Color(0xff1A3FD5),
        ).forEachIndexed { index, color ->
            GradientCircle(color = color, delay = 200 * index)
        }
    }
}

@Composable
fun GradientCircle(
    modifier: Modifier = Modifier,
    color: Color,
    delay: Int = 0,
    duration: Int = 3_000
) {
    val animation = rememberInfiniteTransition()
    val rotation = animation.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration, easing = LinearEasing),
            initialStartOffset = StartOffset(delay)
        ),
        label = "gradientCircleRotation",
    )
    Box(
        modifier = modifier
            .graphicsLayer {
                rotationX = rotation.value
                cameraDistance = 100000f
            }
            .fillMaxSize()
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(color, Color.Transparent),
                        center = Offset.Zero,
                        radius = size.width,
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(color, Color.Transparent),
                        center = Offset.Zero,
                        radius = size.width * 1.5f,
                    ),
                    style = Stroke(width = 1f)
                )
            }
    )
}

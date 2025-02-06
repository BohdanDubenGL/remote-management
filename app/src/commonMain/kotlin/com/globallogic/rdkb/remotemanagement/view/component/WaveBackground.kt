package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path

@Composable
fun WaveBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()

    val secondaryContainerColor = MaterialTheme.colorScheme.secondaryContainer
    val tertiaryContainerColor = MaterialTheme.colorScheme.tertiaryContainer

    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(secondaryContainerColor, tertiaryContainerColor),
                startY = 0f,
                endY = height * 0.6f
            ),
            size = size
        )

        drawPath(
            path = Path().apply {
                moveTo(0f, height * 0.25f + waveOffset)
                cubicTo(
                    width * 0.25f, height * 0.35f + waveOffset,
                    width * 0.75f, height * 0.15f - waveOffset,
                    width, height * 0.35f + waveOffset
                )
                lineTo(width, height)
                lineTo(0f, height)
                close()
            },
            brush = Brush.verticalGradient(
                colors = listOf(secondaryContainerColor, tertiaryContainerColor),
                startY = height * 0.3f,
                endY = height
            )
        )

        drawPath(
            path = Path().apply {
                moveTo(0f, height * 0.75f + waveOffset)
                cubicTo(
                    width * 0.25f, height * 0.65f + waveOffset,
                    width * 0.75f, height * 0.85f - waveOffset,
                    width, height * 0.65f + waveOffset
                )
                lineTo(width, height)
                lineTo(0f, height)
                close()
            },
            brush = Brush.verticalGradient(
                colors = listOf(secondaryContainerColor, tertiaryContainerColor),
                startY = height * 0.6f,
                endY = height
            )
        )
    }
}

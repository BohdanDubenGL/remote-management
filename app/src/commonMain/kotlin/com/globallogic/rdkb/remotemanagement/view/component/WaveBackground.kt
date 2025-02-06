package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

@Composable
fun WaveBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()

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
                colors = listOf(Color(0xFF6200EE), Color(0xFF9C27B0)),
                startY = 0f,
                endY = height * 0.6f
            ),
            size = size
        )

        drawPath(
            path = Path().apply {
                moveTo(0f, height * 0.15f + waveOffset)
                cubicTo(
                    width * 0.25f, height * 0.25f + waveOffset,
                    width * 0.75f, height * 0.05f - waveOffset,
                    width, height * 0.25f + waveOffset
                )
                lineTo(width, height)
                lineTo(0f, height)
                close()
            },
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF9C27B0), Color(0xFF03DAC6)),
                startY = height * 0.4f,
                endY = height
            )
        )

        drawPath(
            path = Path().apply {
                moveTo(0f, height * 0.85f + waveOffset)
                cubicTo(
                    width * 0.25f, height * 0.75f + waveOffset,
                    width * 0.75f, height * 0.95f - waveOffset,
                    width, height * 0.75f + waveOffset
                )
                lineTo(width, height)
                lineTo(0f, height)
                close()
            },
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF9C27B0), Color(0xFF03DAC6)),
                startY = height * 0.6f,
                endY = height
            )
        )
    }
}

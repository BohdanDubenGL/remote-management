package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import kotlin.time.Duration.Companion.seconds

@Composable
fun WaveBackground(
    modifier: Modifier = Modifier,
) {
    WaveAnimation(modifier = modifier, delayMillis = 1000, color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7F))
    WaveAnimation(modifier = modifier, delayMillis = 0, color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7F))
    WaveAnimation(modifier = modifier, delayMillis = 1500, color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8F))
}

@Composable
private fun WaveAnimation(
    modifier: Modifier = Modifier,
    delayMillis: Int,
    color: Color,
) {
    val infiniteTransition = rememberInfiniteTransition()

    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = 100F,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3.seconds.inWholeMilliseconds.toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(delayMillis)
        )
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        drawPath(
            path = Path().apply {
                moveTo(0F, height * 0.15F + waveOffset)
                cubicTo(
                    width * 0.25F, height * 0.15F + waveOffset,
                    width * 0.75F, height * 0.05F - waveOffset,
                    width, height * 0.25F + waveOffset
                )
                lineTo(width, 0F)
                lineTo(0F, 0F)
                close()
            },
            color = color,
        )

        drawPath(
            path = Path().apply {
                moveTo(0F, height * 0.75F + waveOffset)
                cubicTo(
                    width * 0.25F, height * 0.70F + waveOffset,
                    width * 0.75F, height * 0.95F - waveOffset,
                    width, height * 0.85F + waveOffset
                )
                lineTo(width, height)
                lineTo(0F, height)
                close()
            },
            color = color,
        )
    }
}

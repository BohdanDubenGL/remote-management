package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import kotlin.time.Duration.Companion.seconds

@Composable
fun WaveProgressBar(
    modifier: Modifier = Modifier,
    progress: Int,
    circleColor: Color = MaterialTheme.colorScheme.primaryContainer,
    backgroundColor: Color = Color.Black,
    content: @Composable (progress: Int) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(circleColor, CircleShape)
            .padding(2.dp)
            .background(backgroundColor, CircleShape)
            .clip(CircleShape),
    ) {
        val animatedProgress by animateFloatAsState(
            targetValue = progress / 100F,
            animationSpec = tween(durationMillis = 1_000, easing = FastOutSlowInEasing),
        )
        WaveProgressAnimation(
            modifier = modifier,
            delayMillis = 1000,
            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7F),
            horizontalOffsetPercent = 0F,
            progress = animatedProgress,
        )
        WaveProgressAnimation(
            modifier = modifier,
            delayMillis = 1500,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8F),
            horizontalOffsetPercent = 0.04F,
            progress = animatedProgress,
        )
        WaveProgressAnimation(
            modifier = modifier,
            delayMillis = 0,
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7F),
            horizontalOffsetPercent = 0.02F,
            progress = animatedProgress,
        )
        content(progress)
    }
}

@Composable
fun WaveProgressAnimation(
    modifier: Modifier = Modifier,
    delayMillis: Int,
    color: Color,
    horizontalOffsetPercent: Float,
    progress: Float,
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
                moveTo(0F, height * (1F - progress - 0.10F) + waveOffset)
                cubicTo(
                    width * (horizontalOffsetPercent + 0.25F), height * (1F - progress - 0.15F) + waveOffset,
                    width * (horizontalOffsetPercent +  0.55F), height * (1F - progress + 0.10F) - waveOffset,
                    width, height * (1F - progress) + waveOffset
                )
                lineTo(width, height)
                lineTo(0F, height)
                close()
            },
            color = color,
        )
    }
}

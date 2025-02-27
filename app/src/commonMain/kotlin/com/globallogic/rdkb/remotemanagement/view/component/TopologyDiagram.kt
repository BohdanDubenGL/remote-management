package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlendModeColorFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class TopologyNode(
    val name: String,
    val icon: ImageVector,
    val rippleColor: Color,
    val rippleIntervalMillis: Long,
    val rippleDurationMillis: Int,
    val rippleTarget: Float,
    val rippleCount: Int,
) {
    fun createRipples(node: State<NodeState>): Array<Ripple> = Array(rippleCount) { i ->
        Ripple(
            initialDelay = (rippleDurationMillis * (1F - i.inc().toFloat() / rippleCount)).toLong(),
            positionFn = { node.value.position },
            color = rippleColor,
            target = rippleTarget,
            animationDurationMillis = rippleDurationMillis,
        )
    }
}

data class NodeState(
    val topologyNode: TopologyNode,
    val position: Offset,
    val label: String,
    val icon: VectorPainter,
    val color: Color,
    val iconSize: DpSize = DpSize(35.dp, 35.dp),
)

// todo: 5+ clients
@Composable
fun TopologyDiagram(
    modifier: Modifier = Modifier,
    network: TopologyNode,
    router: TopologyNode,
    clients: List<TopologyNode>,
    arrowColor: Color = MaterialTheme.colorScheme.tertiary,
    textColor: Color = MaterialTheme.colorScheme.tertiary,
    tapColor: Color = MaterialTheme.colorScheme.tertiary,
) {
    val networkVectorPainter = rememberVectorPainter(network.icon)
    val routerVectorPainter = rememberVectorPainter(router.icon)
    val clientsVectorPainter = clients.associateWith { rememberVectorPainter(it.icon) }

    val networkNode = remember(network) { mutableStateOf(NodeState(network, Offset(300f, 100f), network.name, networkVectorPainter, Color(0xff1A5FD5))) }
    val routerNode = remember(router) { mutableStateOf(NodeState(router, Offset(300f, 300f), router.name, routerVectorPainter, Color(0xff1A7FD5))) }
    val clientNodes = remember(clients) { clients.mapIndexed { i, client -> mutableStateOf(NodeState(client, Offset(100f + i * 200f, 500f), client.name, clientsVectorPainter[client]!!, Color(0xff1AD4D5))) } }
    val nodes by remember(network, router, clients) { mutableStateOf(listOf(networkNode, routerNode).plus(clientNodes)) }
    val textMeasurer = rememberTextMeasurer()

    val coroutineScope = rememberCoroutineScope()
    val ripples = remember(nodes) {
        mutableStateListOf(
            *network.createRipples(networkNode),
            *router.createRipples(routerNode),
            *clients.zip(clientNodes, TopologyNode::createRipples)
                .flatMap { it.toList() }
                .toTypedArray()
        )
    }

    Box(
        content = {},
        modifier = modifier
            .sizeIn(minWidth = 400.dp, minHeight = 400.dp)
            .onSizeChanged { size ->
                networkNode.value = networkNode.value.copy(
                    position = Offset(size.width * 0.5F, size.height * 0.3F)
                )
                routerNode.value = routerNode.value.copy(
                    position = Offset(size.width * 0.5F, size.height * 0.5F)
                )
                val offset = size.width / clients.size.inc().toFloat()
                clientNodes.forEachIndexed { index, client ->
                    client.value = client.value.copy(
                        position = Offset(offset + index * offset, size.height * 0.7F)
                    )
                }
            }
            .pointerInput(nodes) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    nodes.forEach { node ->
                        if ((change.position - node.value.position).getDistance() < node.value.iconSize.toSize().maxDimension) {
                            val position = node.value.position + dragAmount
                            if (position.x in 0F..size.width.toFloat() && position.y in 0F..size.height.toFloat()) {
                                node.value = node.value.copy(position = position)
                            }
                        }
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val ripple = Ripple(
                        infinite = false,
                        positionFn = { offset },
                        color = tapColor,
                        target = 150F,
                        animationDurationMillis = 1_500,
                    )
                    ripples.add(ripple)
                    coroutineScope.launch { ripple.startAnimation() }
                }
            }
            .drawBehind {
                ripples.forEach { ripple ->
                    drawCircle(
                        color = ripple.color.copy(alpha = ripple.alpha),
                        center = ripple.position,
                        radius = ripple.radius,
                    )
                    drawCircle(
                        color = ripple.color.copy(alpha = ripple.alpha * 1.5F),
                        center = ripple.position,
                        radius = ripple.radius,
                        style = Stroke(width = 2F),
                    )
                }

                drawArrow(networkNode.value.position, routerNode.value.position, networkNode.value.iconSize.height.toPx(), arrowColor)
                clientNodes.forEach { client -> drawArrow(routerNode.value.position, client.value.position, routerNode.value.iconSize.height.toPx(), arrowColor) }

                nodes.forEach { node -> drawNode(node.value, textMeasurer, textColor) }
            },
    )
    LaunchedEffect(ripples) {
        ripples.forEach { ripple ->
            launch { ripple.startAnimation() }
        }
        while (true) {
            delay(1_000)
            ripples.removeAll { it.isFinished }
        }
    }
}

private fun DrawScope.drawNode(node: NodeState, textMeasurer: TextMeasurer, textColor: Color) {
    val iconSize = node.iconSize.toSize()
    drawCircle(color = node.color, center = node.position, radius = iconSize.maxDimension)

    drawIntoCanvas { canvas ->
        translate(node.position.x - iconSize.width / 2F, node.position.y - iconSize.height / 2F) {
            with(node.icon) {
                draw(
                    size = iconSize,
                    colorFilter = BlendModeColorFilter(Color.White, BlendMode.SrcIn)
                )
            }
        }

        val textLayout = textMeasurer.measure(AnnotatedString(node.label))
        drawText(
            textLayoutResult = textLayout,
            color = textColor,
            topLeft = Offset(
                x = node.position.x - textLayout.size.width / 2,
                y = node.position.y + iconSize.height
            )
        )
    }
}

private fun DrawScope.drawArrow(start: Offset, end: Offset, nodeRadius: Float, arrowColor: Color) {
    val arrowHeadSize = 20F
    val angle = atan2((end.y - start.y).toDouble(), (end.x - start.x).toDouble()).toFloat()
    val startOffset = Offset(
        start.x + nodeRadius * cos(angle.toDouble()).toFloat(),
        start.y + nodeRadius * sin(angle.toDouble()).toFloat()
    )
    val endOffset = Offset(
        end.x - (nodeRadius + arrowHeadSize) * cos(angle.toDouble()).toFloat(),
        end.y - (nodeRadius + arrowHeadSize) * sin(angle.toDouble()).toFloat()
    )
    drawLine(
        color = arrowColor,
        start = startOffset,
        end = endOffset,
        strokeWidth = 4F,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10F, 10F)),
    )
    val path = Path().apply {
        moveTo(endOffset.x, endOffset.y)
        lineTo(
            endOffset.x - arrowHeadSize * cos(angle - PI / 6).toFloat(),
            endOffset.y - arrowHeadSize * sin(angle - PI / 6).toFloat()
        )
        moveTo(endOffset.x, endOffset.y)
        lineTo(
            endOffset.x - arrowHeadSize * cos(angle + PI / 6).toFloat(),
            endOffset.y - arrowHeadSize * sin(angle + PI / 6).toFloat()
        )
    }
    drawPath(path, color = arrowColor, style = Stroke(width = 4F))
}

package com.globallogic.rdkb.remotemanagement.view.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Router
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class Network(val name: String, val icon: ImageVector = Icons.Default.Public)
data class Router(val name: String, val icon: ImageVector = Icons.Default.Router)
data class Client(val name: String, val icon: ImageVector = Icons.Default.Phone)

data class NodeState(val position: Offset, val label: String, val icon: VectorPainter, val color: Color)

@Composable
fun TopologyDiagram(
    network: Network,
    router: Router,
    clients: List<Client>
) {
    val networkVectorPainter = rememberVectorPainter(network.icon)
    val routerVectorPainter = rememberVectorPainter(router.icon)
    val clientsVectorPainter = clients.associateWith { rememberVectorPainter(it.icon) }

    val networkState = remember { mutableStateOf(NodeState(Offset(300f, 100f), network.name, networkVectorPainter, Color.Blue)) }
    val routerState = remember { mutableStateOf(NodeState(Offset(300f, 300f), router.name, routerVectorPainter, Color.Red)) }
    val clientStates = remember { clients.mapIndexed { i, client -> mutableStateOf(NodeState(Offset(100f + i * 200f, 500f), client.name, clientsVectorPainter[client]!!, Color.Green)) } }
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
        networkState.value = networkState.value.copy(position = Offset(size.width / 2F, size.height / 4F))
        routerState.value = routerState.value.copy(position = Offset(size.width / 2F, size.height / 2F))
        val offset = size.width / clients.size.inc().toFloat()
        clientStates.forEachIndexed { index, client ->
            client.value = client.value.copy(position = Offset(offset + index * offset, size.height * 3F / 4F))
        }
        detectDragGestures { change, dragAmount ->
            change.consume()
            listOf(networkState, routerState).plus(clientStates).forEach {
                if ((change.position - it.value.position).getDistance() < 50f) {
                    it.value = it.value.copy(position = it.value.position + dragAmount)
                }
            }
        }
    }) {
        drawNode(networkState.value, textMeasurer)
        drawNode(routerState.value, textMeasurer)
        clientStates.forEach { drawNode(it.value, textMeasurer) }
        drawArrow(networkState.value.position, routerState.value.position)
        clientStates.forEach { drawArrow(routerState.value.position, it.value.position) }
    }
}

fun DrawScope.drawNode(node: NodeState, textMeasurer: TextMeasurer) {
    drawCircle(color = node.color, center = node.position, radius = 40f)
    drawIntoCanvas { canvas ->
        val textLayout = textMeasurer.measure(AnnotatedString(node.label))
        drawText(
            textLayoutResult = textLayout,
            topLeft = Offset(
                x = node.position.x - textLayout.size.width / 2,
                y = node.position.y + 60
            )
        )
    }
}

fun DrawScope.drawArrow(start: Offset, end: Offset) {
    val nodeRadius = 40f
    val arrowHeadSize = 20f
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
        color = Color.Black,
        start = startOffset,
        end = endOffset,
        strokeWidth = 4f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)),
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
    drawPath(path, color = Color.Black, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f))
}

package com.origeek.imageViewer.zoomable

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangeConsumed
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.absoluteValue

/**
 * @program: ImageViewer
 *
 * @description:
 *
 * @author: JVZIYAOYAO
 *
 * @create: 2023-11-24 10:15
 **/

/**
 * ZoomableView手势对象
 */
class ZoomableGestureScope(
    // 点击事件
    var onTap: (Offset) -> Unit = {},
    // 双击事件
    var onDoubleTap: (Offset) -> Unit = {},
    // 长按事件
    var onLongPress: (Offset) -> Unit = {},
)


@Composable
fun ZoomableView(
    state: ZoomableViewState,
    // 调试模式
    debugMode: Boolean = false,
    // 检测手势
    detectGesture: ZoomableGestureScope = ZoomableGestureScope(),
    // 显示内容
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    state.apply {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged {
                    updateContainerSize(
                        Size(
                            width = it.width.toFloat(),
                            height = it.height.toFloat(),
                        )
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        clip = false
                    }
                    .pointerInput(state) {
                        detectTapGestures(onLongPress = { detectGesture.onLongPress(it) })
                    }
                    .pointerInput(state) {
                        detectTransformGestures(
                            onTap = { detectGesture.onTap(it) },
                            onDoubleTap = { detectGesture.onDoubleTap(it) },
                            gestureStart = {
                                onGestureStart(scope)
                            },
                            gestureEnd = { transformOnly ->
                                onGestureEnd(scope, transformOnly)
                            },
                            onGesture = { center, pan, zoom, rotate, event ->
                                onGesture(scope, center, pan, zoom, rotate, event)
                            },
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            transformOrigin = TransformOrigin(0F, 0F)
                            scaleX = scale.value
                            scaleY = scale.value
                            translationX = offsetX.value
                            translationY = offsetY.value
                            clip = false
                        }
                        .graphicsLayer {
                            transformOrigin = TransformOrigin.Center
                            rotationZ = rotation.value
                        }
                        .width(density.run { displayWidth.toDp() })
                        .height(density.run { displayHeight.toDp() })
                        .run {
                            if (debugMode) {
                                background(Color.Blue.copy(0.2F))
                            } else this
                        }

                ) {
                    content()
                }
            }
            if (debugMode) {
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            translationX = gestureCenter.value.x - 6.dp.toPx()
                            translationY = gestureCenter.value.y - 6.dp.toPx()
                        }
                        .clip(CircleShape)
                        .background(Color.Red.copy(0.4f))
                        .size(12.dp)
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Cyan)
                        .size(12.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }

}

/**
 * 追踪缩放过程中的中心点
 */
fun panTransformAndScale(
    currentOffset: Float,
    displayLength: Float,
    displayOffset: Float,
    center: Float,
    fromScale: Float,
    toScale: Float,
): Float {
    val currentLength = fromScale.times(displayLength)
    // 中心点在1倍图片上的落点
    val offsetOn1Scale = center - displayOffset
    // 中心点在实际图片上的落点
    val offsetOnReal = offsetOn1Scale - currentOffset
    // 加下来的长度
    val nextLength = toScale.times(displayLength)
    // 接下来中心点在图片上的落点
    val nextOffsetOnReal = nextLength.times(offsetOnReal.div(currentLength))
    // 接下来图片的偏移量
    return offsetOn1Scale - nextOffsetOnReal
}

/**
 * 把位移限制在边界内
 */
fun limitToBound(offset: Float, bound: Pair<Float, Float>): Float {
    return when {
        offset <= bound.first -> {
            bound.first
        }

        offset > bound.second -> {
            bound.second
        }

        else -> {
            offset
        }
    }
}

/**
 *
 * 获取范围的中心点
 *
 */
fun getBoundCenter(bound: Pair<Float, Float>): Float {
    return (bound.first + bound.second).div(2)
}

/**
 * 判断位移是否在边界内
 */
fun inBound(offset: Float, bound: Pair<Float, Float>): Boolean {
    return if (offset > 0) {
        offset < bound.second
    } else if (offset < 0) {
        offset > bound.first
    } else {
        true
    }
}

/**
 * 获取移动边界
 */
fun getBound(scale: Float, bw: Float, dw: Float, off: Float): Pair<Float, Float> {
    val rw = scale.times(dw)
    return if (rw > bw) {
        val xw01 = -(off + rw - bw)
        val xw02 = -off
        Pair(xw01, xw02)
    } else {
        var xw = (rw - dw).div(2)
        if (xw < 0) xw = 0F
        Pair(-xw, -xw)
    }
}

/**
 * 让后一个数与前一个数的符号保持一致
 * @param a Float
 * @param b Float
 * @return Float
 */
fun sameDirection(a: Float, b: Float): Float {
    return if (a > 0) {
        if (b < 0) {
            b.absoluteValue
        } else {
            b
        }
    } else {
        if (b > 0) {
            -b
        } else {
            b
        }
    }
}

/**
 * 重写事件监听方法
 */
suspend fun PointerInputScope.detectTransformGestures(
    panZoomLock: Boolean = false,
    gestureStart: () -> Unit = {},
    gestureEnd: (Boolean) -> Unit = {},
    onTap: (Offset) -> Unit = {},
    onDoubleTap: (Offset) -> Unit = {},
    onGesture: (centroid: Offset, pan: Offset, zoom: Float, rotation: Float, event: PointerEvent) -> Boolean,
) {
    var lastReleaseTime = 0L
    var scope: CoroutineScope? = null
    forEachGesture {
        awaitPointerEventScope {
            var rotation = 0f
            var zoom = 1f
            var pan = Offset.Zero
            var pastTouchSlop = false
            val touchSlop = viewConfiguration.touchSlop
            var lockedToPanZoom = false

            awaitFirstDown(requireUnconsumed = false)
            val t0 = System.currentTimeMillis()
            var releasedEvent: PointerEvent? = null
            var moveCount = 0
            // 这里开始事件
            gestureStart()
            do {
                val event = awaitPointerEvent()
                if (event.type == PointerEventType.Release) releasedEvent = event
                if (event.type == PointerEventType.Move) moveCount++
                val canceled = event.changes.fastAny { it.positionChangeConsumed() }
                if (!canceled) {
                    val zoomChange = event.calculateZoom()
                    val rotationChange = event.calculateRotation()
                    val panChange = event.calculatePan()

                    if (!pastTouchSlop) {
                        zoom *= zoomChange
                        rotation += rotationChange
                        pan += panChange

                        val centroidSize = event.calculateCentroidSize(useCurrent = false)
                        val zoomMotion = abs(1 - zoom) * centroidSize
                        val rotationMotion = abs(rotation * PI.toFloat() * centroidSize / 180f)
                        val panMotion = pan.getDistance()

                        if (zoomMotion > touchSlop ||
                            rotationMotion > touchSlop ||
                            panMotion > touchSlop
                        ) {
                            pastTouchSlop = true
                            lockedToPanZoom = panZoomLock && rotationMotion < touchSlop
                        }
                    }
                    if (pastTouchSlop) {
                        val centroid = event.calculateCentroid(useCurrent = false)
                        val effectiveRotation = if (lockedToPanZoom) 0f else rotationChange
                        if (effectiveRotation != 0f ||
                            zoomChange != 1f ||
                            panChange != Offset.Zero
                        ) {
                            if (!onGesture(
                                    centroid,
                                    panChange,
                                    zoomChange,
                                    effectiveRotation,
                                    event
                                )
                            ) break
                        }
                    }
                }
            } while (!canceled && event.changes.fastAny { it.pressed })

            var t1 = System.currentTimeMillis()
            val dt = t1 - t0
            val dlt = t1 - lastReleaseTime

            if (moveCount == 0) releasedEvent?.let { e ->
                if (e.changes.isEmpty()) return@let
                val offset = e.changes.first().position
                if (dlt < 272) {
                    t1 = 0L
                    scope?.cancel()
                    onDoubleTap(offset)
                } else if (dt < 200) {
                    scope = MainScope()
                    scope?.launch(Dispatchers.Main) {
                        delay(272)
                        onTap(offset)
                    }
                }
                lastReleaseTime = t1
            }

            // 这里是事件结束
            gestureEnd(moveCount != 0)
        }
    }
}
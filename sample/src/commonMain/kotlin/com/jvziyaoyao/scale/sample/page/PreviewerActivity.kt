package com.jvziyaoyao.scale.sample.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.jvziyaoyao.scale.image.previewer.ImagePreviewer
import com.jvziyaoyao.scale.zoomable.pager.PagerGestureScope
import com.jvziyaoyao.scale.zoomable.previewer.PreviewerState
import com.jvziyaoyao.scale.zoomable.previewer.TransformLayerScope
import com.jvziyaoyao.scale.zoomable.previewer.rememberPreviewerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

const val SYSTEM_UI_VISIBILITY = "SYSTEM_UI_VISIBILITY"

@Composable
fun PreviewerBody(
    onImageViewVisible: (Boolean) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val images = remember {
        listOf(
            Icons.Default.ShoppingCart,
            Icons.Default.ShoppingCart,
            Icons.Default.ShoppingCart,
            Icons.Default.ShoppingCart,
            Icons.Default.ShoppingCart,
            Icons.Default.ShoppingCart
        )
    }

    val previewerState =
        rememberPreviewerState(pageCount = { images.size })
//    val previewerState = rememberPopupPreviewerState(pageCount = { images.size })
    if (previewerState.visible) {
        handlePlatformBack(scope,previewerState)
    }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val horizontal = maxWidth > maxHeight
        val lineCount = if (horizontal) 6 else 3
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            LazyVerticalGrid(columns = GridCells.Fixed(lineCount)) {
                images.forEachIndexed { index, item ->
                    item {
                        val needStart = index % lineCount != 0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1F)
                                .padding(start = if (needStart) 2.dp else 0.dp, bottom = 2.dp)
                        ) {
                            val painter =rememberVectorPainter(image = item)
                            Image(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        scope.launch {
                                            previewerState.open(index = index)
                                        }
                                    },
                                painter = painter,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                }
            }
        }
        LaunchedEffect(key1 = previewerState.visible, block = {
            onImageViewVisible(previewerState.visible)
        })
        ImagePreviewer(
            state = previewerState,
            detectGesture = PagerGestureScope(onTap = {
                scope.launch {
                    previewerState.close()
                }
            }),
            previewerLayer = TransformLayerScope(
                background = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(0.8F))
                    )
                }
            ),
            imageLoader = { index ->
                val item = images[index]
                val painter =rememberVectorPainter(image = item)
                Pair(painter, painter.intrinsicSize)
            }
        )
    }
}

@Composable
expect fun handlePlatformBack(scope: CoroutineScope, previewerState: PreviewerState)

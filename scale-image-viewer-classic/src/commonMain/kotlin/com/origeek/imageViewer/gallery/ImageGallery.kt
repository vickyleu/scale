package com.origeek.imageViewer.gallery


import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.jvziyaoyao.scale.zoomable.pager.DEFAULT_ITEM_SPACE
import com.jvziyaoyao.scale.zoomable.pager.SupportedHorizonPager
import com.jvziyaoyao.scale.zoomable.pager.SupportedPagerState
import com.jvziyaoyao.scale.zoomable.pager.rememberSupportedPagerState
import com.origeek.imageViewer.util.FloatRangeImpl
import com.origeek.imageViewer.util.IntRangeImpl
import com.origeek.imageViewer.viewer.ImageViewer
import com.origeek.imageViewer.viewer.ImageViewerState
import com.origeek.imageViewer.viewer.commonDeprecatedText
import com.origeek.imageViewer.viewer.rememberViewerState
import kotlinx.coroutines.launch

/**
 * @program: ImageViewer
 *
 * @description:
 *
 * @author: JVZIYAOYAO
 *
 * @create: 2022-10-10 11:50
 **/

/**
 * gallery手势对象
 */
@Deprecated(
    message = commonDeprecatedText,
)
class GalleryGestureScope(
    // 点击事件
    var onTap: () -> Unit = {},
    // 双击事件
    var onDoubleTap: () -> Boolean = { false },
    // 长按事件
    var onLongPress: () -> Unit = {},
)

/**
 * gallery图层对象
 */
@Deprecated(
    message = commonDeprecatedText,
)
class GalleryLayerScope(
    // viewer图层
    var viewerContainer: @Composable (
        page: Int, viewerState: ImageViewerState, viewer: @Composable () -> Unit
    ) -> Unit = { _, _, viewer -> viewer() },
    // 背景图层
    var background: @Composable ((Int) -> Unit) = {},
    // 前景图层
    var foreground: @Composable ((Int) -> Unit) = {},
)

/**
 * gallery状态
 */
@Deprecated(
    message = commonDeprecatedText,
)
open class ImageGalleryState(
    val pagerState: SupportedPagerState,
) {

    /**
     * 当前viewer的状态
     */
    var imageViewerState by mutableStateOf<ImageViewerState?>(null)
        internal set

    /**
     * 当前页码
     */
    val currentPage: Int
        get() = pagerState.currentPage

    /**
     * 目标页码
     */
    val targetPage: Int
        get() = pagerState.targetPage

    /**
     * 总页数
     */
    val pageCount: Int
        get() = pagerState.pageCount

    /**
     * interactionSource
     */
    val interactionSource: InteractionSource
        get() = pagerState.interactionSource

    /**
     * 滚动到指定页面
     * @param page Int
     * @param pageOffset Float
     */
    suspend fun scrollToPage(
        @IntRangeImpl(from = 0) page: Int,
        @FloatRangeImpl(from = 0.0, to = 1.0) pageOffset: Float = 0f,
    ) = pagerState.scrollToPage(page, pageOffset)

    /**
     * 动画滚动到指定页面
     * @param page Int
     * @param pageOffset Float
     */
    suspend fun animateScrollToPage(
        @IntRangeImpl(from = 0) page: Int,
        @FloatRangeImpl(from = 0.0, to = 1.0) pageOffset: Float = 0f,
    ) = pagerState.animateScrollToPage(page, pageOffset)

}

/**
 * 记录gallery状态
 */
@Deprecated(
    message = commonDeprecatedText,
)
@Composable
fun rememberImageGalleryState(
    @IntRangeImpl(from = 0) initialPage: Int = 0,
    pageCount: () -> Int,
): ImageGalleryState {
    val imagePagerState =
        rememberSupportedPagerState(initialPage, pageCount)
    return remember { ImageGalleryState(imagePagerState) }
}

/**
 * 图片gallery,基于Pager实现的一个图片查看列表组件
 */
@Deprecated(
    message = "方法已弃用，请使用：com.jvziyaoyao.image.pager.ImagePager",
)
@Composable
fun ImageGallery(
    // 编辑参数
    modifier: Modifier = Modifier,
    // gallery状态
    state: ImageGalleryState,
    // 图片加载器
    imageLoader: @Composable (Int) -> Any?,
    // 每张图片之间的间隔
    itemSpacing: Dp = DEFAULT_ITEM_SPACE,
    // 检测手势
    detectGesture: GalleryGestureScope.() -> Unit = {},
    // gallery图层
    galleryLayer: GalleryLayerScope.() -> Unit = {},
) {
//    require(count >= 0) { "imageCount must be >= 0" }
    val scope = rememberCoroutineScope()
    // 手势相关
    val galleryGestureScope = remember { GalleryGestureScope() }
    detectGesture.invoke(galleryGestureScope)
    // 图层相关
    val galleryLayerScope = remember { GalleryLayerScope() }
    galleryLayer.invoke(galleryLayerScope)
    // 确保不会越界
    val currentPage = state.currentPage

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        galleryLayerScope.background(currentPage)
        SupportedHorizonPager(
            state = state.pagerState,
            modifier = Modifier
                .fillMaxSize(),
            itemSpacing = itemSpacing,
        ) { page ->
            val imageState = rememberViewerState()
            LaunchedEffect(key1 = currentPage) {
                if (currentPage != page) imageState.reset()
                if (currentPage == page) {
                    state.imageViewerState = imageState
                }
            }
            galleryLayerScope.viewerContainer(page, imageState) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    key(page) {
                        ImageViewer(
                            modifier = Modifier.fillMaxSize(),
                            model = imageLoader(page),
                            state = imageState,
                            boundClip = false,
                            detectGesture = {
                                this.onTap = {
                                    galleryGestureScope.onTap()
                                }
                                this.onDoubleTap = {
                                    val consumed = galleryGestureScope.onDoubleTap()
                                    if (!consumed) scope.launch {
                                        imageState.toggleScale(it)
                                    }
                                }
                                this.onLongPress = { galleryGestureScope.onLongPress() }
                            },
                        )
                    }
                }
            }
        }
        galleryLayerScope.foreground(currentPage)
    }
}
package com.jvziyaoyao.scale.image.sampling

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmapConfig


class Options {
    var inSampleSize: Int = 1
    var inPreferredConfig: ImageBitmapConfig? = null
    var inJustDecodeBounds: Boolean = false
    var inMutable: Boolean = false
    var inBitmap: ImageBitmap? = null
}

expect class CommonBitmapRegionDecoder(inputStream: CommonInputStream, isShareable: Boolean=false) {
    val isRecycled: Boolean
    val width: Int
    val height: Int

    fun decodeRegion(rect: Rect,options: Options): ImageBitmap
    fun recycle()

    companion object {
        fun newInstance(inputStream: CommonInputStream, flag: Boolean=true): CommonBitmapRegionDecoder?
    }
}


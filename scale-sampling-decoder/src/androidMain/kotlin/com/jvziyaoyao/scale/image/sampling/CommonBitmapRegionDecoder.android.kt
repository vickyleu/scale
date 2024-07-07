package com.jvziyaoyao.scale.image.sampling

import android.graphics.BitmapRegionDecoder
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap


actual class CommonBitmapRegionDecoder actual constructor(
    inputStream: CommonInputStream,
    isShareable: Boolean
) {
    private val decoder = BitmapRegionDecoder.newInstance(inputStream.iss, isShareable)!!

    actual val isRecycled: Boolean
        get() = decoder.isRecycled
    actual val width: Int
        get() = decoder.width
    actual val height: Int
        get() = decoder.height

    actual fun decodeRegion(
        rect: Rect,
        options: Options
    ): ImageBitmap {
        val bitmap = decoder.decodeRegion(
            android.graphics.Rect(
                rect.left.toInt(),
                rect.top.toInt(),
                rect.right.toInt(),
                rect.bottom.toInt()
            ), null
        )
        return bitmap.asImageBitmap()
    }

    actual fun recycle() {
        decoder.recycle()
    }

    actual companion object {
        actual fun newInstance(
            inputStream: CommonInputStream,
            flag: Boolean
        ): CommonBitmapRegionDecoder? {
            return CommonBitmapRegionDecoder(inputStream, flag)
        }
    }

}
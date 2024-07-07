package com.jvziyaoyao.scale.image.sampling

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.FileInputStream

/**
 * 通过流创建BitmapRegionDecoder
 *
 * @param inputStream
 * @return
 */
actual fun createBitmapRegionDecoder(inputStream: CommonInputStream): CommonBitmapRegionDecoder? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        CommonBitmapRegionDecoder.newInstance(inputStream)
    } else {
        CommonBitmapRegionDecoder.newInstance(inputStream, false)
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
actual fun createSamplingDecoder(file: CommonFile): SamplingDecoder? {
    val inputStream = FileInputStream(file)
    val iss = CommonInputStream(inputStream)
    val exifInterface = androidx.exifinterface.media.ExifInterface(file)
    val decoder = createBitmapRegionDecoder(iss)
    val rotation = exifInterface.getDecoderRotation()
    return decoder?.let { createSamplingDecoder(it, rotation) }
}

/**
 * 通过Exif接口获取SamplingDecoder的旋转方向
 *
 * @return
 */
fun androidx.exifinterface.media.ExifInterface.getDecoderRotation(): SamplingDecoder.Rotation {
    val orientation = getAttributeInt(
        androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
        androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
    )
    return when (orientation) {
        androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> SamplingDecoder.Rotation.ROTATION_90
        androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> SamplingDecoder.Rotation.ROTATION_180
        androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> SamplingDecoder.Rotation.ROTATION_270
        else -> SamplingDecoder.Rotation.ROTATION_0
    }
}

actual fun ImageBitmap?.recycle() {
    this?.asAndroidBitmap()?.recycle()
}

actual fun SamplingDecoder.getRotateBitmap(
    bitmap: ImageBitmap,
    degree: Float
): ImageBitmap {
    val matrix = Matrix()
    matrix.postRotate(degree)
    return Bitmap.createBitmap(
        bitmap.asAndroidBitmap(),
        0,
        0,
        bitmap.width,
        bitmap.height,
        matrix,
        false
    ).asImageBitmap()
}
package com.jvziyaoyao.scale.image.sampling

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.refTo
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Image
import org.jetbrains.skia.Matrix33
import platform.Foundation.NSFileHandle
import platform.Foundation.closeFile
import platform.Foundation.fileHandleForReadingAtPath
import platform.Foundation.readDataOfLength
import platform.posix.memcpy
import kotlin.math.abs

/**
 * 通过流创建BitmapRegionDecoder
 *
 * @param inputStream
 * @return
 */
actual fun createBitmapRegionDecoder(inputStream: CommonInputStream): CommonBitmapRegionDecoder? {
    return CommonBitmapRegionDecoder.newInstance(inputStream)
}


actual fun createSamplingDecoder(file: CommonFile): SamplingDecoder? {
    val inputStream = CommonFileInputStream(file.fileFullPath)
    val exifInterface = ExifInterface(file)
    val decoder = createBitmapRegionDecoder(inputStream)
    val rotation = exifInterface.getDecoderRotation()
    return decoder?.let { createSamplingDecoder(it, rotation) }
}

class CommonFileInputStream(path: String) : CommonInputStream() {
    private val fileHandle: NSFileHandle? = NSFileHandle.fileHandleForReadingAtPath(path)
    private var eof = false

    init {
        require(fileHandle != null) { "File not found: $path" }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun read(): Int {
        val data = fileHandle?.readDataOfLength(1u) ?: return -1
        return if (data.length.toInt() > 0) {
            data.bytes!!.reinterpret<ByteVar>().pointed.value.toInt()
        } else {
            eof = true
            -1
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        val data = fileHandle?.readDataOfLength(length.toULong()) ?: return -1
        val bytesRead = data.length.toInt()
        if (bytesRead > 0) {
            memScoped {
                val bufferPointer = buffer.refTo(offset).getPointer(this)
                memcpy(bufferPointer, data.bytes, bytesRead.toULong())
                return bytesRead
            }
        }
        eof = true
        return -1
    }

    fun close() {
        fileHandle?.closeFile()
    }
}

actual fun ImageBitmap?.recycle() {
    this?.asSkiaBitmap()?.reset()
}

actual fun SamplingDecoder.getRotateBitmap(
    bitmap: ImageBitmap,
    degree: Float
): ImageBitmap {
    val matrix = Matrix()
    matrix.rotateZ(degree)
//        matrix.postRotate(degree)
//        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)

    // 使用Skia进行图像操作
    val skiaBitmap = bitmap.asSkiaBitmap()
    val result = Bitmap().apply {
        allocN32Pixels(skiaBitmap.width, skiaBitmap.height)
    }
    val canvas = Canvas(result)
    canvas.concat(matrix.asSkiaMatrix())
    canvas.drawImage(Image.makeFromBitmap(skiaBitmap), 0f, 0f)
    canvas.restore()
    canvas.readPixels(result, 0, 0)
    canvas.close()
    return result.asComposeImageBitmap()
}
// 扩展函数，将Compose Multiplatform的Matrix转换为Skia的Matrix
private fun Matrix.asSkiaMatrix(coerceScale: Boolean = false): org.jetbrains.skia.Matrix33 {
    // skiko shaders with zero scale cause crash
    val scaleX = when {
        coerceScale && abs(values[Matrix.ScaleX]) < 0.001f -> 0.001f
        else -> values[Matrix.ScaleX]
    }
    val scaleY = when {
        coerceScale && abs(values[Matrix.ScaleY]) < 0.001f -> 0.001f
        else -> values[Matrix.ScaleY]
    }
    return Matrix33(
        scaleX,
        values[Matrix.SkewX],
        values[Matrix.TranslateX],
        values[Matrix.SkewY],
        scaleY,
        values[Matrix.TranslateY],
        values[Matrix.Perspective0],
        values[Matrix.Perspective1],
        values[Matrix.Perspective2],
    )
}

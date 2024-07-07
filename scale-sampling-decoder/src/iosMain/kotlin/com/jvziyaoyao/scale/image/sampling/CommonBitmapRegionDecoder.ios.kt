package com.jvziyaoyao.scale.image.sampling

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.CoreFoundation.CFDataRef
import platform.CoreGraphics.CGBitmapContextCreate
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGContextDrawImage
import platform.CoreGraphics.CGDataProviderCreateWithCFData
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGImageCreateWithImageInRect
import platform.CoreGraphics.CGImageGetHeight
import platform.CoreGraphics.CGImageGetWidth
import platform.CoreGraphics.CGImageRef
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.kCGBitmapByteOrder32Big
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.create
import platform.ImageIO.CGImageSourceCreateImageAtIndex
import platform.ImageIO.CGImageSourceCreateWithDataProvider
import platform.ImageIO.CGImageSourceRef

@OptIn(ExperimentalForeignApi::class)
actual class CommonBitmapRegionDecoder actual constructor(
    inputStream: CommonInputStream,
    isShareable: Boolean
) :
    SynchronizedObject() {
    private val imageSource: CGImageSourceRef? = CGImageSourceCreateWithDataProvider(
        CGDataProviderCreateWithCFData(
            CFBridgingRetain(inputStream.readAllBytes().toNSData()) as CFDataRef
        ),
        null
    )

    private val image = imageSource?.let {
        CGImageSourceCreateImageAtIndex(it, 0u, null)
    }


    actual val isRecycled: Boolean
        get() = image == null

    actual val width: Int
        get() {
            if (image == null) {
                return 0
            }
            val width = CGImageGetWidth(image).toInt()
            return width
        }

    actual val height: Int
        get() {
            if (image == null) {
                return 0
            }
            val height = CGImageGetHeight(image).toInt()
            return height
        }

    actual fun decodeRegion(rect: Rect, options: Options): ImageBitmap {
        val croppedImage = image?.let {
            CGImageCreateWithImageInRect(
                it,
                CGRectMake(
                    rect.left.toDouble(),
                    rect.top.toDouble(),
                    rect.width.toDouble(),
                    rect.height.toDouble()
                )
            )
        } ?: throw IllegalStateException("Image not loaded")

        return croppedImage.asImageBitmap()
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun ByteArray.toNSData(): NSData {
        return this.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = this.size.toULong())
        }
    }

    private fun CGImageRef.asImageBitmap(): ImageBitmap {
        val width = CGImageGetWidth(this).toInt()
        val height = CGImageGetHeight(this).toInt()
        val colorSpace = CGColorSpaceCreateDeviceRGB()
        val rawData = ByteArray((width * height * 4)) // Assuming 4 bytes per pixel (RGBA)
        rawData.usePinned { pinned ->
            val context = CGBitmapContextCreate(
                pinned.addressOf(0),
                width.toULong(),
                height.toULong(),
                8u,
                (4 * width).toULong(),
                colorSpace,
                CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value
                        or kCGBitmapByteOrder32Big
            )
            context?.let {
                CGContextDrawImage(
                    context,
                    CGRectMake(0.0, 0.0, width.toDouble(), height.toDouble()),
                    this
                )
            }
        }
        val bm = ImageBitmap(
            width, height,
            config = ImageBitmapConfig.Argb8888,
            hasAlpha = true,
            colorSpace = ColorSpaces.Srgb
        )
        bm.asSkiaBitmap().apply {
            installPixels(rawData)
        }
        return bm
    }

    actual fun recycle() {
        // For CoreGraphics, no explicit recycling is needed as memory management is handled by ARC
    }

    actual companion object {
        actual fun newInstance(
            inputStream: CommonInputStream,
            flag: Boolean
        ): CommonBitmapRegionDecoder? {
            return try {
                CommonBitmapRegionDecoder(inputStream, flag)
            } catch (e: Exception) {
                null
            }
        }
    }

}
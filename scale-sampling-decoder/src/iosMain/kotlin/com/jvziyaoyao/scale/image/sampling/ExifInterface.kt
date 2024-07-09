package com.jvziyaoyao.scale.image.sampling

import kotlinx.cinterop.COpaquePointerVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.get
import kotlinx.cinterop.pointed
import kotlinx.cinterop.refTo
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryGetCount
import platform.CoreFoundation.CFDictionaryGetKeysAndValues
import platform.CoreFoundation.CFNumberGetValue
import platform.CoreFoundation.CFNumberRef
import platform.CoreFoundation.CFStringGetCString
import platform.CoreFoundation.CFStringGetLength
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFStringRefVar
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.CFURLRef
import platform.CoreFoundation.kCFNumberIntType
import platform.CoreFoundation.kCFStringEncodingUTF8
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSURL
import platform.ImageIO.CGImageSourceCopyPropertiesAtIndex
import platform.ImageIO.CGImageSourceCreateWithURL
import platform.darwin.NSIntegerVar
import platform.posix.free
import platform.posix.malloc
import kotlin.collections.set

@OptIn(ExperimentalForeignApi::class)
@Suppress("unchecked_cast")
class ExifInterface(file: CommonFile) {
    private val properties = mutableMapOf<String, Any?>()

    init {
        val url = NSURL.fileURLWithPath(file.fileFullPath)
        val cfurl = CFBridgingRetain(url) as CFURLRef
        val source = CGImageSourceCreateWithURL(cfurl, null)
        val cfProperties = CGImageSourceCopyPropertiesAtIndex(source, 0u, null)
        cfProperties?.let {
            val count = CFDictionaryGetCount(it).toInt()
            val keysTypeRef = malloc(count.toULong() * sizeOf<CFStringRefVar>().toULong())!!
                .reinterpret<COpaquePointerVar>()
            val valuesTypeRef = malloc(count.toULong() * sizeOf<CFTypeRefVar>().toULong())!!
                .reinterpret<COpaquePointerVar>()

            CFDictionaryGetKeysAndValues(it, keysTypeRef, valuesTypeRef)

            val keysArray = keysTypeRef.toKStringArray(count)
            val valuesArray = valuesTypeRef.toCFTypeArray(count)

            for (i in 0 until count) {
                val key = keysArray[i] ?: continue
                val value = valuesArray[i]
                properties[key] = value
            }

            free(keysTypeRef)
            free(valuesTypeRef)
        }
    }

    fun getAttribute(key: String): Any? {
        return properties[key]
    }

    fun getDecoderRotation(): SamplingDecoder.Rotation {
        val orientation =
            properties["Orientation"] as? CFNumberRef ?: return SamplingDecoder.Rotation.ROTATION_0
        val orientationValueRef = malloc(sizeOf<NSIntegerVar>().toULong())!!
        val flag = CFNumberGetValue(orientation, kCFNumberIntType, orientationValueRef)
        return if (!flag) {
            SamplingDecoder.Rotation.ROTATION_0
        } else {
            when (orientationValueRef.reinterpret<IntVar>().pointed.value) {
                1 -> SamplingDecoder.Rotation.ROTATION_0
                3 -> SamplingDecoder.Rotation.ROTATION_180
                6 -> SamplingDecoder.Rotation.ROTATION_90
                8 -> SamplingDecoder.Rotation.ROTATION_270
                else -> SamplingDecoder.Rotation.ROTATION_0
            }
        }.also {
            free(orientationValueRef)
        }
    }

    private fun CPointer<COpaquePointerVar>.toKStringArray(size: Int): Array<String?> {
        val array = Array<String?>(size) { null }
        for (i in 0 until size) {
            val cfStringRef: CPointer<CFStringRefVar> = this[i]?.reinterpret() ?: continue
            array[i] = cfStringRef.pointed.value?.toKString()
        }
        return array
    }

    private fun CFStringRef.toKString(): String? {
        val length = CFStringGetLength(this)
        val cString = ByteArray(length.toInt() * 4 + 1)
        val success = CFStringGetCString(this, cString.refTo(0), cString.size.toLong(), kCFStringEncodingUTF8)
        return if (success) cString.toKString() else null
    }

    private fun CPointer<COpaquePointerVar>.toCFTypeArray(size: Int): Array<CFTypeRef?> {
        val array = Array<CFTypeRef?>(size) { null }
        for (i in 0 until size) {
            array[i] = this[i]?.reinterpret<CFTypeRefVar>()?.pointed?.value
        }
        return array
    }
}
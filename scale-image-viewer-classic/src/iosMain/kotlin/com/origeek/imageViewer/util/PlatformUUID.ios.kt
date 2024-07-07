package com.origeek.imageViewer.util

import platform.Foundation.NSUUID

actual object PlatformUUID {
    actual fun randomUUID(): String {
        return NSUUID().UUIDString()
    }
}
package com.origeek.imageViewer.util

import java.util.UUID

actual object PlatformUUID {
    actual fun randomUUID(): String {
        return UUID.randomUUID().toString()
    }
}
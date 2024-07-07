package com.jvziyaoyao.scale.image.sampling

import platform.Foundation.NSFileManager


actual class CommonFile {
    actual fun exists(): Boolean {
        return fileManager.fileExistsAtPath(fileFullPath)
    }

    private val fileManager = NSFileManager.defaultManager()
    internal var fileFullPath = ""

    actual constructor(path: String) {
        fileFullPath = path
    }

    actual constructor(parent: String, child: String) {
        fileFullPath = "$parent/$child"
    }

    actual constructor(
        parent: CommonFile,
        child: String
    ) {
        fileFullPath = "${parent.fileFullPath}/$child"
    }

}

actual open class CommonInputStream{
    fun readAllBytes(): ByteArray {
        return ByteArray(0)
    }
}
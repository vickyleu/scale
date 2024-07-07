package com.jvziyaoyao.scale.image.sampling


actual class CommonFile : java.io.File {

    actual constructor(path: String) : super(path)

    actual constructor(parent: String, child: String) : super(parent, child)

    actual constructor(parent: CommonFile, child: String) : super(parent, child)

    actual override fun exists(): Boolean {
        return exists()
    }
}

actual open class CommonInputStream(val iss:java.io.InputStream){

}
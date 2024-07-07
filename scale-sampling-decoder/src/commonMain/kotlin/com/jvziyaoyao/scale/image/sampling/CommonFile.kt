package com.jvziyaoyao.scale.image.sampling

expect class  CommonFile{
    constructor(path: String)

    constructor(parent: String, child: String)

    constructor(parent: CommonFile, child: String)

    fun exists(): Boolean
}

expect open class CommonInputStream
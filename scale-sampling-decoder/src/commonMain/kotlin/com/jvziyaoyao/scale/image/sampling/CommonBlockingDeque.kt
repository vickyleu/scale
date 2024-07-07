package com.jvziyaoyao.scale.image.sampling

expect class CommonBlockingDeque<T>(){
    fun putLast(element: T)
    fun pollFirst(): T?
    fun pollLast(): T?
    fun takeFirst(): T
    fun takeLast(): T
    fun clear()
    fun putFirst(element: T)
    fun take(): T
    fun contains(block: T): Boolean
    fun remove(element: T)
}
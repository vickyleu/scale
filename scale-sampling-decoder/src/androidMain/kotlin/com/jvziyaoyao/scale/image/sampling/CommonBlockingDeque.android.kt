package com.jvziyaoyao.scale.image.sampling

import java.util.concurrent.LinkedBlockingDeque


actual class CommonBlockingDeque<T> {
    private val deque = LinkedBlockingDeque<T>()

    actual fun putFirst(element: T) {
        deque.putFirst(element)
    }

    actual fun putLast(element: T) {
        deque.putLast(element)
    }

    actual fun pollFirst(): T? {
        return deque.pollFirst()
    }

    actual fun pollLast(): T? {
        return deque.pollLast()
    }

    actual fun takeFirst(): T {
        return deque.takeFirst()
    }

    actual fun takeLast(): T {
        return deque.takeLast()
    }

    actual fun clear() {
        deque.clear()
    }

    actual fun take(): T {
        return deque.take()
    }

    actual fun contains(block: T): Boolean {
        return deque.contains(block)
    }

    actual fun remove(element: T) {
        deque.remove(element)
    }
}
package com.jvziyaoyao.scale.image.sampling

import kotlinx.atomicfu.locks.SynchronizedObject
import platform.Foundation.NSMutableArray
import platform.Foundation.NSThread
import platform.Foundation.removeAllObjects
import platform.Foundation.removeObject

@Suppress("unchecked_cast")
actual class CommonBlockingDeque<T>: SynchronizedObject() {
    private val deque: NSMutableArray = NSMutableArray()

    actual fun putFirst(element: T) {
        lock()
        try {
            deque.insertObject(element as Any, 0U)
        } finally {
            unlock()
        }
    }

    actual fun putLast(element: T) {
        lock()
        try {
            deque.addObject(element as Any)
        } finally {
            unlock()
        }
    }

    actual fun pollFirst(): T? {
        lock()
        return try {
            if (deque.count.toInt() == 0) {
                null
            } else {
                val firstElement = deque.objectAtIndex(0U)
                deque.removeObjectAtIndex(0U)
                firstElement as? T
            }
        } finally {
            unlock()
        }
    }

    actual fun pollLast(): T? {
        lock()
        return try {
            if (deque.count.toInt() == 0) {
                null
            } else {
                val lastIndex = deque.count.toInt() - 1
                val lastElement = deque.objectAtIndex(lastIndex.toULong())
                deque.removeObjectAtIndex(lastIndex.toULong())
                lastElement as? T
            }
        } finally {
            unlock()
        }
    }

    actual fun takeFirst(): T {
        lock()
        try {
            while (deque.count.toInt() == 0) {
                unlock()
                NSThread.sleepForTimeInterval(0.01)
                lock()
            }
            val firstElement = deque.objectAtIndex(0U)
            deque.removeObjectAtIndex(0U)
            return firstElement as T
        } finally {
            unlock()
        }
    }

    actual fun takeLast(): T {
        lock()
        try {
            while (deque.count.toInt() == 0) {
                unlock()
                NSThread.sleepForTimeInterval(0.01)
                lock()
            }
            val lastIndex = deque.count.toInt() - 1
            val lastElement = deque.objectAtIndex(lastIndex.toULong())
            deque.removeObjectAtIndex(lastIndex.toULong())
            return lastElement as T
        } finally {
            unlock()
        }
    }

    actual fun clear() {
        lock()
        try {
            deque.removeAllObjects()
        } finally {
            unlock()
        }
    }

    actual fun take(): T {
        return takeFirst()
    }

    actual fun contains(block: T): Boolean {
        for (i in 0 until deque.count.toInt()) {
            if (deque.objectAtIndex(i.toULong()) == block) {
                return true
            }
        }
        return false
    }

    actual fun remove(element: T) {
        deque.removeObject(element)
    }
}
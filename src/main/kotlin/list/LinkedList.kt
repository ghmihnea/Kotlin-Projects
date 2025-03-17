package org.jetbrains.kotlin.list

/**
 * Max points: 4
 */
class LinkedList<T>(/* TODO */) : MutableList<T> {
    /* TODO */

    // =========== Task L1 ===========

    override val size: Int
        get() = TODO("Not yet implemented")

    companion object {
        fun <T> from(vararg elements: T): LinkedList<T> {
            TODO("Not yet implemented")
        }
    }

    override fun get(index: Int): T {
        TODO("Not yet implemented")
    }

    override fun set(index: Int, element: T): T {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun addAll(elements: Collection<T>): Boolean {
        TODO("Not yet implemented")
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        TODO("Not yet implemented")
    }

    override fun add(index: Int, element: T) {
        TODO("Not yet implemented")
    }

    override fun add(element: T): Boolean {
        TODO("Not yet implemented")
    }

    // =========== Task L2 ===========

    override fun removeAt(index: Int): T {
        TODO("Not yet implemented")
    }

    override fun remove(element: T): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    // =========== Task L3 ===========

    override fun lastIndexOf(element: T): Int {
        TODO("Not yet implemented")
    }

    override fun indexOf(element: T): Int {
        TODO("Not yet implemented")
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        TODO("Not yet implemented")
    }

    override fun contains(element: T): Boolean {
        TODO("Not yet implemented")
    }

    // ==========================================
    // No need to implement the following methods
    // ==========================================

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        error("Not yet implemented")
    }

    override fun iterator(): MutableIterator<T> {
        return IteratorImpl()
    }

    override fun listIterator(): MutableListIterator<T> {
        error("Not yet implemented")
    }

    override fun listIterator(index: Int): MutableListIterator<T> {
        error("Not yet implemented")
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        error("Not yet implemented")
    }

    inner class IteratorImpl(private var index: Int = 0) : MutableListIterator<T> {
        override fun add(element: T) {
            error("Not yet implemented")
        }

        override fun hasNext(): Boolean {
            return this@LinkedList.size > index
        }

        override fun hasPrevious(): Boolean {
            error("Not yet implemented")
        }

        override fun next(): T {
            return this@LinkedList[index++]
        }

        override fun nextIndex(): Int {
            error("Not yet implemented")
        }

        override fun previous(): T {
            error("Not yet implemented")
        }

        override fun previousIndex(): Int {
            error("Not yet implemented")
        }

        override fun remove() {
            error("Not yet implemented")
        }

        override fun set(element: T) {
            error("Not yet implemented")
        }
    }
}

package org.jetbrains.kotlin.avltree

/**
 * Max points: 8
 */
class AvlTreeMap<K, V> : MutableMap<K, V> {
    /* TODO */

    // =========== Task A1 ===========

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = TODO("Not yet implemented")
    override val keys: MutableSet<K>
        get() = TODO("Not yet implemented")
    override val size: Int
        get() = TODO("Not yet implemented")
    override val values: MutableCollection<V>
        get() = TODO("Not yet implemented")

    companion object {
        fun <K : Comparable<K>, V> from(vararg pairs: Pair<K, V>): AvlTreeMap<K, V> {
            TODO("Not yet implemented")
        }

        fun <K, V> from(vararg pairs: Pair<K, V>, comparator: Comparator<K>): AvlTreeMap<K, V> {
            TODO("Not yet implemented")
        }
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun get(key: K): V? {
        TODO("Not yet implemented")
    }

    override fun putAll(from: Map<out K, V>) {
        TODO("Not yet implemented")
    }

    override fun put(key: K, value: V): V? {
        TODO("Not yet implemented")
    }

    // =========== Task A2 ===========

    override fun remove(key: K): V? {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun containsValue(value: V): Boolean {
        TODO("Not yet implemented")
    }

    override fun containsKey(key: K): Boolean {
        TODO("Not yet implemented")
    }
}
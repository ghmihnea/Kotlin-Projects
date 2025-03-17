package org.jetbrains.kotlin.hashmap

/**
 * Max points: 6
 */
class HashMap<K, V>(/* TODO */) : MutableMap<K, V> {
    /* TODO */

    // =========== Task H1 ===========

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = TODO("Not yet implemented")
    override val keys: MutableSet<K>
        get() = TODO("Not yet implemented")
    override val size: Int
        get() = TODO("Not yet implemented")
    override val values: MutableCollection<V>
        get() = TODO("Not yet implemented")

    companion object {
        fun <K, V> from(vararg pairs: Pair<K, V>): HashMap<K, V> {
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

    // =========== Task H2 ===========

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

    // =========== Task H3 ===========

    private fun rehash() {
        TODO("Not yet implemented")
    }
}
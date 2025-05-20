
package org.jetbrains.kotlin.hashmap

/**
 * Max points: 6
 */
class HashMap<K, V>(
    private var initialCapacity: Int = 32,
    private val loadFactor: Double = 1.0,
) : MutableMap<K, V> {
    private data class Entry<K, V>(
        override val key: K,
        override var value: V,
    ) : MutableMap.MutableEntry<K, V> {
        override fun setValue(newValue: V): V {
            val oldValue = value
            value = newValue
            return oldValue
        }
    }
    private var buckets: Array<MutableList<Entry<K, V>>> = Array(initialCapacity) { mutableListOf() }

    private var numberOfEntries : Int = 0

    // =========== Task H1 ===========

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = buckets.flatMap { it }.toMutableSet()
    override val keys: MutableSet<K>
        get() = entries.map { it.key }.toMutableSet()
    override val size: Int
        get() = numberOfEntries
    override val values: MutableCollection<V>
        get() = entries.map { it.value }.toMutableSet()

    companion object
    {
        fun <K, V> from(vararg pairs: Pair<K, V>): HashMap<K, V> {
            val map = HashMap<K, V>()
            map.putAll(pairs.toMap())
            return map
        }
    }

    override fun isEmpty(): Boolean {
        return numberOfEntries == 0
    }

    override fun get(key: K): V? {
        val index = key.hashCode().mod(buckets.size)
        for (entry in buckets[index]) {
            if (entry.key == key) return entry.value
        }
        return null
    }

    override fun putAll(from: Map<out K, V>) {
        for ((key, value) in from) {
            put(key, value)
        }
    }

    override fun put(key: K, value: V): V? {
        val i = (key?.hashCode() ?: 0) and 0x7fffffff % buckets.size
        for (entry in buckets[i]) {
            if (entry.key == key) {
                val oldValue = entry.value
                entry.setValue(value)
                return oldValue
            }
        }
        buckets[i].add(Entry(key, value))
        numberOfEntries++
        if (numberOfEntries > loadFactor * buckets.size) {
            rehash()
        }
        return null
    }

    // =========== Task H2 ===========

    override fun remove(key: K): V? {
        val i = (key.hashCode() and 0x7fffffff) % buckets.size
        val bucket = buckets[i]
        val iterator = bucket.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.key == key)
            {
                iterator.remove()
                numberOfEntries--
                return entry.value
            }
        }
        return null
    }

    override fun clear() {

        buckets = Array(initialCapacity) { mutableListOf() }
        numberOfEntries = 0
    }

    override fun containsValue(value: V): Boolean {
        return entries.any { it.value == value }
    }

    override fun containsKey(key: K): Boolean {
        return get(key) != null
    }

    // =========== Task H3 ===========

    private fun rehash() {
        val oldBuckets = buckets
        initialCapacity *= 2
        buckets = Array(initialCapacity) { mutableListOf() }
        numberOfEntries = 0
        for (bucket in oldBuckets) {
            for(entry in bucket) {
                put(entry.key, entry.value)
            }
        }
    }
}

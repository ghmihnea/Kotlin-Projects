package org.jetbrains.kotlin.hashmap

/**
 * Max points: 6
 */
class HashMap<K, V>(
    private var initialCapacity: Int = 32,
    /*initial number of buckets in the hash table*/
    private val loadFactor: Double = 1.0,
    /*determining when to resize the hash table*/
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
        /*returns a mutable set of all key-value pairs*/
        get() = buckets.flatMap { it }.toMutableSet()
    override val keys: MutableSet<K>
        get() = entries.map { it.key }.toMutableSet()
    override val size: Int
        get() = numberOfEntries
    override val values: MutableCollection<V>
        get() = entries.map { it.value }.toMutableSet()

    companion object
    /*helper function to create a HashMap from a series of key-value pairs*/
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
        val i = (key?.hashCode() ?: 0) and 0x7fffffff % buckets.size
        /*computing the index in the buckets array using the hashcode of the key*/
        for (entry in buckets[i]) {
            if (entry.key == key) return entry.value
        }
        return null
    }

    override fun putAll(from: Map<out K, V>) {
        for ((key, value) in from) {
            put(key, value)
            /*inserting each key,value pair into the current HashMap*/
        }
    }

    override fun put(key: K, value: V): V? {
        val i = (key?.hashCode() ?: 0) and 0x7fffffff % buckets.size
        for (entry in buckets[i]) {
            if (entry.key == key) {
                val oldValue = entry.value
                entry.setValue(value)
                return oldValue
                /*return the old value, as it was replaced with the new one*/
            }
        }
        buckets[i].add(Entry(key, value))
        /*if the key does not exist we create a new entry and add it to
        the corresponding bucket*/
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
            /*if a matching key is found we remove the entry*/
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
        /*reinitializing the buckets array with empty lists for each bucket*/
        numberOfEntries = 0
    }

    override fun containsValue(value: V): Boolean {
        return entries.any { it.value == value }
        /*iterating over the entries in the map and checking if any
        entry has the specified value*/
    }

    override fun containsKey(key: K): Boolean {
        return get(key) != null
    }

    // =========== Task H3 ===========

    private fun rehash() {
    val oldBuckets = buckets
        initialCapacity *= 2
        /*we double the initial capacity to increase the size of the hash
        table and avoid collisions*/
        buckets = Array(initialCapacity) { mutableListOf() }
        numberOfEntries = 0
        for (bucket in oldBuckets) {
            for(entry in bucket) {
                put(entry.key, entry.value)
            }
        }
    }
}
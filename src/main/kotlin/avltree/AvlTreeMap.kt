package org.jetbrains.kotlin.avltree

/**
 * Max points: 8
 */
class AvlTreeMap<K, V> : MutableMap<K, V> {
    private var root: AvlTreeNode<K, V>? = null
    private var _size = 0

    private var comparator: Comparator<in K>? = null

    constructor()

    constructor(comparator: Comparator<K>) {
        this.comparator = comparator
    }

    private fun compare(k1: K, k2: K): Int {
        return comparator?.compare(k1, k2)
            ?: when {
                k1 is Comparable<*> && k2 is Comparable<*> -> {
                    @Suppress("UNCHECKED_CAST") // see below for how to remove this too
                    (k1 as Comparable<Any>).compareTo(k2 as Any)
                }
                else -> throw IllegalStateException("Keys must be Comparable or a Comparator must be provided.")
            }
    }

    // =========== Task A1 ===========

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = buildSet {
            fun traverse(node: AvlTreeNode<K, V>?) {
                if (node == null) return
                traverse(node.left)
                add(object : MutableMap.MutableEntry<K, V> {
                    override val key = node.key
                    override var value: V = node.value

                    override fun setValue(newValue: V): V {
                        val old = value
                        node.value = newValue
                        value = newValue
                        return old
                    }
                })
                traverse(node.right)
            }
            traverse(root)
        }.toMutableSet()

    override val keys: MutableSet<K>
        get() = entries.mapTo(mutableSetOf()) { it.key }
    override val size: Int
        get() = _size
    override val values: MutableCollection<V>
        get() = entries.mapTo(mutableListOf()) { it.value }

    companion object {
        fun <K : Comparable<K>, V> from(vararg pairs: Pair<K, V>): AvlTreeMap<K, V> {
            val map = AvlTreeMap<K, V>()
            for ((k, v) in pairs) {
                map[k] = v
            }
            return map
        }

        fun <K, V> from(vararg pairs: Pair<K, V>, comparator: Comparator<K>): AvlTreeMap<K, V> {
            val map = AvlTreeMap<K, V>(comparator)
            for ((k, v) in pairs) {
                map[k] = v
            }
            return map
        }
    }

    override fun isEmpty(): Boolean {
        return size == 0
    }

    override fun get(key: K): V? {
        var current = root
        while (current != null) {
            val cmp = compare(key, current.key)
            current = when {
                cmp < 0 -> current.left
                cmp > 0 -> current.right
                else -> return current.value
            }
        }
        return null
    }
    override fun putAll(from: Map<out K, V>) {
        for ((k, v) in from) {
            put(k, v)
        }
    }

    override fun put(key: K, value: V): V? {
        var old: V? = null
        root = insert(root, key, value) { old = it }
        return old
    }

    // =========== Task A2 ===========

    override fun remove(key: K): V? {
        var old: V? = null
        root = delete(root, key) { old = it }
        if(old != null) _size--
        return old
    }

    override fun clear() {
        root = null
        _size = 0    }

    override fun containsValue(value: V): Boolean {
        return entries.any { it.value == value }
    }

    override fun containsKey(key: K): Boolean {
        return get(key) != null
    }
    private fun height(node: AvlTreeNode<K, V>?): Int = node?.height ?: 0

    private fun balanceFactor(node: AvlTreeNode<K, V>): Int =
        height(node.left) - height(node.right)

    private fun rotateLeft(x: AvlTreeNode<K, V>): AvlTreeNode<K, V> {
        val y = x.right!!
        x.right = y.left
        y.left = x
        x.height = 1 + maxOf(height(x.left), height(x.right))
        y.height = 1 + maxOf(height(y.left), height(y.right))
        return y
    }

    private fun rotateRight(y: AvlTreeNode<K, V>): AvlTreeNode<K, V> {
        val x = y.left!!
        y.left = x.right
        x.right = y
        y.height = 1 + maxOf(height(y.left), height(y.right))
        x.height = 1 + maxOf(height(x.left), height(x.right))
        return x
    }
    private fun balance(node: AvlTreeNode<K, V>): AvlTreeNode<K, V> {
        val balance = balanceFactor(node)
        return when {
            balance > 1 && balanceFactor(node.left!!) >= 0 -> rotateRight(node)
            balance > 1 -> {
                node.left = rotateLeft(node.left!!)
                rotateRight(node)
            }
            balance < -1 && balanceFactor(node.right!!) <= 0 -> rotateLeft(node)
            balance < -1 -> {
                node.right = rotateRight(node.right!!)
                rotateLeft(node)
            }
            else -> {
                node.height = 1 + maxOf(height(node.left), height(node.right))
                node
            }
        }
    }
    private fun insert(
        node: AvlTreeNode<K, V>?,
        key: K,
        value: V,
        oldValue: (V) -> Unit
    ): AvlTreeNode<K, V> {
        if (node == null) {
            _size++
            return AvlTreeNode(key, value)
        }
        val cmp = compare(key, node.key)
        when {
            cmp < 0 -> node.left = insert(node.left, key, value, oldValue)
            cmp > 0 -> node.right = insert(node.right, key, value, oldValue)
            else -> {
                oldValue(node.value)
                node.value = value
                return node
            }
        }

        return balance(node)
    }

    private fun delete(
        node: AvlTreeNode<K, V>?,
        key: K,
        oldValue: (V) -> Unit
    ): AvlTreeNode<K, V>? {
        if (node == null) return null

        val cmp = compare(key, node.key)
        when {
            cmp < 0 -> node.left = delete(node.left, key, oldValue)
            cmp > 0 -> node.right = delete(node.right, key, oldValue)
            else -> {
                oldValue(node.value)
                if (node.left == null) return node.right
                if (node.right == null) return node.left
                val min = minValueNode(node.right!!)
                node.key = min.key
                node.value = min.value
                node.right = delete(node.right, min.key) {}
            }
        }

        return balance(node)
    }

    private fun minValueNode(node: AvlTreeNode<K, V>): AvlTreeNode<K, V> {
        var current = node
        while (current.left != null) current = current.left!!
        return current
    }
}


package org.jetbrains.kotlin.avltree

internal class AvlTreeNode<K, V> (
    var key: K,
    var value: V,
    var left: AvlTreeNode<K, V>? = null,
    var right: AvlTreeNode<K, V>? = null,
    var height: Int = 1
    )
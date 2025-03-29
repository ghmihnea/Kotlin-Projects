package org.jetbrains.kotlin.list

internal class LinkedListNode<T>(
    var data: T,
    var next: LinkedListNode<T>? = null,
    var prev: LinkedListNode<T>? = null
)

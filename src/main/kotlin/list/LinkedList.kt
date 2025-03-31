package org.jetbrains.kotlin.list

/**
 * Max points: 4
 */
class LinkedList<T> : MutableList<T> {

    private var head: LinkedListNode<T>? = null
    private var tail: LinkedListNode<T>? = null


    private var length = 0

    class BrokenInvariantException(message: String) : RuntimeException(message)
    /*exception for handling cases when the list s unexpectedly broken*/

    // =========== Task L1 ===========

    override val size: Int
        get() = length

    companion object
    /*helper function to create LinkedList from vararg elements*/
    {
        fun <T> from(vararg elements: T): LinkedList<T> {
            val list = LinkedList<T>()/*creating a new LinkedList instance*/
            list.addAll(elements.toList())/*adding all elements to the list*/
            return list
        }
    }

    override fun get(index: Int): T {
        if (index < 0 || index >= size) throw IndexOutOfBoundsException("Index $index out of bounds for length $length")
        var currentNode = head
        for (i in 0 until index) {
            currentNode = currentNode?.next
        }
        return currentNode?.data ?: throw BrokenInvariantException("Failed to get element at index $index")
    }

    override fun set(index: Int, element: T): T {
        if (index < 0 || index >= size) throw IndexOutOfBoundsException("Index $index out of bounds for length $length")
        var currentNode = head
        for (i in 0 until index) {
            currentNode = currentNode?.next
        }
        val oldValue = currentNode?.data ?: throw BrokenInvariantException("Failed to get element at index $index")
        currentNode.data = element
        return oldValue
    }

    override fun isEmpty(): Boolean = length == 0

    override fun addAll(elements: Collection<T>): Boolean
    /*adding all elements from a collection to the list*/
    {
        var added = false
        for (element in elements) {
            add(element)/*adding each element individually*/
            added = true
        }
        return added
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        if (index < 0 || index > size) throw IndexOutOfBoundsException()

        var currentNode = head
        var currentI = 0

        while (currentNode !=null && currentI < index) {
            currentNode = currentNode.next/*moving right before the specified index*/
            currentI++
        }

        for (element in elements)
        /*inserting the new nodes*/
        {
            val newNode = LinkedListNode(element)

            if (currentNode == null) {
                if (tail == null) {
                    head = newNode
                    tail = newNode
                    /*setting the new node as the head and tail
                    if the list is empty*/
                } else {
                    tail?.next = newNode
                    newNode.prev = tail
                    tail = newNode
                }
            } else
            /*if inserting randomly*/
            {
                newNode.next = currentNode
                newNode.prev = currentNode.prev
                currentNode.prev?.next = newNode
                currentNode.prev = newNode
                /*we interlink the current node with the new one*/

                if (currentNode == head) {
                    head = newNode
                }
            }
            currentNode = newNode.next
        }
        length += elements.size

        return true
    }

    override fun add(index: Int, element: T)
    /*adds an element at a specific index*/
    {
        if (index < 0 || index > size) throw IndexOutOfBoundsException("Index: $index, Size: $size")
        if (index == size) {
            add(element)
            /*add at the end of the list*/
        } else {
            var currentNode = head
            for(i in 0 until index) {
                currentNode = currentNode?.next
            }
            val newNode = LinkedListNode(element, currentNode, currentNode?.prev)
            currentNode?.prev?.next = newNode
            currentNode?.prev = newNode
            if (index == 0) head = newNode
            length++
        }
    }

    override fun add(element: T): Boolean
    /*adds an element at the end of the list*/
    {
        val newNode = LinkedListNode(element)
        if (tail == null) {
            head = newNode
            tail = newNode
        } else {
            tail?.next = newNode
            newNode.prev = tail
            tail = newNode
        }
        length++
        return true
    }

    // =========== Task L2 ===========

    private fun removeNode(node: LinkedListNode<T>?)
    /*helper function introduced to improve readability for
    removeAt and remove*/
    {
        if (node == null) return

        if(node == head && node == tail) {
            head = null
            tail = null
        } else if (node == head) {
            head = node.next
            head?.prev = null
        } else if (node == tail) {
            tail = node.prev
            tail?.next = null
        } else {
            node.prev?.next = node.next
            node.next?.prev = node.prev
        }
    }
    override fun removeAt(index: Int): T {
        if (index < 0 || index >= size) throw IndexOutOfBoundsException()
        var currentNode = head
        for (i in 0 until index) {
            currentNode = currentNode?.next
            /*go through the list until reaching the specified index*/
        }
        val value = currentNode?.data ?: throw BrokenInvariantException("Failed to get element at index $index")

        removeNode(currentNode)

        length--
        return value
    }
    override fun remove(element: T): Boolean
    /*removes the first occurrence of a specified element*/
    {
        var currentNode = head
        var removed = false

        while (currentNode != null) {
            val nextNode = currentNode.next
            if (currentNode.data == element) {
                removeNode(currentNode)
                length--
                removed = true
                break
            }
            currentNode = nextNode
        }
        return removed
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        /*removes all occurrences of the elements in the given collection*/
        if(isEmpty()) return false

        val toRemove = elements.toSet()
        /*converting the collection to a set for a faster lookup
        during comparison*/
        var currentNode = head
        var removed = false

        while (currentNode != null) {
            val nextNode = currentNode.next
            if(currentNode.data in toRemove) {
                if(currentNode == head) {
                    head = nextNode
                    head?.prev = null
                } else {
                    currentNode.prev?.next = nextNode
                    /*skipping the current node by linking
                   the previous node to the next one*/
                }
                if(currentNode == tail) {
                    tail = currentNode.prev
                    tail?.next = null
                } else {
                    nextNode?.prev = currentNode.prev
                    /*updating the next node's previous pointer
                    to skip the current node*/
                }

                length --
                removed = true
            }
            currentNode = nextNode
        }
        return removed
    }

    override fun clear() {
        head = null
        tail = null
        length = 0
    }

    // =========== Task L3 ===========

    override fun lastIndexOf(element: T): Int {
        var currentNode = tail
        var i = length - 1
        /*initialize the index to the last position*/
        while (currentNode != null)
        /*traversing the list backwards*/
        {
            if (currentNode.data == element) return i
            currentNode = currentNode.prev
            i --
        }
        return -1
    }

    override fun indexOf(element: T): Int {
        var currentNode = head
        var i = 0
        while (currentNode != null) {
            if (currentNode.data == element) return i
            currentNode = currentNode.next
            i++
        }
        return -1
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        for (element in elements) {
            if (!contains(element)) return false
            /*if any element is not found in the linked list, return false*/
        }
        return true
    }

    override fun contains(element: T): Boolean {
        var currentNode = head
        while (currentNode != null) {
            if (currentNode.data == element) return true
            currentNode = currentNode.next
        }
        return false
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

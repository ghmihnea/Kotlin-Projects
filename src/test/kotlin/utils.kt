import org.jetbrains.kotlin.avltree.AvlTreeMap
import org.jetbrains.kotlin.hashmap.HashMap
import org.jetbrains.kotlin.list.LinkedList
import org.junit.jupiter.api.Assertions.assertEquals

typealias MapOperation<K, V, R> = (MutableMap<K, V>.() -> R)

enum class MapImpl {
    HASH,
    AVL,
    ORACLE
}

fun <K, V> assertEqualsMap(expected: Map<K, V>, actual: Map<K, V>, message: String? = null) {
    assertEquals(expected.isEmpty(), actual.isEmpty(), message)
    assertEquals(expected.size, actual.size, message)
    assertEquals(expected, actual, message)
}

val mapImpl = when (System.getProperty("mapUnderTest")) {
    "hash" -> MapImpl.HASH
    "avl" -> MapImpl.AVL
    else -> MapImpl.ORACLE
}

inline fun <reified K, reified V> runMapWithOracle(
    initial: List<Pair<K, V>>,
    vararg operations: MapOperation<K, V, *>
) = runMapWithOracle(
    initial = initial,
    mapUnderTest = mapImpl,
    operations = operations
)

inline fun <reified K, reified V> runMapWithOracle(
    initial: List<Pair<K, V>>,
    mapUnderTest: MapImpl,
    vararg operations: MapOperation<K, V, *>
) = runMapWithOracle(
    oracle = mutableMapOf(*initial.toTypedArray()),
    listUnderTest = buildMap(*initial.toTypedArray(), mapUnderTest = mapUnderTest),
    operations = operations
)

fun <K, V> runMapWithOracle(
    oracle: MutableMap<K, V>,
    listUnderTest: MutableMap<K, V>,
    vararg operations: MapOperation<K, V, *>
) {
    assertEqualsMap(oracle, listUnderTest)
    for ((index, operation) in operations.withIndex()) {
        val oracleResult = try {
            oracle.operation()
        } catch (e: Throwable) {
            ErrorMessage(e.message)
        }
        val testResult = if (oracleResult is ErrorMessage) {
            try {
                listUnderTest.operation()
            } catch (e: Throwable) {
                ErrorMessage(e.message)
            }
        } else {
            listUnderTest.operation()
        }
        assertEquals(oracleResult, testResult, "operation@[$index]: Return value mismatch")
        assertEqualsMap(oracle, listUnderTest, "operation@[$index]: Resulting map is not equal to the oracle")
    }
}

fun <K, V> buildMap(vararg pairs: Pair<K, V>, mapUnderTest: MapImpl): MutableMap<K, V> {
    return when (mapUnderTest) {
        MapImpl.HASH -> HashMap.from(*pairs).also {
            assertEqualsMap(pairs.toMap(), it, "Constructed map is not equal to the expected map")
        }

        MapImpl.AVL -> AvlTreeMap.from(*pairs).also {
            assertEqualsMap(pairs.toMap(), it, "Constructed map is not equal to the expected map")
        }

        MapImpl.ORACLE -> mutableMapOf(*pairs)
    }
}


typealias ListOperation<T, R> = (MutableList<T>.() -> R)

enum class ListImpl {
    LINKED,
    ORACLE
}

fun <T> assertEqualsList(expected: List<T>, actual: List<T>, message: String? = null) {
    assertEquals(expected.isEmpty(), actual.isEmpty(), message)
    assertEquals(expected.size, actual.size, message)
    assertEquals(expected, actual, message)
}

val listImpl = when (System.getProperty("listUnderTest")) {
    "linked" -> ListImpl.LINKED
    else -> ListImpl.ORACLE
}

inline fun <reified T> runListWithOracle(
    initial: List<T>,
    vararg operations: ListOperation<T, *>
) = runListWithOracle(initial, listImpl, *operations)

inline fun <reified T> runListWithOracle(
    initial: List<T>,
    listImpl: ListImpl,
    vararg operations: ListOperation<T, *>
) = runListWithOracle(
    mutableListOf(*initial.toTypedArray()),
    buildList(*initial.toTypedArray(), listUnderTest = listImpl),
    *operations
)

fun <T> runListWithOracle(
    oracle: MutableList<T>,
    listUnderTest: MutableList<T>,
    vararg operations: ListOperation<T, *>
) {
    assertEqualsList(oracle, listUnderTest)
    for ((index, operation) in operations.withIndex()) {
        val oracleResult = try {
            oracle.operation()
        } catch (e: Throwable) {
            ErrorMessage(e.message)
        }
        val testResult = if (oracleResult is ErrorMessage) {
            try {
                listUnderTest.operation()
            } catch (e: Throwable) {
                ErrorMessage(e.message)
            }
        } else {
            listUnderTest.operation()
        }
        assertEquals(oracleResult, testResult, "operation@[$index]: Return value mismatch")
        assertEqualsList(oracle, listUnderTest, "operation@[$index]: Resulting list is not equal to the oracle")
    }
}

fun <T> buildList(vararg elements: T, listUnderTest: ListImpl): MutableList<T> {
    return when (listUnderTest) {
        ListImpl.LINKED -> LinkedList.from(*elements).also {
            assertEqualsList(elements.toList(), it, "Constructed list is not equal to the expected list")
        }

        ListImpl.ORACLE -> mutableListOf(*elements)
    }
}

data class ErrorMessage(val msg: String?)

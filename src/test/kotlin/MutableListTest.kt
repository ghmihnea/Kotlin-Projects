import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

class MutableListTest {

    @Tag("L1")
    @Test
    fun testAddGetAndSet() {
        runListWithOracle<Int>(listOf(),
            { add(10) },
            { add(20) },
            { get(-1) },
            { get(0) },
            { get(1) },
            { set(1, 2) },
            { set(0, 3) },
            { set(Int.MAX_VALUE, 5) },
            { add(1, 30) },
            { add(7, 30) },
            { add(0, 0) },
            { addAll(2, listOf(1, 2, 3)) },
            { addAll(listOf()) }
        )
    }

    @Tag("L2")
    @Test
    fun testRemoveElements() {
        runListWithOracle(
            listOf(3, 4, 5, 6, 7, 8, 9),
            { remove(6) },
            { remove(6) },
            { removeAt(1) },
            { removeAt(0) },
            { removeAll(listOf(6, 7, 9)) },
            { removeAll(listOf(8, 5)) },
        )
    }

    @Tag("L2")
    @Test
    fun testIntegration() {
        runListWithOracle(
            listOf(3, 4, 5, 6, 7, 8, 9),
            { addAll(listOf(7, 8, 9)) },
            { addAll(0, listOf(3, 4, 5)) },
            { add(3, 6) },
            { set(0, 5) },
            { get(0) },
            { get(-1) },
            { set(0, 3) },
            { remove(6) },
            { remove(6) },
           /* { add(3, 6) },
            { get(3) },
            { removeAt(1) },
            { add(10) },
            { removeAt(0) },
            { removeAll(listOf(6, 7, 9)) },
            { removeAll(listOf(8, 5)) },
            { clear() },
            { add(6) },
            { set(size, 6) },
            { set(0, 5) },
            { get(0) }, */
        )
    }

    @Tag("L3")
    @Test
    fun testObservingOperations() {
        runListWithOracle(
            listOf(1, 2, 3, 3, 4, 7, 2, 5, 6, 7),
            { indexOf(1) },
            { indexOf(2) },
            { indexOf(3) },
            { indexOf(7) },
            { indexOf(10) },
            { lastIndexOf(1) },
            { lastIndexOf(2) },
            { lastIndexOf(3) },
            { lastIndexOf(7) },
            { lastIndexOf(10) },
            { contains(1) },
            { contains(3) },
            { contains(11) },
            { containsAll(listOf(1, 2)) },
            { containsAll(listOf(0, 1, 2)) },
        )
    }
}

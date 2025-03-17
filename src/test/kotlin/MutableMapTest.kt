import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

class MutableMapTest {
    @Tag("H1")
    @Tag("A1")
    @Test
    fun testAddGetAndPut() {
        runMapWithOracle<Int, String>(
            listOf(),
            { entries },
            { put(1, "one") },
            { put(2, "two") },
            { get(1) },
            { get(2) },
            { put(1, "uno") },
            { get(1) },
            { put(3, "three") },
            { putAll(mapOf(4 to "four", 5 to "five")) },
            { get(4) },
            { values.map { it } },
            { keys },
            { entries },
        )
    }

    @Tag("H3")
    @Tag("A1")
    @Test
    fun testALotOfPut() {
        runMapWithOracle<Int, String>(
            listOf(),
            {
                for (i in (1..10000).shuffled()) {
                    put(i, "value$i")
                }
            },
        )
    }

    @Tag("H2")
    @Tag("A2")
    @Test
    fun testRemoveElements() {
        runMapWithOracle(
            listOf(1 to "one", 2 to "two", 3 to "three"),
            { containsKey(3) },
            { containsValue("four") },
            { remove(2) },
            { entries },
            { remove(4) },
            { clear() },
            { isEmpty() }
        )
    }

    @Tag("H2")
    @Tag("A2")
    @Test
    fun testIntegration() {
        runMapWithOracle(
            listOf(1 to "one", 2 to "two"),
            { values.map { it } },
            { keys },
            { entries },
            { put(3, "three") },
            { put(4, "four") },
            { containsKey(3) },
            { containsValue("four") },
            { remove(1) },
            { put(5, "five") },
            { put(2, "deux") },
            { putAll(mapOf(6 to "six", 7 to "seven")) },
            { values.map { it } },
            { keys },
            { entries },
            { get(7) },
            { clear() },
            { isEmpty() }
        )
    }

    @Tag("H3")
    @Tag("A2")
    @Test
    fun testALotOfData() {
        runMapWithOracle<Int, String>(
            listOf(),
            {
                for (i in (1..10000).shuffled()) {
                    put(i, "value$i")
                }
            },
            {
                for (i in (1..5000).shuffled()) {
                    remove(i)
                }
            },
            {
                for (i in (5001..10000).shuffled()) {
                    remove(i)
                }
            }
        )
    }
}

import org.jetbrains.edu.kotlin.wikirace.WikiPath
import org.jetbrains.edu.kotlin.wikirace.WikiRacer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class RacingTest {
    @ParameterizedTest
    @MethodSource("searchTestData")
    fun searchTestSingleThread(expectedPathSize: Int, startPage: String, expectedPath: List<String>) {
        val searchResult = WikiRacer.get(1).race(startPage, KOTLIN_PAGE, 2)
        assertEquals(expectedPathSize, searchResult.steps, "From $startPage to $KOTLIN_PAGE $expectedPathSize steps")
        expectedPath.zip(searchResult.path).forEachIndexed { index, (expected, actual) ->
            assertEquals(
                expected,
                actual,
                "The $index-th item in the path from $startPage to $KOTLIN_PAGE is $expected, but was $actual"
            )
        }
    }

    companion object {
        private const val AVL_PAGE = "AVL tree"
        private const val COMPARISON_PAGE = "Comparison of programming languages"
        private const val JETBRAINS_PAGE = "JetBrains"
        private const val JVM_PAGE = "Java virtual machine"
        private const val KOTLIN_PAGE = "Kotlin (programming language)"

        @JvmStatic
        fun searchTestData() = listOf(
            Arguments.of(0, KOTLIN_PAGE, listOf(KOTLIN_PAGE)),
            Arguments.of(1, COMPARISON_PAGE, listOf(COMPARISON_PAGE, KOTLIN_PAGE)),
            Arguments.of(1, JETBRAINS_PAGE, listOf(JETBRAINS_PAGE, KOTLIN_PAGE)),
            Arguments.of(1, JVM_PAGE, listOf(JVM_PAGE, KOTLIN_PAGE)),
            Arguments.of(2, AVL_PAGE, listOf(AVL_PAGE, "Tail_call", KOTLIN_PAGE)),
            Arguments.of(WikiPath.NOT_FOUND.steps, "Bremen", WikiPath.NOT_FOUND.path),
        )
    }
}

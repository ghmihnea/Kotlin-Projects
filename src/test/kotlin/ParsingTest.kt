import org.jetbrains.edu.kotlin.wikirace.WikiRacer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class ParsingTest {
    @ParameterizedTest
    @MethodSource("referencesData")
    fun referencesExtractorTest(url: String, expectedReferences: List<String>, referencesNumber: Int) {
        val wikiRacer = WikiRacer.get(1)
        val references = wikiRacer.getReferences(url)
        assertEquals(
            referencesNumber,
            references.size,
            "For the url: $url you need to extract $referencesNumber references."
        )
        expectedReferences.forEach {
            assertTrue(it in references, "The reference: $it must be found by the $url url.")
        }
    }

    companion object {
        @JvmStatic
        fun referencesData() = listOf(
            Arguments.of(
                "Kotlin (programming language) aaaaa",
                emptyList<String>(),
                0,
            ),
            Arguments.of(
                "Kotlin (programming language)",
                listOf(
                    "James_Gosling",
                    "Java_Community_Process",
                    "JetBrains",
                ),
                229
            ),
        )
    }
}

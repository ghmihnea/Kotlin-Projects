import java.util.*

plugins {
    kotlin("jvm") version "2.1.10"
}

group = "org.edu.jvm.languages"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

private fun Double.format() = String.format(Locale.ENGLISH, "%.2f", this)

abstract class RunTestsWithTag : Test() {
    private fun Double.format() = String.format(Locale.ENGLISH, "%.2f", this)

    @get:Input
    var tagName: String = ""

    @get:Input
    var points = 1.0

    override fun executeTests() {
        useJUnitPlatform {
            includeTags(tagName)
        }

        addTestListener(object : TestListener {
            override fun beforeSuite(suite: TestDescriptor) {}
            override fun beforeTest(testDescriptor: TestDescriptor) {}
            override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
            override fun afterSuite(suite: TestDescriptor, result: TestResult) {
                if (suite.parent == null) {
                    val fraction = if (result.testCount > 0) {
                        result.successfulTestCount.toDouble() / result.testCount.toDouble()
                    } else 0.0
                    val earnedPoints = points * fraction

                    println("Finished running ${result.testCount} tests (${result.successfulTestCount} successful, ${result.failedTestCount} failed, ${result.skippedTestCount} skipped)")
                    println("Points earned: ${earnedPoints.format()}/${points.format()}")

                    // Store earned points in task's extra property
                    project.extra["points_$tagName"] = earnedPoints
                }
            }
        })

        super.executeTests()
    }
}

val correctnessWeight = 0.6

val tagsToPoints = mapOf(
    "L1" to 1.0,
    "L2" to 2.0,
    "L3" to 1.0,
    "H1" to 2.0,
    "H2" to 2.0,
    "H3" to 2.0,
    "A1" to 4.0,
    "A2" to 4.0,
).mapValues { it.value * correctnessWeight }

val listTags = tagsToPoints.filter { it.key.startsWith("L") }
val hashMapTags = tagsToPoints.filter { it.key.startsWith("H") }
val avlMapTags = tagsToPoints.filter { it.key.startsWith("A") }

val useOracle = project.properties["useOracle"]?.toString()?.toBoolean() ?: false
val ignoreTags = project.properties["ignoreTags"]?.toString()?.split(",") ?: emptyList()

fun registerTagGroupTask(
    taskName: String,
    className: String,
    systemPropertyName: String,
    nonOracleValue: String,
    tagGroup: Map<String, Double>
) {
    val filteredTagGroup = tagGroup.filterKeys { it !in ignoreTags }

    tagGroup.forEach { (tag, maxPoints) ->
        tasks.register("test$tag", RunTestsWithTag::class) {
            tagName = tag
            points = maxPoints

            ignoreFailures = true
            inputs.property("ignoreTags", ignoreTags.joinToString(","))
            inputs.property("useOracle", useOracle.toString())
            systemProperty(systemPropertyName, if (useOracle) "oracle" else nonOracleValue)
        }
    }

    tasks.register(taskName, Test::class) {
        inputs.property("ignoreTags", ignoreTags.joinToString(","))
        inputs.property("useOracle", useOracle.toString())

        val testTasks = filteredTagGroup.keys.map { "test$it" }.toTypedArray()

        dependsOn(testTasks)

        doLast {
            val totalPoints = filteredTagGroup.keys.sumOf { project.extra["points_$it"] as? Double ?: .0 }
            val expectedPoints = filteredTagGroup.values.sum()
            val resultNote = "===== Total correctness points for ${className}: ${totalPoints.format()}/${expectedPoints.format()} ====="
            println(
                """
                ${"=".repeat(resultNote.length)}
                $resultNote
                ${"=".repeat(resultNote.length)}
            """.trimIndent()
            )

            if (totalPoints < expectedPoints) {
                throw TestExecutionException("Total correctness points for $className is less than expected: ${totalPoints.format()}/${expectedPoints.format()}")
            }
        }
    }
}

registerTagGroupTask("testList", "LinkedList", "listUnderTest", "linked", listTags)
registerTagGroupTask("testHashMap", "HashMap", "mapUnderTest", "hash", hashMapTags)
registerTagGroupTask("testAvlMap", "AvlTreeMap", "mapUnderTest", "avl", avlMapTags)

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
package org.jetbrains.edu.kotlin.wikirace

import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors

class WikiRacerImpl(private val maxThreads: Int) : WikiRacer {
    val forbiddenPrefixes = listOf(
        "File:",
        "Wikipedia:",
        "Help:",
        "Template:",
        "Category:",
        "Special:",
        "Portal:",
        "User:",
        "MediaWiki:",
        "Draft:",
        "TimedText:",
        "Module:",
        "Media:",
        "Template_talk:",
        "Talk:"
    )

    override fun getReferences(page: String): List<String> {
        val formattedPage = page.replace(" ", "_")
        val fullUrl = "https://en.wikipedia.org/wiki/$formattedPage"
        return try {
            val doc = Jsoup.connect(fullUrl)
                .userAgent("Mozilla/5.0 (WikiRacerBot; +https://example.com)")
                .get()

            println("Loaded page: $fullUrl")


            doc.select("[href^=/wiki/]")
                .mapNotNull { element ->
                    val link = element.attr("href").removePrefix("/wiki/")
                        .split('#')
                        .first()
                    if (
                        link != formattedPage &&
                        link != "Main_Page" &&
                        forbiddenPrefixes.none { link.startsWith(it) }
                    ) link else null
                }.distinct()
        } catch (ex: HttpStatusException) {
            println("Error fetching $fullUrl: ${ex.message}")
            emptyList()
        }
    }

    override fun race(startPage: String, destinationPage: String, searchDepth: Int): WikiPath {
        val start = startPage.replace(' ', '_')
        val end = destinationPage.replace(' ', '_')

        if (start == end) return WikiPath(listOf(start))
        if (searchDepth <= 0) return WikiPath.NOT_FOUND

        val visited = ConcurrentHashMap.newKeySet<String>()
        val toVisitQueue = ConcurrentLinkedQueue<Pair<String, List<String>>>()
        val executor = Executors.newFixedThreadPool(maxThreads)

        toVisitQueue.add(start to listOf(start))
        visited.add(start)

        for (depth in 1..searchDepth) {
            val nextLevel = ConcurrentLinkedQueue<Pair<String, List<String>>>()
            val tasks = buildSearchTasks(toVisitQueue, visited, nextLevel, end)

            try {
                val results = executor.invokeAll(tasks)
                for (result in results) {
                    val path = result.get() ?: continue
                    executor.shutdownNow()
                    return path
                }
            } catch (e: InterruptedException) {
                println("Search interrupted: ${e.message}")
                break
            }

            toVisitQueue.addAll(nextLevel)
            println("Depth $depth: Visiting ${visited.size} pages...")
        }

        executor.shutdown()
        return WikiPath.NOT_FOUND
    }

    private fun buildSearchTasks(
        toVisitQueue: ConcurrentLinkedQueue<Pair<String, List<String>>>,
        visited: MutableSet<String>,
        nextLevel: ConcurrentLinkedQueue<Pair<String, List<String>>>,
        destination: String
    ): List<Callable<WikiPath?>> {
        val tasks = mutableListOf<Callable<WikiPath?>>()

        while (toVisitQueue.isNotEmpty()) {
            val (currentPage, path) = toVisitQueue.poll()
            tasks.add(Callable {
                val references = getReferences(currentPage)
                for (link in references) {
                    if (link == destination) return@Callable WikiPath(path + link)
                    if (visited.add(link)) {
                        nextLevel.add(link to (path + link))
                    }
                }
                null
            })
        }

        return tasks
    }
}

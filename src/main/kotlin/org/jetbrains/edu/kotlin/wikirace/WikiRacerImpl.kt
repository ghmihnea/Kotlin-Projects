package org.jetbrains.edu.kotlin.wikirace

import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import java.util.concurrent.*

typealias PagePath = Pair<String, List<String>>
typealias Task = Callable<WikiPath?>
typealias WikiPathFuture = Future<WikiPath?>
typealias TaskResultList = List<WikiPathFuture>
typealias SearchSetup = Triple<ConcurrentLinkedQueue<PagePath>, MutableSet<String>, ExecutorService>

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
                    val link = element.attr("href")
                        .removePrefix("/wiki/")
                        .split('#')
                        .first()

                    link.takeIf {
                        it.isNotBlank() && it != formattedPage && it != "Main_Page" && forbiddenPrefixes.none { prefix ->
                            it.startsWith(
                                prefix
                            )
                        }
                    }
                }.distinct()
        } catch (ex: HttpStatusException) {
            println("Error fetching $fullUrl: ${ex.message}")
            emptyList()
        }
    }

    override fun race(startPage: String, destinationPage: String, searchDepth: Int): WikiPath {
        val start = startPage.replace(' ', '_')
        val end = destinationPage.replace(' ', '_')

        if (start == end) {
            return WikiPath(listOf(start))
        }
        if (searchDepth <= 0) {
            return WikiPath.NOT_FOUND
        }

        val (toVisitQueue, visited, executor) = prepareSearch(start)

        for (depth in 1..searchDepth) {
            val foundPath = searchDepthLevel(toVisitQueue, visited, end, executor)
            if (foundPath != null) {
                return foundPath
            }

            println("Depth $depth: Visiting ${visited.size} pages...")
        }

        executor.shutdown()
        return WikiPath.NOT_FOUND
    }

    private fun prepareSearch(start: String): SearchSetup {
        val visited = ConcurrentHashMap.newKeySet<String>()
        val toVisitQueue = ConcurrentLinkedQueue<PagePath>()
        val executor = Executors.newFixedThreadPool(maxThreads)

        toVisitQueue.add(start to listOf(start))
        visited.add(start)

        return Triple(toVisitQueue, visited, executor)
    }

    private fun searchDepthLevel(
        toVisitQueue: ConcurrentLinkedQueue<PagePath>,
        visited: MutableSet<String>,
        end: String,
        executor: ExecutorService
    ): WikiPath? {
        val nextLevel = ConcurrentLinkedQueue<PagePath>()
        val tasks = mutableListOf<Task>()

        while (toVisitQueue.isNotEmpty()) {
            val (currentPage, path) = toVisitQueue.poll()
            tasks.add(createTask(currentPage, path, end, visited, nextLevel))
        }

        return try {
            val results: TaskResultList = executor.invokeAll(tasks)
            results.mapNotNull { it.get() }.firstOrNull().also {
                if (it != null) {
                    executor.shutdownNow()
                } else {
                    toVisitQueue.addAll(nextLevel)
                }
            }
        } catch (e: InterruptedException) {
            println("Search interrupted: ${e.message}")
            null
        }
    }

    private fun createTask(
        currentPage: String,
        path: List<String>,
        end: String,
        visited: MutableSet<String>,
        nextLevel: ConcurrentLinkedQueue<PagePath>
    ): Task = Callable {
        val references = getReferences(currentPage)
        for (link in references) {
            if (link == end) {
                return@Callable WikiPath(path + link)
            }
            if (visited.add(link)) {
                nextLevel.add(link to (path + link))
            }
        }
        null
    }
}

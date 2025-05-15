package org.jetbrains.edu.kotlin.wikirace

import org.jsoup.Jsoup
import java.util.concurrent.*

interface WikiRacer {
    /**
     * @param startPage The starting page of the search.
     * @param destinationPage The destination page to be reached.
     * @param searchDepth The maximum depth of the search. If the destination page is not found within this depth, the
     * search should stop and return `WikiPath.NOT_FOUND`.
     */
    fun race(startPage: String, destinationPage: String, searchDepth: Int): WikiPath

    /**
     * Returns a list of all wikipedia articles references from the given page.
     * Remember to create the full link, e.g. `JetBrains -> https://en.wikipedia.org/wiki/JetBrains`.
     * You will need to remove the anchor from URLs (everything after and including the `#` symbol).
     * Only unique references should be returned — no duplicates.
     *
     * @param page Wikipedia page
     *
     * @see <a href="https://jsoup.org/cookbook/input/load-document-from-url">Jsoup documentation</a>
     * You can use `Jsoup.connect` to get a document
     * @see <a href="https://www.tabnine.com/code/java/methods/org.jsoup.nodes.Element/select">Example</a>
     * You can use `html.select` to find the references
     */
    fun getReferences(page: String): List<String>

    companion object {
        /**
         * @param maxThreads The maximum number of threads to use.
         */
        fun get(maxThreads: Int): WikiRacer {
            return object : WikiRacer {
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
                                    link.isNotBlank() &&
                                    link != formattedPage &&
                                    link != "Main_Page" &&
                                    forbiddenPrefixes.none { link.startsWith(it) }
                                ) link else null
                            }.distinct()
                    } catch (ex: Exception) {
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

                    for(depth in 1..searchDepth) {
                        val nextLevel = ConcurrentLinkedQueue<Pair<String, List<String>>>()
                        val tasks = mutableListOf<Callable<WikiPath?>>()

                        while (toVisitQueue.isNotEmpty()) {
                            val (currentPage, path) = toVisitQueue.poll()

                            tasks.add(Callable {
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
                            })
                        }

                        try {
                            val results = executor.invokeAll(tasks)
                            for (result in results) {
                                val path = result.get()
                                if (path != null) {
                                    executor.shutdownNow()
                                    return path
                                }
                            }
                        } catch (e: InterruptedException) {
                            println("Search interrupted: ${e.message}")
                            break
                        }

                        toVisitQueue.addAll(nextLevel)
                        println("Depth $depth: Visited ${visited.size} pages...")
                    }

                    executor.shutdown()
                    return WikiPath.NOT_FOUND
                }
            }
        }
    }
}

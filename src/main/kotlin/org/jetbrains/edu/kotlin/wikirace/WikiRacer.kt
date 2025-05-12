package org.jetbrains.edu.kotlin.wikirace

import org.jsoup.Jsoup

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
     * Remember to create the full link, e.g. `JetBrains -> https://en.wikipedia.org/wiki/JetBrains`
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


                        doc.select("#mw-content-text a[href^=\"/wiki/\"]")
                            .mapNotNull { element ->
                                val link = element.attr("href").removePrefix("/wiki/")
                                if (
                                    link.isNotBlank() &&
                                    !link.contains('#') &&
                                    forbiddenPrefixes.none { link.startsWith(it) }
                                ) link else null
                            }
                    } catch (ex: Exception) {
                        println("Error fetching $fullUrl: ${ex.message}")
                        emptyList()
                    }
                }
                override fun race(startPage: String, destinationPage: String, searchDepth: Int): WikiPath {
                    if (startPage == destinationPage) return WikiPath(listOf(startPage))
                    if (searchDepth <= 0) return WikiPath.NOT_FOUND

                    val visited = mutableSetOf<String>()
                    val toVisitQueue = ArrayDeque<Pair<String, List<String>>>()

                    toVisitQueue.add(startPage to listOf(startPage))
                    visited.add(startPage)

                    while (toVisitQueue.isNotEmpty()) {
                        val (currentPage, pathSoFar) = toVisitQueue.removeFirst()
                        if (pathSoFar.size > searchDepth + 1) continue

                        val links = getReferences(currentPage)
                        for (link in links) {
                            if (link == destinationPage) return WikiPath(pathSoFar + link)
                            if (link !in visited) {
                                visited.add(link)
                            toVisitQueue.add(link to (pathSoFar + link))
                            }
                        }
                    }

                    return WikiPath.NOT_FOUND
                }
            }
        }
    }
}
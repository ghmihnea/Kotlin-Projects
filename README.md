
# Parallel & Concurrent Test
**Source Code Location:**  
All source code is in the `src` folder:  
- `src/main/kotlin/org/jetbrains/edu/kotlin/wikirace` – contains the main project implementation including `WikiRacer` interface and other classes  
- `src/main/kotlin/org/jetbrains/edu/kotlin/Main.kt` – main entry point  
- `src/test/kotlin` – contains test cases for the project

## Task description

You may have heard about the game “Wikipedia race”, where you click on links in Wikipedia articles with the objective of
getting from one article to another in as few clicks as possible. You can click on any link on the page. Playing the 
game is fun – and it’s even more fun when you win! In this task, you need to implement a search to find the shortest 
from a start page to a destination page.

## Implementation Details

Your solution should be a console app with the following arguments:
- `search-depth` – How many transitions from the start page are allowed. Don't forget to check if the depth is > 0 and 
inform the user if the input is incorrect.
- Optional `max-threads` – How many threads to use, the default value is one. Don't forget to check if the number of 
threads is > 0 and inform the user if the input is incorrect.
- Optional `start` and `final` – If provided, then the solution should start searching from the Wikipedia page with the 
given title, otherwise it should start from any Wikipedia article. For example, if `AVL tree` is given as the `start` 
argument, your search should start from `https://en.wikipedia.org/wiki/AVL_tree`. Same applies to final. **OR** your
application can allow for multiple races, and wait for user input to get `start` and `final`.

The output of the app is a sequence of pages in the found path, or a message saying that no path was found within the
search depth. Consider displaying the progress, so that the user knows that the application has not frozen.

Feel free to use:
- [Clikt](https://ajalt.github.io/clikt/) or any other library to handle the command-line arguments
- [Jsoup](https://jsoup.org/), or [Ktor](https://ktor.io/docs/request.html) + [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization), 
or any other libraries to get and parse pages

You can work with Wikipedia as is, or via their [API](https://en.wikipedia.org/wiki/Special:ApiSandbox#action=parse&format=json&page=Pet_door&prop=text&disabletoc=1&formatversion=2).

To extract links from a Wiki page, you need to extract all internal (referring to any Wiki page) links. For `Jsoup` it 
can be achieved via `html.select("[href^=/wiki/]").map { it.attr("href") }`. You have to exclude the page itself, the
main wiki page, and non-articles, which 
include special prefixes:
```kotlin
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
```


To run the tests you could use predefined run configurations or run them manually:

**NOTE**: Wikipedia pages get updated more frequently than tests to this task, so 
[line 24 here](./src/test/kotlin/ParsingTest.kt) might be wrong.
* To run all tests locally, you can use:`./gradlew test`;

* To run Detekt locally, you can use: `./gradlew detekt`;

* To run Diktat locally, you can use: `./gradlew diktat`.

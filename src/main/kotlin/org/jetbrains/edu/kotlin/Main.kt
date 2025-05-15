package org.jetbrains.edu.kotlin

import org.jetbrains.edu.kotlin.wikirace.WikiPath
import org.jetbrains.edu.kotlin.wikirace.WikiRacer

fun main(args: Array<String>) {
    val argsMap = args.toList().windowed(2, 2).associate { it[0] to it[1] }

    val depth = argsMap["--search-depth"]?.toIntOrNull()

    val threads = argsMap["--max-threads"]?.toIntOrNull() ?: 1

    val start = argsMap["--start"]
    val end = argsMap["--final"]

    if (depth == null || depth <= 0) {
        println("Error: --search-depth must be a positive integer")
        return
    }

    if (threads <= 0) {
        println("Error: --max-threads must be a positive integer")
        return
    }

    if (start == null || end == null) {
        println("Error: Both --start and --final must be provided")
        return
    }

    println("Searching from \"$start\" to \"$end\" with depth $depth and $threads thread(s)...")

    val racer = WikiRacer.get(threads)
    val result: WikiPath = racer.race(start, end, depth)

    if (result == WikiPath.NOT_FOUND) {
        println("No path found within the given depth.")
    } else {
        println("Path found in ${result.steps} steps:")
        result.path.forEach { println("→ $it") }
    }
}

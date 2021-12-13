package me.aoc

import me.aoc.Aoc.Companion.asResource

object Aoc6 : Aoc<List<Int>, Long> {
    override fun calc(input: List<Int>): Long {
        var total = input.size.toLong()
        for (stage in input) {
            total += totalSpawned(stage, 79)
        }
        return total
    }

    data class Cycle(val stage: Int, val left: Int)

    val cache = mutableMapOf<Cycle, Long>()

    override fun calc2(input: List<Int>): Long {
        var total = input.size.toLong()
        for (stage in input) {
            total += totalSpawned(stage, 255)
        }
        return total
    }

    fun totalSpawned(stage: Int, days: Int): Long {
        if (cache.containsKey(Cycle(stage, days))) {
            return cache.getValue(Cycle(stage, days))
        }
        val newFishes = singleNumProduced(stage, days)
        var left = days
        var delta = stage
        var next = newFishes
        while (left >= delta) {
            left -= delta
            next += totalSpawned(9, left)
            delta = 7
        }
        cache[Cycle(stage, days)] = next
        return cache.getValue(Cycle(stage, days))
    }

    fun singleNumProduced(stage: Int, days: Int): Long {
        return if (days < stage) {
            0L
        } else {
            (days - stage) / 7L + 1
        }
    }

    override fun parse(content: String): List<Int> {
        return content.split(",").map { it.toInt() }
    }
}

fun main() {
    with(Aoc6) {
        println(
            calc(
                parse(
                    """
3,4,3,1,2
""".trimIndent()
                )
            )
        )
        val input = "aoc6.txt".asResource { content ->
            parse(content)
        }
        println(calc(input))
        println(calc2(input))
    }
}
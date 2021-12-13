package me.aoc

import me.aoc.Aoc.Companion.asResource

object Aoc1 : Aoc<List<Int>, Long> {
    override fun calc(input: List<Int>): Long {
        var sum = 0
        for (i in 0 until (input.size - 1)) {
            if (input[i + 1] > input[i])
                sum += 1
        }
        return sum.toLong()
    }

    override fun calc2(input: List<Int>): Long {
        var sum = 0
        for (i in 0 until (input.size - 3)) {
            if (input.subList(i + 1, i + 3 + 1).sum() > input.subList(i, i + 3).sum())
                sum += 1
        }
        return sum.toLong()

    }

    override fun parse(content: String): List<Int> {
        return content.split("\n").map { it.toInt() }
    }
}

fun main() {
    with(Aoc1) {
        println(
            calc2(
                parse(
                    """
199
200
208
210
200
207
240
269
260
263
""".trimIndent()
                )
            )
        )
        val input = "aoc1.txt".asResource { content ->
            parse(content)
        }
        println(calc(input))
        println(calc2(input))
    }
}
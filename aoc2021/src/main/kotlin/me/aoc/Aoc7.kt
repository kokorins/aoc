package me.aoc

import me.aoc.Aoc.Companion.asResource
import java.lang.StrictMath.abs

object Aoc7 : Aoc<List<Int>, Long> {
    override fun calc(input: List<Int>): Long {
        val median = input.sorted()[input.size / 2]
        var result = 0L
        for (pos in input) {
            result += abs(pos - median).toLong()
        }
        return result
    }

    override fun calc2(input: List<Int>): Long {
        fun cost(from: Int, to: Int): Long {
            val d = abs(to - from)
            return d * (d + 1) / 2L
        }

        fun sumCostTo(pos: Int): Long {
            var result = 0L
            for (x in input) {
                result += cost(pos, x)
            }
            return result
        }

        val min = input.minOrNull() ?: 0
        val max = input.maxOrNull() ?: 0
        val pos = (min..max).minByOrNull { sumCostTo(it) } ?: 0
        return sumCostTo(pos)
    }

    override fun parse(content: String): List<Int> {
        return content.split(",").map { it.toInt() }
    }
}

fun main() {
    with(Aoc7) {
        println(
            calc2(
                parse(
                    """
16,1,2,0,4,2,7,1,2,14
""".trimIndent()
                )
            )
        )
        val input = "aoc7.txt".asResource { content ->
            parse(content)
        }
        println(calc(input))
        println(calc2(input))
    }
}
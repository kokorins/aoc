package me.aoc

import me.aoc.Aoc.Companion.asResource

object Aoc0: Aoc<List<Int>, Long> {
    override fun calc(input: List<Int>): Long = TODO()
    override fun calc2(input: List<Int>): Long = TODO()
    override fun parse(content: String): List<Int> = TODO()
}

fun main() {
    with(Aoc0) {
        println(
            calc(
                parse(
                    """
""".trimIndent()
                )
            )
        )
        val input = "aoc0.txt".asResource { content ->
            parse(content)
        }
        println(calc(input))
//        println(calc2(input))
    }
}
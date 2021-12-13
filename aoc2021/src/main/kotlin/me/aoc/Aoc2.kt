package me.aoc

import me.aoc.Aoc.Companion.asResource

object Aoc2 : Aoc<List<Aoc2.Delta>, Long> {
    data class Delta(val dx: Int = 0, val dy: Int = 0) {
        companion object {
            fun h(dx: Int) = Delta(dx, 0)
            fun v(dy: Int) = Delta(0, dy)
        }
    }

    data class Point(val x: Long, val y: Long) {
        fun shift(delta: Delta): Point {
            return Point(x + delta.dx, y + delta.dy)
        }
    }

    data class AimedPoint(val x: Long, val y: Long, val aim: Long) {
        fun shift(delta: Delta): AimedPoint {
            return AimedPoint(x + delta.dx, y + delta.dx * aim, aim + delta.dy)
        }
    }

    override fun calc(input: List<Delta>): Long {
        var pos = Point(0, 0)
        for (delta in input) {
            pos = pos.shift(delta)
        }
        return pos.x * pos.y
    }

    override fun calc2(input: List<Delta>): Long {
        var pos = AimedPoint(0, 0, 0)
        for (delta in input) {
            pos = pos.shift(delta)
        }
        return pos.x * pos.y

    }

    override fun parse(content: String): List<Delta> {
        return content.split("\n").map { row ->
            val (dir, delta) = row.split(" ")
            when (dir) {
                "up" -> Delta.v(-delta.toInt())
                "down" -> Delta.v(delta.toInt())
                "forward" -> Delta.h(delta.toInt())
                else -> throw Error("No such direction")
            }
        }
    }
}

fun main() {
    with(Aoc2) {
        println(
            calc2(
                parse(
                    """
forward 5
down 5
forward 8
up 3
down 8
forward 2
""".trimIndent()
                )
            )
        )
        val input = "aoc2.txt".asResource { content ->
            parse(content)
        }
        println(calc(input))
        println(calc2(input))
    }
}
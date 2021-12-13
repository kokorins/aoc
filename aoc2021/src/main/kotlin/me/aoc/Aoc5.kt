package me.aoc

import me.aoc.Aoc.Companion.asResource
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

object Aoc5 : Aoc<List<Aoc5.Line>, Long> {
    data class Point(val x: Int, val y: Int)
    data class Line(val from: Point, val to: Point) {
        fun isHorV(): Boolean {
            return from.x == to.x || from.y == to.y
        }

        fun cover(): Set<Point> {
            val result = mutableSetOf<Point>()
            val dx = to.x - from.x
            val dy = to.y - from.y
            if (dx == 0) {
                for (y in min(to.y, from.y)..max(to.y, from.y)) {
                    result.add(Point(to.x, y))
                }
            } else if (dy == 0) {
                for (x in min(to.x, from.x)..max(to.x, from.x)) {
                    result.add(Point(x, to.y))
                }
            } else {
                val dx = (to.x - from.x).sign
                val dy = (to.y - from.y).sign
                for (i in 0..(to.x - from.x).absoluteValue) {
                    result.add(Point(from.x + dx * i, from.y + dy * i))
                }
            }
            return result
        }

        companion object {
            fun xOrder(from: Point, to: Point): Line {
                return if (from.x > to.x) {
                    Line(to, from)
                } else {
                    Line(from, to)
                }
            }
        }
    }

    override fun calc(input: List<Line>): Long {
        val points = mutableSetOf<Point>()
        val moreThanOnce = mutableSetOf<Point>()
        for (line in input) {
            if (line.isHorV()) {
                val cover = line.cover()
                moreThanOnce.addAll(points.intersect(cover))
                points.addAll(cover)
            }
        }
        return moreThanOnce.size.toLong()
    }

    override fun calc2(input: List<Line>): Long {
        val points = mutableSetOf<Point>()
        val moreThanOnce = mutableSetOf<Point>()
        for (line in input) {
            val cover = line.cover()
            moreThanOnce.addAll(points.intersect(cover))
            points.addAll(cover)
        }
        return moreThanOnce.size.toLong()

    }

    override fun parse(content: String): List<Line> {
        return content.split("\n").map {
            val (from, to) = it.split(" -> ".toRegex()).map { it.split(",").map { it.toInt() } }
                .map { Point(it[0], it[1]) }
            Line.xOrder(from, to)
        }
    }
}

fun main() {
    with(Aoc5) {
        println(
            calc2(
                parse(
                    """
0,9 -> 5,9
8,0 -> 0,8
9,4 -> 3,4
2,2 -> 2,1
7,0 -> 7,4
6,4 -> 2,0
0,9 -> 2,9
3,4 -> 1,4
0,0 -> 8,8
5,5 -> 8,2
""".trimIndent()
                )
            )
        )
        val input = "aoc5.txt".asResource { content ->
            parse(content)
        }
        println(calc(input))
        println(calc2(input))
    }
}
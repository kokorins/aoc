package me.aoc

import me.aoc.Aoc.Companion.asResource
import kotlin.math.min

object Aoc15 : Aoc<List<List<Int>>, Long> {
    data class Point(val x: Int, val y: Int) {
        fun neigs(w: Int, h: Int): List<Point> {
            val result = mutableListOf<Point>()
            if (x > 0) {
                result.add(Point(x - 1, y))
            }
            if (y > 0) {
                result.add(Point(x, y - 1))
            }
            if (x < w - 1) {
                result.add(Point(x + 1, y))
            }
            if (y < h - 1) {
                result.add(Point(x, y + 1))
            }
            return result
        }
    }

    data class Path(val pt: Point, val dist: Int)
//    data class Edge(val lhs: Point, val rhs: Point, val weight: Int)

    override fun calc(input: List<List<Int>>): Long {
        val grid = input
        val width = grid.first().size
        val height = grid.size
        val queue = mutableMapOf(Point(0, 0) to 0)
        val done = mutableSetOf<Point>()
        while (queue.isNotEmpty()) {
            val elem = queue.minByOrNull { it.value } ?: throw Error("Shouldn't be empty")
            queue.remove(elem.key)
            done.add(elem.key)
            if (elem.key.x == width - 1 && elem.key.y == height - 1) {
                return elem.value.toLong()
            }
            val ns = elem.key.neigs(width, height)
            for (n in ns) {
                if (n !in done) {
                    queue.compute(n) { pt, dist ->
                        if (dist == null) {
                            elem.value + grid[pt.y][pt.x]
                        } else {
                            min(dist, elem.value + grid[pt.y][pt.x])
                        }
                    }
                }
            }
        }
        return -1L
    }

    override fun calc2(input: List<List<Int>>): Long {
        val grid = scale(input, 5)
        val width = grid.first().size
        val height = grid.size
        val queue = mutableMapOf(Point(0, 0) to 0)
        val done = mutableSetOf<Point>()
        while (queue.isNotEmpty()) {
            val elem = queue.minByOrNull { it.value } ?: throw Error("Shouldn't be empty")
            queue.remove(elem.key)
            done.add(elem.key)
            if (elem.key.x == width - 1 && elem.key.y == height - 1) {
                return elem.value.toLong()
            }
            val ns = elem.key.neigs(width, height)
            for (n in ns) {
                if (n !in done) {
                    queue.compute(n) { pt, dist ->
                        if (dist == null) {
                            elem.value + grid[pt.y][pt.x]
                        } else {
                            min(dist, elem.value + grid[pt.y][pt.x])
                        }
                    }
                }
            }
        }
        return -1L
    }

    fun scale(input: List<List<Int>>, scale: Int): List<List<Int>> {
        val width = input.first().size
        val height = input.size

        val result = MutableList(height * scale) { MutableList(width * scale) { 0 } }
        for (j in 0 until height * scale) {
            for (i in 0 until width * scale) {
                result[j][i] = input[j % height][i % width]
                val add = (j / height) + (i / width)
                for (idx in 1..add) {
                    result[j][i] += 1
                    if (result[j][i] > 9) {
                        result[j][i] = 1
                    }
                }
            }
        }

        return result
    }

    override fun parse(content: String): List<List<Int>> {
        return content.split("\n").map {
            it.map { it.digitToInt() }
        }
    }
}

fun main() {
    with(Aoc15) {
        println(
            calc2(
                parse(
                    """
1163751742
1381373672
2136511328
3694931569
7463417111
1319128137
1359912421
3125421639
1293138521
2311944581
""".trimIndent()
                )
            )
        )
        val input = "aoc15.txt".asResource { content ->
            parse(content)
        }
        println(calc(input))
        println(calc2(input))
    }
}
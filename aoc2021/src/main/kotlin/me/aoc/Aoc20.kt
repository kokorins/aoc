package me.aoc

import me.aoc.Aoc.Companion.asResource

object Aoc20 : Aoc<Aoc20.Input, Long> {
    data class Point(val x: Int, val y: Int) {
        fun neigs(): List<Point> {
            val result = mutableListOf<Point>()
            for (j in (-1..1)) {
                for (i in (-1..1)) {
                    result.add(Point(x + i, y + j))
                }
            }
            return result
        }

        fun withX(x: Int) = Point(x, y)
        fun withY(y: Int) = Point(x, y)
    }

    data class State(val grid: Set<Point>, val min: Point, val max: Point, val stored: Boolean) {
        fun lit(pt: Point): Boolean = if (stored) grid.contains(pt) else !grid.contains(pt)
        override fun toString(): String {
            val stringBuilder = StringBuilder()
            for (j in min.y..max.y) {
                for (i in min.x..max.x) {
                    if (lit(Point(i, j))) {
                        stringBuilder.append("#")
                    } else {
                        stringBuilder.append('.')
                    }
                }
                stringBuilder.appendLine()
            }
            return stringBuilder.toString()
        }

        fun step(pattern: List<Boolean>): State {
            val stored = if (stored) !pattern[0] else !pattern[511]
            var newMax = min
            var newMin = max
            val result = mutableSetOf<Point>()
            for (j in (min.y - 1)..(max.y + 1)) {
                for (i in (min.x - 1)..(max.x + 1)) {
                    val pt = Point(i, j)
                    if (stored == lit(pt, pattern)) {
                        result.add(pt)
                        if (newMax.x < pt.x) {
                            newMax = newMax.withX(pt.x)
                        }
                        if (newMax.y < pt.y) {
                            newMax = newMax.withY(pt.y)
                        }
                        if (newMin.x > pt.x) {
                            newMin = newMin.withX(pt.x)
                        }
                        if (newMin.y > pt.y) {
                            newMin = newMin.withY(pt.y)
                        }
                    }
                }
            }
            return State(result, newMin, newMax, stored)
        }

        fun lit(pt: Point, pattern: List<Boolean>): Boolean {
            var result = 0
            for (n in pt.neigs()) {
                result = result.shl(1).or(if (lit(n)) 1 else 0)
            }
            return pattern[result]
        }
    }

    data class Input(val pattern: List<Boolean>, val state: State)

    override fun calc(input: Input): Long {
        var (pattern, state) = input
//        println(state)
        for (i in 1..2) {
            state = state.step(pattern)
//            println(state)
        }
        return state.grid.size.toLong()
    }

    override fun calc2(input: Input): Long {
        var (pattern, state) = input
//        println(state)
        for (i in 1..50) {
            state = state.step(pattern)
//            println(state)
        }
        return state.grid.size.toLong()
    }
    override fun parse(content: String): Input {
        val (patternBlock, gridBlock) = content.split("\n\n")
        val pattern = patternBlock.map { ch ->
            when (ch) {
                '.' -> false
                '#' -> true
                else -> throw Error("Unexpected $ch")
            }
        }
        val grid = mutableSetOf<Point>()
        val rows = gridBlock.split("\n")
        val min = Point(0, 0)
        var max = Point(0, 0)
        for (j in rows.indices) {
            val row = rows[j]
            for (i in row.indices) {
                if (row[i] == '#') {
                    grid.add(Point(i, j))
                    if (max.x < i) {
                        max = max.withX(i)
                    }
                    if (max.y < j) {
                        max = max.withY(j)
                    }
                }
            }
        }
        return Input(pattern, State(grid, min, max, true))
    }
}

fun main() {
    with(Aoc20) {
        println(
            calc2(
                parse(
                    """
..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..###..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###.######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#..#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#......#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#.....####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#.......##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#

#..#.
#....
##..#
..#..
..###
""".trimIndent()
                )
            )
        )
        val input = "aoc20.txt".asResource { content ->
            parse(content)
        }
        println(calc(input))
        println(calc2(input))
    }
}
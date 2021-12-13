package me.aoc

import me.aoc.Aoc.Companion.asResource
import java.util.*

object Aoc11 : Aoc<Aoc11.Grid, Long> {
    data class Point(val x: Int, val y: Int) {
        fun d(dx: Int, dy: Int) = Point(x + dx, y + dy)
        fun neigs(width: Int, height: Int): List<Point> {
            val ds = listOf(
                -1 to -1, -1 to 0, -1 to 1,
                0 to -1, 0 to 1,
                1 to -1, 1 to 0, 1 to 1
            )
            return ds.map { this.d(it.first, it.second) }
                .filter { it.x in 0 until width && it.y in 0 until height }
        }
    }


    data class Grid(val grid: List<List<Int>>, val width: Int, val height: Int) {
        fun step(): Grid {
            val newGrid = MutableList(height) { MutableList(width) { 0 } }
            val queue = ArrayDeque<Point>()
            for (j in 0 until height) {
                for (i in 0 until width) {
                    queue.add(Point(i, j))
                    newGrid[j][i] = grid[j][i]
                }
            }
            while (queue.isNotEmpty()) {
                val cur = queue.pop()
                if (newGrid[cur.y][cur.x] <= 9) {
                    newGrid[cur.y][cur.x] += 1
                    if (newGrid[cur.y][cur.x] > 9) {
                        for (n in cur.neigs(width, height)) {
                            queue.add(n)
                        }
                    }
                }
            }
            for (j in 0 until height) {
                for (i in 0 until width) {
                    if (newGrid[j][i] > 9) {
                        newGrid[j][i] = 0
                    }
                }
            }
            return Grid(newGrid, width, height)
        }
    }

    override fun calc(input: Grid): Long {
        var cur = input
        var result = 0L
        for (i in 1..100) {
            cur = cur.step()
            for (j in 0 until input.height) {
                for (i in 0 until input.width) {
                    if (cur.grid[j][i] == 0) {
                        result += 1
                    }
                }
            }
        }
        return result
    }

    override fun calc2(input: Grid): Long {
        var cur = input
        var result = 0L
        loop@while (true) {
            cur = cur.step()
            result += 1
            for (j in 0 until input.height) {
                for (i in 0 until input.width) {
                    if (cur.grid[j][i] != 0) {
                        continue@loop
                    }
                }
            }
            return result
        }
    }

    override fun parse(content: String): Grid {
        val grid = content.split("\n").map {
            it.map { it.digitToInt() }
        }
        return Grid(grid, grid.first().size, grid.size)
    }
}

fun main() {
    with(Aoc11) {
        println(
            calc2(
                parse(
                    """
5483143223
2745854711
5264556173
6141336146
6357385478
4167524645
2176841721
6882881134
4846848554
5283751526
""".trimIndent()
                )
            )
        )
        val input = "aoc11.txt".asResource { content ->
            parse(content)
        }
        println(calc(input))
        println(calc2(input))
    }
}
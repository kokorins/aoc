package me.aoc

import me.aoc.Aoc.Companion.asResource

object Aoc9 : Aoc<Aoc9.HeatMap, Long> {
    data class Point(val x: Int, val y: Int) {
        fun d(dx: Int, dy: Int) = Point(x + dx, y + dy)
        fun neigs(width: Int, height: Int): List<Point> {
            val ds = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
            return ds.map { this.d(it.first, it.second) }
                .filter { it.x in 0 until width && it.y in 0 until height }
        }
    }

    data class HeatMap(val grid: List<List<Int>>, val width: Int, val height: Int) {
        fun at(pt: Point): Int = grid[pt.y][pt.x]
    }

    override fun calc(input: HeatMap): Long {
        var result = 0L
        for (y in 0 until input.height) {
            for (x in 0 until input.width) {
                val value = input.grid[y][x]
                if (Point(x, y).neigs(input.width, input.height).all { input.grid[it.y][it.x] > value }) {
                    result += value + 1
                }
            }
        }
        return result
    }

    fun lowPoints(heatMap: HeatMap): List<Point> {
        val result = mutableListOf<Point>()
        for (y in 0 until heatMap.height) {
            for (x in 0 until heatMap.width) {
                val value = heatMap.grid[y][x]
                if (Point(x, y).neigs(heatMap.width, heatMap.height).all { heatMap.grid[it.y][it.x] > value }) {
                    result.add(Point(x, y))
                }
            }
        }
        return result
    }

    fun basin(heatMap: HeatMap, start: Point): Set<Point> {
        val visited = mutableSetOf<Point>()
        val queue = mutableListOf(start)
        while (!queue.isEmpty()) {
            val cur = queue.first()
            queue.removeAt(0)
            for (neig in cur.neigs(heatMap.width, heatMap.height)) {
                if (!visited.contains(neig)) {
                    if (heatMap.at(neig) < 9) {
                        visited.add(neig)
                        queue.add(neig)
                    }
                }
            }
        }
        return visited
    }

    override fun calc2(input: HeatMap): Long {
        val basins = mutableListOf<Set<Point>>()
        for (lowPoint in lowPoints(input)) {
            basins.add(basin(input, lowPoint))
        }
        val sizes = basins.sortedByDescending { it.size }.take(3).map { it.size }
        var prod = 1L
        sizes.forEach { prod *= it }
        return prod
    }

    override fun parse(content: String): HeatMap {
        val map = content.split("\n").map { it.toCharArray().map { it.digitToInt() } }
        return HeatMap(map, map.first().size, map.size)
    }
}

fun main() {
    with(Aoc9) {
        println(
            calc2(
                parse(
                    """
2199943210
3987894921
9856789892
8767896789
9899965678
""".trimIndent()
                )
            )
        )
        val input = "aoc9.txt".asResource { content ->
            parse(content)
        }
        println(calc(input))
        println(calc2(input))
    }
}
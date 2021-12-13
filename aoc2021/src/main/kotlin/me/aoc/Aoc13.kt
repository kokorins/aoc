package me.aoc

import me.aoc.Aoc.Companion.asResource

object Aoc13 : Aoc<Aoc13.Paper, Long> {
    data class Point(val x: Int, val y: Int)
    sealed interface Fold {
        data class X(val row: Int) : Fold {
            override fun apply(dots: Set<Point>): Set<Point> {
                return dots.flatMap {
                    if (it.x == row) {
                        listOf()
                    } else if (it.x > row) {
                        listOf(Point(2 * row - it.x, it.y))
                    } else {
                        listOf(it)
                    }
                }.toSet()
            }
        }

        data class Y(val col: Int) : Fold {
            override fun apply(dots: Set<Point>): Set<Point> {
                return dots.flatMap {
                    if (it.y == col) {
                        listOf()
                    } else if (it.y > col) {
                        listOf(Point(it.x, 2 * col - it.y))
                    } else {
                        listOf(it)
                    }
                }.toSet()
            }
        }

        fun apply(dots: Set<Point>): Set<Point>
    }

    data class Paper(val dots: Set<Point>, val ops: List<Fold>)

    override fun calc(input: Paper): Long {
        val (dots, folds) = input
        var cur = dots
        val fold = folds.first()
        cur = fold.apply(cur)
        return cur.size.toLong()
    }

    override fun calc2(input: Paper): Long {
        val (dots, folds) = input
        var cur = dots
        for (fold in folds) {
            cur = fold.apply(cur)
        }
        val minx = cur.map { it.x }.minOrNull() ?: 0
        val maxx = cur.map { it.x }.maxOrNull() ?: 0
        val miny = cur.map { it.x }.minOrNull() ?: 0
        val maxy = cur.map { it.x }.maxOrNull() ?: 0
        for (y in miny..maxy) {
            for (x in minx..maxx) {
                if(cur.contains(Point(x, y))) {
                    print("x")
                } else {
                    print(" ")
                }
            }
            println()
        }
        return 0L
    }

    override fun parse(content: String): Paper {
        val (coords, commands) = content.split("\n\n")
        val points = coords.split("\n").map {
            val (x, y) = it.split(",").map { it.toInt() }
            Point(x, y)
        }.toSet()
        val folds = commands.split("\n").map {
            val (coord, num) = it.split("=")
            if (coord.endsWith("x")) {
                Fold.X(num.toInt())
            } else {
                Fold.Y(num.toInt())
            }
        }
        return Paper(points, folds)
    }
}

fun main() {
    with(Aoc13) {
        println(
            calc(
                parse(
                    """
6,10
0,14
9,10
0,3
10,4
4,11
6,0
6,12
4,1
0,13
10,12
3,4
3,0
8,4
1,10
2,14
8,10
9,0

fold along y=7
fold along x=5
""".trimIndent()
                )
            )
        )
        val input = "aoc13.txt".asResource { content ->
            parse(content)
        }
        println(calc(input))
        println(calc2(input))
    }
}
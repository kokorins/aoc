package me.aoc

import me.aoc.Aoc.Companion.asResource

object Aoc25 : Aoc<Aoc25.State, Long> {
    data class Point(val x: Int, val y: Int) {
        fun add(that: Point, width: Int, height: Int) = Point((x + that.x) % width, (y + that.y) % height)
    }

    sealed interface Cucumber {
        object Right : Cucumber {
            override val idx = 0
            override val delta = Point(1, 0)
        }

        object Down : Cucumber {
            override val idx = 1
            override val delta = Point(0, 1)
        }

        val delta: Point
        val idx: Int
    }

    data class State(val grid: Map<Point, Cucumber>, val width: Int, val height: Int) {
        fun step(): Pair<State, Boolean> {
            var moved = false
            val newGrid = mutableMapOf<Point, Cucumber>()
            for (type in listOf(Cucumber.Right, Cucumber.Down)) {
                for ((pt, cucumber) in grid) {
                    if (cucumber == type) {
                        val newPt = pt.add(type.delta, width, height)
                        val old = grid[newPt]?.idx
                        val new = newGrid[newPt]?.idx
                        if (new != null || (old != null && old >= type.idx)) {
                            newGrid[pt] = type
                        } else {
                            newGrid[newPt] = type
                            moved = true
                        }
                    }
                }
            }
            return State(newGrid, width, height) to moved
        }

        override fun toString(): String {
            val stringBuilder = StringBuilder()
            for (j in 0 until height) {
                for (i in 0 until width) {
                    val ch = when (grid[Point(i, j)]) {
                        Cucumber.Right -> ">"
                        Cucumber.Down -> "v"
                        else -> '.'
                    }
                    stringBuilder.append(ch)
                }
                stringBuilder.appendLine()
            }
            return stringBuilder.toString()
        }
    }

    override fun calc(input: State): Long {
        var idx = 0
        var cur = input
        var moved = true
        while (moved) {
            val pair = cur.step()
            cur = pair.first
            moved = pair.second
            idx += 1
//            if (idx % 10 == 0) {
//                println(cur)
//            }
        }
        return idx.toLong()
    }

    override fun calc2(input: State): Long = TODO()
    override fun parse(content: String): State {
        val grid = mutableMapOf<Point, Cucumber>()

        val rows = content.split("\n")
        val width = rows.first().length
        val height = rows.size
        for (j in 0 until height) {
            for (i in 0 until width) {
                val cucumber = when (rows[j][i]) {
                    '>' -> Cucumber.Right
                    'v' -> Cucumber.Down
                    else -> null
                }
                if (cucumber != null) {
                    grid[Point(i, j)] = cucumber
                }
            }
        }
        val state = State(grid, width, height)
        return state
    }
}

fun main() {
    with(Aoc25) {
        println(
            calc(
                parse(
                    """
v...>>.vv>
.vv>>.vv..
>>.>v>...v
>>v>>.>.v.
v>v.vv.v..
>.>>..v...
.vv..>.>v.
v.v..>>v.v
....v..v.>
""".trimIndent()
                )
            )
        )
        val input = "aoc25.txt".asResource { content ->
            parse(content)
        }
        println(calc(input))
//        println(calc2(input))
    }
}
package me.aoc

import me.aoc.Aoc.Companion.asResource
import java.lang.Math.*
import java.util.*

object Aoc23 : Aoc<Aoc23.State, Long> {
    data class Point(val x: Int, val y: Int)
    data class Token(val name: String, val cost: Int, val pos: Point, val finished: Boolean) {
        fun inRoom() = pos.y > 0
        fun inHall() = pos.y == 0

        companion object {
            private val costs = mapOf("A" to 1, "B" to 10, "C" to 100, "D" to 1000)
            fun init(name: String, idx: Int) =
                /**
                 * Positions
                 * (2,1) (4,1) (6,1) (8,1)
                 * (2,2) (4,2) (6,2) (8,2)
                 */
                Token(name, costs.getValue(name), rooms[idx], false)

            val hall = (0..10).map { Point(it, 0) }.toSet().minus((0..3).map { Point(it * 2 + 2, 0) }.toSet())
            val rooms = (0..15).map { Point((it % 4) * 2 + 2, it / 4 + 1) }
            fun reachableHallPositions(tokens: Map<Point, Token>, token: Token): List<Point> {
                if ((1 until token.pos.y).any { tokens.containsKey(Point(token.pos.x, it)) })
                    return emptyList()
                val tokensInHall = tokens.values.filter { it.inHall() }
                var lowBound = -1
                var highBound = 11
                for (blocker in tokensInHall) {
                    if (token.pos.x > blocker.pos.x) {
                        lowBound = max(lowBound, blocker.pos.x)
                    }
                    if (token.pos.x < blocker.pos.x) {
                        highBound = min(highBound, blocker.pos.x)
                    }
                }
                return hall.filter { it.x in (lowBound + 1) until highBound }
            }

            val roomFit = mapOf("A" to 2, "B" to 4, "C" to 6, "D" to 8)

            fun reachableRoomPositions(tokens: Map<Point, Token>, token: Token): List<Point> {
                val x = roomFit.getValue(token.name)
                val validRooms = (1..4).map { Point(x, it) }
                val possibleMoves = validRooms.filter { it !in tokens.keys }.groupBy { it.x }.map { (x, pts) ->
                    Point(x, pts.maxOf { it.y })
                }
                val tokensInHall = tokens.values.filter { it.inHall() }.minus(token)
                return possibleMoves.filter { target -> tokensInHall.none { between(it.pos, target, token.pos) } }
            }

            fun between(pt: Point, lhs: Point, rhs: Point): Boolean {
                return (pt.x - lhs.x) * (pt.x - rhs.x) < 0
            }

        }
    }

    data class Move(val from: Point, val to: Point) {
        fun cost(state: State): Int {
            return (abs(to.x - from.x) + abs(to.y - from.y)) * state.tokens.getValue(from).cost
        }
    }

    data class StateWithCost(val state: State, val cost: Long)

    data class State(val tokens: Map<Point, Token>) {
        override fun toString(): String {
            val stringBuilder = StringBuilder()
            for (j in 0..4) {
                for (i in 0..10) {
                    val pt = Point(i, j)
                    val token = tokens[pt]
                    if (token != null) {
                        if (token.finished) {
                            stringBuilder.append(token.name.lowercase())
                        } else {
                            stringBuilder.append(token.name)
                        }
                    } else {
                        if (Token.rooms.contains(pt) || Token.hall.contains(pt)) {
                            stringBuilder.append(".")
                        } else {
                            stringBuilder.append(" ")
                        }
                    }
                }
                stringBuilder.appendLine()
            }
            return stringBuilder.toString()
        }

        fun final(): Boolean {
            return if (tokens.keys.any { it.y == 0 })
                false
            else {
                tokens.values.groupBy { it.name }.values.all { tokens -> tokens.map { it.pos.x }.toSet().size == 1 }
            }
        }

        fun next(): List<Move> {
            val result = mutableListOf<Move>()
            for (token in tokens.values) {
                if (token.finished) {
                    continue
                } else {
                    if (token.inRoom()) {
                        val positions = Token.reachableHallPositions(tokens, token)
                        result.addAll(positions.map { Move(token.pos, it) })
                    } else {
                        val positions = Token.reachableRoomPositions(tokens, token)
                        result.addAll(positions.map { Move(token.pos, it) })
                    }
                }
            }
            return result
        }

        fun make(move: Move): State {
            val token = tokens.getValue(move.from)
            val finished = move.to.y > 0
            assert(!tokens.containsKey(move.to))
            return State(tokens.minus(move.from).plus(move.to to token.copy(pos = move.to, finished = finished)))
        }
    }

    override fun calc(input: State): Long {
        val costs = mutableMapOf(input to 0L)
        val queue = PriorityQueue<StateWithCost>(Comparator.comparing { it.cost })
        queue.add(StateWithCost(input, 0L))
//        var idx = 0
        val processed = mutableSetOf<State>()
        while (queue.isNotEmpty()) {
            val (cur, cost) = queue.poll()
            if (cur.final()) {
                println(cur)
                return cost
            }
//            idx += 1
//            if (idx % 100000 == 0) {
//                println(cur)
//                println(processed.size)
//            }

            processed.add(cur)
            for (move in cur.next()) {
                val add = move.cost(cur)
                val n = cur.make(move)
                if (n !in processed) {
                    val curNCost = costs[n]
                    if (curNCost == null || curNCost > cost + add) {
                        costs[n] = cost + add
                        queue.add(StateWithCost(n, cost + add))
                    }
                }
            }

        }
        return costs.filterKeys { state -> state.final() }.minOf { it.value }
    }

    override fun calc2(input: State): Long = calc(input)

    override fun parse(content: String): State {
        val results = "[ABCD]".toRegex().findAll(content)
        return State(results.mapIndexed { idx, result ->
            Token.init(result.value, idx)
        }.associateBy { it.pos })
    }
}

fun main() {
    with(Aoc23) {
        println(
            calc2(
                parse(
                    """
#############
#...........#
###B#C#B#D###
  #D#C#B#A#
  #D#B#A#C#
  #A#D#C#A#
  #########
""".trimIndent()
                )
            )
        )
        val input = "aoc23.txt".asResource { content ->
            parse(content)
        }
//        println(input)
//        for (m in input.next()) {
//            val next = input.make(m)
//            println(next)
//            for(mm in next.next()) {
//                println(next.make(mm))
//            }
//        }
//        println(calc(input))
        println(calc2(input))
    }
}
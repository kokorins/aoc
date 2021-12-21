package me.aoc

import me.aoc.Aoc.Companion.asResource

object Aoc21 : Aoc<Aoc21.Input, Long> {
    data class Input(val p1: Int, val p2: Int)
    data class PlayerState(val score: Int, val pos: Int) {
        fun move(steps: Int): PlayerState {
            val newPos = (pos + steps) % 10
            return PlayerState(score + newPos + 1, newPos)
        }
    }

    data class DiceState(val numRolls: Int, val pos: Int) {
        fun score() = pos + 1
        fun roll(): DiceState = DiceState(numRolls + 1, (pos + 1) % 100)
    }

    data class State(val players: Map<Int, PlayerState>, val diceState: DiceState) {
        fun roll(idx: Int): State {
            val player = players.getValue(idx)
            var curDice = diceState
            var sum = 0
            for (i in 1..3) {
                curDice = curDice.roll()
                sum += curDice.score()
            }
            val newPlayer = player.move(sum)
            return State(players.minus(idx).plus(idx to newPlayer), curDice)
        }


    }

    override fun calc(input: Input): Long {
        var state = State(mapOf(0 to PlayerState(0, input.p1 - 1), 1 to PlayerState(0, input.p2 - 1)), DiceState(0, -1))
        while (true) {
            state = state.roll(0)
            if (state.players.getValue(0).score >= 1000) {
                println("${state.players.getValue(1).score}*${state.diceState.numRolls}")
                return state.players.getValue(1).score * state.diceState.numRolls.toLong()
            }
            state = state.roll(1)
            if (state.players.getValue(1).score >= 1000) {
                println("${state.players.getValue(0).score}*${state.diceState.numRolls}")
                return state.players.getValue(0).score * state.diceState.numRolls.toLong()
            }
        }
    }

    data class DiracState(val players: Map<Int, PlayerState>, val turn: Int) {
        fun turn(): List<Pair<DiracState, Long>> {
            val results = mutableListOf<Pair<DiracState, Long>>()
            val scores = roll().map { it.sum() }.groupingBy { it }.eachCount()
            for ((sum, num) in scores) {
                val player = players.getValue(turn)
                val newPlayer = player.move(sum)
                results.add(DiracState(players.minus(turn).plus(turn to newPlayer), 1 - turn) to num.toLong())
            }

            return results
        }

        fun roll(prefix: List<Int> = listOf()): List<List<Int>> {
            if (prefix.size == 3) {
                return listOf(prefix)
            }
            val result = mutableListOf<List<Int>>()
            for (idx in 1..3)
                result.addAll(roll(prefix.plus(idx)))
            return result
        }

        fun win(): Int? {
            return if (players.getValue(1 - turn).score >= 21) {
                1 - turn
            } else {
                null
            }
        }
    }

    val cache = mutableMapOf<DiracState, List<Long>>()

    fun numWins(state: DiracState, num: Long): List<Long> {
        if (!cache.containsKey(state)) {
            val wins = mutableListOf(0L, 0L)
            for ((newState, newNum) in state.turn()) {
                val id = newState.win()
                if (id != null) {
                    wins[id] += (newNum)
                } else {
                    val cur = numWins(newState, newNum)
                    for (idx in cur.indices) {
                        wins[idx] += cur[idx]
                    }
                }
            }
            cache.put(state, wins)
        }

        val wins = cache.getValue(state)
        return wins.map { it * num }
    }

    override fun calc2(input: Input): Long {
        val wins = numWins(
            DiracState(
                mapOf(0 to PlayerState(0, input.p1 - 1), 1 to PlayerState(0, input.p2 - 1)), 0
            ), 1
        )
        return wins.maxOrNull() ?: 0L
    }

    override fun parse(content: String): Input {
        val (l, r) = content.split("\n").map {
            it.split(" ").last().toInt()
        }
        return Input(l, r)
    }
}

fun main() {
    with(Aoc21) {
        println(
            calc2(
                parse(
                    """
Player 1 starting position: 4
Player 2 starting position: 8
""".trimIndent()
                )
            )
        )
        val input = "aoc21.txt".asResource { content ->
            parse(content)
        }
        println(calc(input))
        println(calc2(input))
    }
}
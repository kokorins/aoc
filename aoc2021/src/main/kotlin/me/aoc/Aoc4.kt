package me.aoc

import me.aoc.Aoc.Companion.asResource

object Aoc4: Aoc<Aoc4.BingoGame, Long> {
    data class Board(val layout: List<List<Int>>, val winCombos: List<Set<Int>>) {
        fun hasWin(numbers: Set<Int>):Boolean {
            return winCombos.any { numbers.containsAll(it) }
        }
        fun score(numbers: Set<Int>, number: Int):Long {
            var unmarked = 0
            for(row in layout) {
                unmarked += row.toSet().minus(numbers).sum()
            }
            println("$unmarked * $number")
            return unmarked * number.toLong()
        }
    }
    data class BingoGame(val numbers: List<Int>, val boards: List<Board>)
    override fun calc(input: BingoGame): Long {
        val (numbers, boards) = input
        val occurrence = mutableSetOf<Int>()
        for(number in numbers) {
            occurrence.add(number)
            for(board in boards) {
                if(board.hasWin(occurrence)) {
                    return board.score(occurrence, number)
                }
            }
        }
        return -1L
    }
    override fun calc2(input: BingoGame): Long {
        val (numbers, boards) = input
        val occurrence = mutableSetOf<Int>()
        var exclude = boards.toSet()
        for(number in numbers) {
            occurrence.add(number)
            for(board in exclude) {
                if(board.hasWin(occurrence)) {
                    exclude = exclude.minus(board)
                    if(exclude.isEmpty()) {
                        return board.score(occurrence, number)
                    }
                }
            }
        }
        return -1L
    }
    override fun parse(content: String): BingoGame {
        val blocks = content.split("\n\n")
        val numbers= blocks.first().split(",").map { it.toInt() }
        val boards = blocks.drop(1).map { board->
            val rows = board.split("\n")

            val layout = MutableList(5){ MutableList(5) {0}}
            for(j in rows.indices) {
                val row = rows[j].split(" ").filterNot { it.isEmpty() }.map { it.toInt() }
                for(i in row.indices) {
                    layout[i][j] = row[i]
                }
            }

            val winCombos = mutableListOf<Set<Int>>()
            for(i in 0..4) {
                val combo = mutableSetOf<Int>()
                val combo2 = mutableSetOf<Int>()
                for(j in 0..4) {
                    combo.add(layout[i][j])
                    combo2.add(layout[j][i])
                }
                winCombos.add(combo)
                winCombos.add(combo2)
            }
            Board(layout, winCombos)
        }
        return BingoGame(numbers, boards)
    }
}

fun main() {
    with(Aoc4) {
        println(
            calc2(
                parse(
                    """
7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1

22 13 17 11  0
 8  2 23  4 24
21  9 14 16  7
 6 10  3 18  5
 1 12 20 15 19

 3 15  0  2 22
 9 18 13 17  5
19  8  7 25 23
20 11 10 24  4
14 21 16 12  6

14 21 17 24  4
10 16 15  9 19
18  8 23 26 20
22 11 13  6  5
 2  0 12  3  7
""".trimIndent()
                )
            )
        )
        val input = "aoc4.txt".asResource { content ->
            parse(content)
        }
        println(calc(input))
        println(calc2(input))
    }
}
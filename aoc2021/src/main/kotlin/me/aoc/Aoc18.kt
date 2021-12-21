package me.aoc

import me.aoc.Aoc.Companion.asResource

object Aoc18 : Aoc<List<Aoc18.Snail>, Long> {
    data class Reader(val line: String) {
        var idx = 0
        fun read(): Snail {
            return readPair()
        }

        fun readPair(): Snail {
            if (readChar() != '[') {
                throw Error("[ Expected at $idx")
            }
            val lhs = if (cur() == '[') {
                readPair()
            } else {
                readNumber()
            }
            if (readChar() != ',') {
                throw Error(", Expected at $idx")
            }
            val rhs = if (cur() == '[') {
                readPair()
            } else {
                readNumber()
            }
            if (readChar() != ']') {
                throw Error("] Expected at $idx")
            }
            return Snail.Pair(lhs, rhs)
        }

        fun readNumber(): Snail {
            return Snail.Number(readChar().digitToInt())
        }

        fun readChar(): Char {
            val ch = line[idx]
            idx += 1
            return ch
        }

        fun cur(): Char {
            return line[idx]
        }
    }

    sealed interface Snail {
        data class Number(var n: Int) : Snail {
            override fun toString(): String {
                return n.toString()
            }
        }

        data class Pair(var lhs: Snail, var rhs: Snail) : Snail {
            override fun toString(): String {
                return "[$lhs,$rhs]"
            }
        }
    }

    fun explode(number: Snail, parents: List<Snail.Pair> = listOf()): Boolean {
        return when (number) {
            is Snail.Number -> false
            is Snail.Pair -> {
                if (parents.size >= 4) {
                    val lhs = prevNumber(number, parents)
                    if (lhs != null) {
                        lhs.n += (number.lhs as Snail.Number).n
                    }
                    val rhs = nextNumber(number, parents)
                    if (rhs != null) {
                        rhs.n += (number.rhs as Snail.Number).n
                    }
                    val p = parents.first()
                    val newNumber = Snail.Number(0)
                    if (p.lhs === number) {
                        p.lhs = newNumber
                    } else {
                        p.rhs = newNumber
                    }
                    true
                } else {
                    if (explode(number.lhs, listOf(number) + parents)) {
                        true
                    } else {
                        explode(number.rhs, listOf(number) + parents)
                    }
                }
            }
        }
    }

    fun split(number: Snail, parents: List<Snail.Pair> = listOf()): Boolean {
        return when (number) {
            is Snail.Number -> {
                if (number.n > 9) {
                    val p = parents.first()
                    val pair = Snail.Pair(Snail.Number(number.n / 2), Snail.Number(number.n - number.n / 2))
                    if (p.lhs === number) {
                        p.lhs = pair
                    } else {
                        p.rhs = pair
                    }
                    true
                } else {
                    false
                }
            }
            is Snail.Pair -> {
                if (split(number.lhs, listOf(number) + parents)) {
                    true
                } else {
                    split(number.rhs, listOf(number) + parents)
                }
            }
        }
    }

    fun reduce(number: Snail) {
        while (explode(number) || split(number)) {}
//        println(number)
    }

    fun mostLeft(node: Snail): Snail.Number {
        return when (node) {
            is Snail.Number -> node
            is Snail.Pair -> mostLeft(node.lhs)
        }
    }

    fun mostRight(node: Snail): Snail.Number {
        return when (node) {
            is Snail.Number -> node
            is Snail.Pair -> mostRight(node.rhs)
        }
    }

    fun isLeft(node: Snail, parents: List<Snail.Pair>): Boolean {
        return if (parents.isEmpty()) {
            false
        } else {
            parents.first().lhs === node
        }
    }

    fun isRight(node: Snail, parents: List<Snail.Pair>): Boolean {
        return if (parents.isEmpty()) {
            false
        } else {
            parents.first().rhs === node
        }
    }

    fun prevNumber(node: Snail, parents: List<Snail.Pair>): Snail.Number? {
        var cur = node
        var curParents = parents
        while (isLeft(cur, curParents)) {
            cur = curParents.first()
            curParents = curParents.drop(1)
        }
        return if (curParents.isEmpty()) {
            null
        } else {
            mostRight(curParents.first().lhs)
        }
    }

    fun nextNumber(node: Snail, parents: List<Snail.Pair>): Snail.Number? {
        var cur = node
        var curParents = parents
        while (isRight(cur, curParents)) {
            cur = curParents.first()
            curParents = curParents.drop(1)
        }
        return if (curParents.isEmpty()) {
            null
        } else {
            mostLeft(curParents.first().rhs)
        }
    }

    fun magnitute(number: Snail): Long {
        return when (number) {
            is Snail.Pair -> magnitute(number.lhs) * 3 + magnitute(number.rhs) * 2
            is Snail.Number -> number.n.toLong()
        }
    }

    override fun calc(input: List<Snail>): Long {
        var sum = input.first()
        for (i in 1 until input.size) {
            sum = Snail.Pair(sum, input[i])
            reduce(sum)
        }
//        println("Final: $sum")
        return magnitute(sum)
    }

    override fun calc2(input: List<Snail>): Long {
        var max = Long.MIN_VALUE
        for(lhs in input) {
            for(rhs in input) {
                if(lhs!=rhs) {
                    val sum = Reader("[$lhs,$rhs]").read() // simple deep copy
                    reduce(sum)
                    val cur = magnitute(sum)
                    if(max<cur) {
                        println("$lhs + $rhs")
                        max = cur
                    }
                }
            }
        }
        return max
    }
    override fun parse(content: String): List<Snail> {
        return content.split("\n").map { Reader(it).read() }
    }
}

fun main() {
    with(Aoc18) {
        println(
            calc2(
                parse(
                    """
[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
[[[5,[2,8]],4],[5,[[9,9],0]]]
[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
[[[[5,4],[7,7]],8],[[8,3],8]]
[[9,3],[[9,9],[6,[4,9]]]]
[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]
""".trimIndent()
                )
            )
        )
        val input = "aoc18.txt".asResource { content ->
            parse(content)
        }
//        println(calc(input)) // mutating input
        println(calc2(input))
    }
}
package me.aoc

import me.aoc.Aoc.Companion.asResource
import java.util.*

object Aoc10 : Aoc<List<String>, Long> {

    override fun calc(input: List<String>): Long {
        return input.map { firstInvalid(it) }.map { score(it) }.sumOf { it }.toLong()
    }

    fun firstInvalid(text: String): Char? {
        val stack = Stack<Char>()
        val match = mapOf('(' to ')', '<' to '>', '{' to '}', '[' to ']')
        for (ch in text) {
            if (ch in setOf('(', '[', '<', '{')) {
                stack.push(ch)
            } else {
                val open = stack.pop()
                if (match[open] != ch) {
                    return ch
                }
            }
        }
        return null
    }

    fun incomplete(text: String): String? {
        val stack = Stack<Char>()
        val match = mapOf('(' to ')', '<' to '>', '{' to '}', '[' to ']')
        for (ch in text) {
            if (ch in setOf('(', '[', '<', '{')) {
                stack.push(ch)
            } else {
                val open = stack.pop()
                if (match[open] != ch) {
                    return null
                }
            }
        }
        var result = ""
        while(!stack.isEmpty()) {
            val ch = stack.pop()
            result = result.plus(match[ch])
        }
        return result
    }

    fun score(ch: Char?): Int {
        return when (ch) {
            ')' -> 3
            ']' -> 57
            '}' -> 1197
            '>' -> 25137
            null -> 0
            else -> throw Error("Unexpected $ch")
        }
    }

    fun score(suffix: String?): Long {
        if (suffix == null)
            return 0L
        var result = 0L
        for (ch in suffix) {
            result *= 5
            result += when (ch) {
                ')' -> 1
                ']' -> 2
                '}' -> 3
                '>' -> 4
                else -> throw Error("Unexpected $ch")
            }
        }
        return result
    }

    override fun calc2(input: List<String>): Long {
        val scores = input.map { incomplete(it) }.map { score(it) }.filterNot { it == 0L }
        return scores.sorted()[scores.size / 2]
    }

    override fun parse(content: String): List<String> = content.split("\n")
}

fun main() {
    with(Aoc10) {
        println(
            calc2(
                parse(
                    """
[({(<(())[]>[[{[]{<()<>>
[(()[<>])]({[<{<<[]>>(
{([(<{}[<>[]}>{[]{[(<()>
(((({<>}<{<{<>}{[]{[]{}
[[<[([]))<([[{}[[()]]]
[{[{({}]{}}([{[{{{}}([]
{<[[]]>}<{[{[{[]{()[[[]
[<(<(<(<{}))><([]([]()
<{([([[(<>()){}]>(<<{{
<{([{{}}[<[[[<>{}]]]>[]]
""".trimIndent()
                )
            )
        )
        val input = "aoc10.txt".asResource { content ->
            parse(content)
        }
        println(calc(input))
        println(calc2(input))
    }
}
package me.aoc

import me.aoc.Aoc.Companion.asResource
import java.util.*

object Aoc3 : Aoc<List<List<Int>>, Long> {
    override fun calc(input: List<List<Int>>): Long {
        val row = input.first()
        val common = MutableList(row.size) { 0 }
        for (idx in row.indices) {
            for (j in input.indices)
                common[idx] += input[j][idx]
        }
        var freq = 0
        val half = input.size / 2
        var once = 0
        for (b in common) {
            once = once.shl(1).or(1)
            freq = if (b > half) {
                freq.shl(1).or(1)
            } else {
                freq.shl(1)
            }
        }
        return freq.xor(once).toLong() * freq.toLong()
    }

    fun toLong(row: List<Int>): Long {
        var freq = 0L
        var once = 0
        for (b in row) {
            once = once.shl(1).or(1)
            freq = if (b > 0) {
                freq.shl(1).or(1)
            } else {
                freq.shl(1)
            }
        }
        return freq
    }

    fun highFreqFilter(input: List<List<Int>>, idx: Int): List<List<Int>> {
        if (input.size == 1) {
            return input
        }
        var cnt = 0
        for (row in input) {
            cnt += row[idx]
        }
        return if (2 * cnt >= input.size) {
            highFreqFilter(input.filter { it[idx] == 1 }, idx + 1)
        } else {
            highFreqFilter(input.filter { it[idx] == 0 }, idx + 1)
        }
    }

    fun lowFreqFilter(input: List<List<Int>>, idx: Int): List<List<Int>> {
        if (input.size == 1) {
            return input
        }
        var cnt = 0
        for (row in input) {
            cnt += row[idx]
        }
        return if (2 * cnt < input.size) {
            lowFreqFilter(input.filter { it[idx] == 1 }, idx + 1)
        } else {
            lowFreqFilter(input.filter { it[idx] == 0 }, idx + 1)
        }
    }

    override fun calc2(input: List<List<Int>>): Long {
        val hi = highFreqFilter(input, 0)
        val low = lowFreqFilter(input, 0)
        return toLong(hi.first()) * toLong(low.first())
    }

    override fun parse(content: String): List<List<Int>> {
        return content.split("\n").map { it.toList().map { it.digitToInt() } }
    }
}

fun main() {
    with(Aoc3) {
        println(
            calc2(
                parse(
                    """
00100
11110
10110
10111
10101
01111
00111
11100
10000
11001
00010
01010
""".trimIndent()
                )
            )
        )
        val input = "aoc3.txt".asResource { content ->
            parse(content)
        }
        println(calc(input))
        println(calc2(input))
    }
}
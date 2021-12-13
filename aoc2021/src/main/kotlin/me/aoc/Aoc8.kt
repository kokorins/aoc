package me.aoc

import me.aoc.Aoc.Companion.asResource

object Aoc8 : Aoc<List<Aoc8.Display>, Long> {
    data class Display(val digits: List<String>, val output: List<String>) {
        val cache = mutableMapOf<Int, String>()
        fun deduce(digit: Int): String {
            if (cache.containsKey(digit)) {
                return cache.getValue(digit)
            }
            val pattern = when (digit) {
                1 -> digits.first { it.length == 2 }
                4 -> digits.first { it.length == 4 }
                7 -> digits.first { it.length == 3 }
                8 -> digits.first { it.length == 7 }
                3 -> digits.first { it.length == 5 && contains(it, deduce(1)) }
                6 -> digits.first { it.length == 6 && !contains(it, deduce(1)) }
                9 -> digits.first { it.length == 6 && contains(it, deduce(4)) }
                0 -> digits.first { it.length == 6 && contains(it, deduce(1)) && !contains(it, deduce(4)) }
                2 -> digits.first {
                    it.length == 5 && !contains(it, deduce(1)) && !contains(deduce(9), it)
                }
                5 -> digits.first {
                    it.length == 5 && !contains(it, deduce(1)) && contains(deduce(9), it)
                }

                else -> throw Error("Unknown $digit")
            }
            cache[digit] = pattern.toSortedSet().joinToString("")
//            println(cache)
//            println(digits)
            return cache.getValue(digit)
        }

        fun contains(patter: String, subpattern: String): Boolean {
            return patter.toSortedSet().intersect(subpattern.toSortedSet()) == subpattern.toSortedSet()
        }
    }

    override fun calc(input: List<Display>): Long {
        val digits = listOf(1, 4, 7, 8)
        var result = 0L
        for (display in input) {
            for (digit in digits) {
                result += display.output.count { it == display.deduce(digit) }
            }
        }
        return result
    }

    override fun calc2(input: List<Display>): Long {
        var result = 0L
        for (display in input) {

            for (digit in 0..9) {
                display.deduce(digit)
            }
            var number = 0L
            val mapping = display.cache.entries.associate { it.value to it.key }
            display.output.map { mapping.getValue(it) }.forEach {
                number *= 10
                number += it
            }
            result += number
        }
        return result
    }

    override fun parse(content: String): List<Display> {
        return content.split("\n").map {
            val (digits, numbers) = it.split("|").map { it.trim() }
            Display(
                digits.split(" ").map { it.toSortedSet().joinToString("") },
                numbers.split(" ").map { it.toSortedSet().joinToString("") })
        }
    }
}

fun main() {
    with(Aoc8) {
        println(
            calc2(
                parse(
                    """
be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe
edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec | fcgedb cgb dgebacf gc
fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef | cg cg fdcagb cbg
fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega | efabcd cedba gadfec cb
aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga | gecf egdcabf bgf bfgea
fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf | gebdcfa ecba ca fadegcb
dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf | cefg dcbef fcge gbcadfe
bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef
egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb
gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagce
""".trimIndent()
                )
            )
        )
        val input = "aoc8.txt".asResource { content ->
            parse(content)
        }
        println(calc(input))
        println(calc2(input))
    }
}
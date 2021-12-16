package me.aoc

import me.aoc.Aoc.Companion.asResource

object Aoc14 : Aoc<Aoc14.Input, Long> {
    data class Rule(val pattern: String, val result: Char)
    data class Rules(val mappings: Map<String, Char>) {
        companion object {
            fun of(rules: List<Rule>): Rules {
                return Rules(rules.associate {
                    val (pattern, result) = it
                    pattern to result
                })
            }
        }
    }

    data class Input(val template: String, val rules: List<Rule>)

    override fun calc(input: Input): Long {
        cache.clear()
        val (pattern, ruleSet) = input
        val rules = Rules.of(ruleSet)
        fun counts(pair: String, depth: Int): Map<Char, Long> {
            return if (depth == 0 || !rules.mappings.containsKey(pair)) {
                emptyMap()
            } else {
                if (!cache.containsKey(State(pair, depth))) {
                    val middle = rules.mappings.getValue(pair)
                    val lhs = counts(pair[0] + middle.toString(), depth - 1)
                    val rhs = counts(middle + pair[1].toString(), depth - 1)
                    val result = merge(merge(lhs, rhs), mapOf(middle to 1))
                    cache[State(pair, depth)] = result
                }
                return cache.getValue(State(pair, depth))
            }
        }

        var result = mapOf(pattern[0] to 1L)
        for (i in 1 until pattern.length) {
            val pair = pattern.substring((i - 1)..i)
            result = merge(merge(result, counts(pair, 10)), mapOf(pattern[i] to 1))
        }
        val min = result.minOf { it.value }
        val max = result.maxOf { it.value }
        return (max - min).toLong()
    }

    data class State(val pair: String, val level: Int)

    val cache = mutableMapOf<State, Map<Char, Long>>()

    fun merge(lhs: Map<Char, Long>, rhs: Map<Char, Long>): Map<Char, Long> {
        val keys = lhs.keys.union(rhs.keys)
        val result = mutableMapOf<Char, Long>()
        for (k in keys) {
            result[k] = lhs.getOrDefault(k, 0L) + rhs.getOrDefault(k, 0L)
        }
        return result
    }

    override fun calc2(input: Input): Long {
        cache.clear()
        val (pattern, ruleSet) = input
        val rules = Rules.of(ruleSet)
        fun counts(pair: String, depth: Int): Map<Char, Long> {
            return if (depth == 0 || !rules.mappings.containsKey(pair)) {
                emptyMap()
            } else {
                if (!cache.containsKey(State(pair, depth))) {
                    val middle = rules.mappings.getValue(pair)
                    val lhs = counts(pair[0] + middle.toString(), depth - 1)
                    val rhs = counts(middle + pair[1].toString(), depth - 1)
                    val result = merge(merge(lhs, rhs), mapOf(middle to 1))
                    cache[State(pair, depth)] = result
                }
                return cache.getValue(State(pair, depth))
            }
        }

        var result = mapOf(pattern[0] to 1L)
        for (i in 1 until pattern.length) {
            val pair = pattern.substring((i - 1)..i)
            result = merge(merge(result, counts(pair, 40)), mapOf(pattern[i] to 1))
        }
        val min = result.minOf { it.value }
        val max = result.maxOf { it.value }
        return (max - min).toLong()
    }

    override fun parse(content: String): Input {
        val (template, ruleSet) = content.split("\n\n")
        val rules = ruleSet.split("\n").map {
            val (pattern, result) = it.split(" -> ".toRegex())
            Rule(pattern, result.first())
        }
        return Input(template, rules)
    }
}

fun main() {
    with(Aoc14) {
        println(
            calc2(
                parse(
                    """
NNCB

CH -> B
HH -> N
CB -> H
NH -> C
HB -> C
HC -> B
HN -> C
NN -> C
BH -> H
NC -> B
NB -> B
BN -> B
BB -> N
BC -> B
CC -> N
CN -> C
""".trimIndent()
                )
            )
        )
        val input = "aoc14.txt".asResource { content ->
            parse(content)
        }
        println(calc(input))
        println(calc2(input))
    }
}
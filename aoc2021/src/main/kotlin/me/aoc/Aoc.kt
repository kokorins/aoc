package me.aoc

import com.google.common.io.Resources

/**
 * Advent of Code
 */
interface Aoc<I, O> {
    fun calc(input: I): O
    fun calc2(input: I): O
    fun parse(content: String): I

    companion object {
        fun <T> String.asResource(f: (String) -> T): T {
            val content = Resources.getResource(this).readText()
            return f(content)
        }
    }
}
package me.aoc

import me.aoc.Aoc.Companion.asResource
import java.math.BigInteger

object Aoc16 : Aoc<List<Int>, Long> {
    sealed interface Packet {
        data class Literal(override val version: Int, override val type: Int, val value: Long) : Packet
        data class Operator(override val version: Int, override val type: Int, val sub: List<Packet>) : Packet

        val version: Int
        val type: Int
    }

    data class Reader(val input: List<Int>) {
        var idx = 0
        fun read(): Packet {
            val version = readVersion()
            val type = readType()
            if (type == 4) {
                val value = readValue()
                return Packet.Literal(version, type, value.toLong())
            } else {
                val lengthId = readLengthId()
                val sub = mutableListOf<Packet>()
                when (lengthId) {
                    0 -> {
                        val length = readBits(15)
//                        println("V: $version, T: $type, LId: $lengthId, L: $length")
                        val cur = idx
                        while (idx < cur + length) {
                            sub.add(read())
                        }
                    }
                    1 -> {
                        val length = readBits(11)
//                        println("LId: $lengthId, L: $length")
                        for (i in 1..length) {
                            sub.add(read())
                        }
                    }
                }
                return Packet.Operator(version, type, sub)
            }
        }

        fun readVersion(): Int {
            return readBits(3).toInt()
        }

        fun readType(): Int {
            return readBits(3).toInt()
        }

        fun readValue(): Long {
            var result = 0L
            while (input[idx] == 1) {
                idx += 1
                result = readBits(4, result)
            }
            idx += 1
            return readBits(4, result)
        }

        fun readLengthId(): Int {
            return readBits(1).toInt()
        }

        fun readBits(num: Int, prefix: Long = 0): Long {
            return if (num == 0) {
                prefix
            } else {
                val newPrefix = prefix.shl(1).or(input[idx].toLong())
                ++idx
                readBits(num - 1, newPrefix)
            }
        }
    }

    fun sumVersions(packet: Packet): Long {
        return when (packet) {
            is Packet.Literal -> packet.version.toLong()
            is Packet.Operator -> packet.version + packet.sub.sumOf { sumVersions(it) }
        }
    }

    fun eval(packet: Packet): BigInteger {
        return when (packet) {
            is Packet.Literal -> BigInteger.valueOf(packet.value)
            is Packet.Operator -> {
                val subs = packet.sub.map { eval(it) }
                when (packet.type) {
                    0 -> {
                        var result = BigInteger.ZERO
                        for (s in subs) {
                            result += s
                        }
                        result
                    }
                    1 -> {
                        var result = BigInteger.ONE
                        for (s in subs) {
                            result *= s
                        }
                        result
                    }
                    2 -> subs.minOrNull() ?: throw Error("Expected subs")
                    3 -> subs.maxOrNull() ?: throw Error("Expected subs")
                    5 -> if (subs[0] > subs[1]) BigInteger.ONE else BigInteger.ZERO
                    6 -> if (subs[0] < subs[1]) BigInteger.ONE else BigInteger.ZERO
                    7 -> if (subs[0] == subs[1]) BigInteger.ONE else BigInteger.ZERO
                    else -> throw Error("Unexpected ${packet.type}")
                }
            }
        }
    }

    override fun calc(input: List<Int>): Long {
        val packet = Reader(input).read()
        return sumVersions(packet)
    }

    override fun calc2(input: List<Int>): Long {
        val packet = Reader(input).read()
        val result = eval(packet)
//        println(result)
        return result.toLong()
    }

    override fun parse(content: String): List<Int> {
        val result = content.flatMap {
            it.digitToInt(16).toString(2).padStart(4, '0').toList()
        }.map { it.digitToInt() }
        return result
    }
}

fun main() {
    with(Aoc16) {
        println(
            calc2(
                parse(
                    """
9C0141080250320F1802104A08
""".trimIndent()
                )
            )
        )
        val input = "aoc16.txt".asResource { content ->
            parse(content)
        }
        println(calc(input))
        println(calc2(input))
    }
}
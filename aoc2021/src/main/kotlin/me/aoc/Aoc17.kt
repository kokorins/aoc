package me.aoc

import me.aoc.Aoc.Companion.asResource
import java.lang.StrictMath.*

object Aoc17 : Aoc<Aoc17.Area, Long> {
    data class TimeSpeed(val step: Int, val v: Int)
    data class Area(val xMin: Int, val xMax: Int, val yMin: Int, val yMax: Int)

    override fun calc(input: Area): Long {
        val xSteps = xSteps(input.xMax)
        val ySteps = ySteps(input.yMin)
        val sameSteps = mutableListOf<TimeSpeed>()
        for (validX in input.xMin..input.xMax) {
            val stepXs = xSteps.getValue(validX)
            for (step in stepXs) {
                if (step.v == 0) {
                    for (validY in input.yMin..input.yMax) {
                        val stepYs = ySteps.getValue(validY)
                        sameSteps.addAll(stepYs.filter { it.step >= step.step })
                    }
                } else { // step.v>0
                    for (validY in input.yMin..input.yMax) {
                        val stepYs = ySteps.getValue(validY)
                        sameSteps.addAll(stepYs.filter { it.step == step.step })
                    }
                }
            }
        }

        return highests(sameSteps)
    }

    fun highests(ySteps: List<TimeSpeed>): Long {
        return ySteps.maxOfOrNull {
            var speed = it.step + it.v
            var pos = 0L
            while (speed > 0) {
                pos += speed
                speed -= 1
            }
            pos
        } ?: 0L
    }


    fun xSteps(maxDist: Int): Map<Int, List<TimeSpeed>> {
        val xCache = mutableMapOf<Int, MutableList<TimeSpeed>>()
        for (initialSpeed in 1..2 * maxDist) {
            xCache[initialSpeed] = mutableListOf()
        }
        for (initialSpeed in 1..maxDist) {
            var curSpeed = initialSpeed
            var dist = 0
            var step = 0
            while (dist <= maxDist && curSpeed >= 0) {
                step += 1
                dist += curSpeed
                curSpeed -= 1
                xCache.getValue(dist).add(TimeSpeed(step, curSpeed))
            }
        }
        return xCache
    }

    fun ySteps(level: Int): Map<Int, List<TimeSpeed>> {
        val yCache = mutableMapOf<Int, MutableList<TimeSpeed>>()
        for (initialSpeed in level..abs(level)) {
            var curSpeed = initialSpeed
            var depth = 0
            var step = 0
            while (depth >= level) {
                step += 1
                depth += curSpeed
                curSpeed -= 1

                yCache.putIfAbsent(depth, mutableListOf())
                yCache.getValue(depth).add(TimeSpeed(step, curSpeed))
            }
        }
        return yCache
    }

    override fun calc2(input: Area): Long {
        val xSteps = xSteps(input.xMax)
        val ySteps = ySteps(input.yMin)
//        val sameSteps = mutableListOf<TimeSpeed>()
        val speeds = mutableSetOf<Pair<Int, Int>>()
        for (validX in input.xMin..input.xMax) {
            val stepXs = xSteps.getValue(validX)
            for (step in stepXs) {
                if (step.v == 0) {
                    for (validY in input.yMin..input.yMax) {
                        val stepYs = ySteps.getValue(validY)
                        speeds.addAll(stepYs.filter { it.step >= step.step }.map { (step.v+step.step) to (it.step + it.v) })
                    }
                } else { // step.v>0
                    for (validY in input.yMin..input.yMax) {
                        val stepYs = ySteps.getValue(validY)
                        speeds.addAll(stepYs.filter { it.step == step.step }.map { (step.v+step.step) to (it.step + it.v) })
                    }
                }
            }
        }
        return speeds.size.toLong()
    }

    override fun parse(content: String): Area {
        val results = "((-)*\\d)+".toRegex().findAll(content)
        val numbers = mutableListOf<Int>()
        for (result in results) {
            numbers.add(result.value.toInt())
        }
        return Area(abs(min(numbers[0], numbers[1])), abs(max(numbers[0], numbers[1])), numbers[2], numbers[3])
    }
}

fun main() {
    with(Aoc17) {
        println(
            calc2(
                parse(
                    """
target area: x=20..30, y=-10..-5
""".trimIndent()
                )
            )
        )
        val input = "aoc17.txt".asResource { content ->
            parse(content)
        }
        println(calc(input))
        println(calc2(input))
    }
}
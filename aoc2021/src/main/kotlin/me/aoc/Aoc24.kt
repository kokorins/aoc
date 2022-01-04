package me.aoc

import me.aoc.Aoc.Companion.asResource

object Aoc24 : Aoc<List<Aoc24.Op>, Long> {
    sealed interface Addend {
        data class Number(val number: Int) : Addend
        data class Var(val name: String) : Addend
        companion object {
            fun from(text: String): Addend {
                return if (text in listOf("w", "x", "y", "z")) {
                    Var(text)
                } else {
                    Number(text.toInt())
                }
            }
        }
    }

    sealed interface Op {
        data class In(override val variable: String) : Op
        sealed interface BiOp : Op {
            data class Add(override val variable: String, override val addend: Addend) : BiOp
            data class Mul(override val variable: String, override val addend: Addend) : BiOp
            data class Div(override val variable: String, override val addend: Addend) : BiOp
            data class Mod(override val variable: String, override val addend: Addend) : BiOp
            data class Eql(override val variable: String, override val addend: Addend) : BiOp

            val addend: Addend
        }

        val variable: String
    }

    data class Program(val ops: List<Op>) {
        val variables = setOf("x", "y", "z", "w")
        fun value(vars: Map<String, Long>, add: Addend): Long {
            when (add) {
                is Addend.Var -> return vars.getValue(add.name)
                is Addend.Number -> return add.number.toLong()
            }
        }

        fun splitInputs(): List<IntRange> {
            val results = mutableListOf<IntRange>()
            var start = 0
            for (line in ops.indices) {
                val op = ops[line]
                if (op is Op.In && start < line) {
                    results.add(start until line)
                    start = line
                }
            }
            if (start < ops.size) {
                results.add(start until ops.size)
            }
            return results
        }

        fun run(input: List<Int>, range: IntRange, init: Map<String, Long>): Map<String, Long> {
            var idx = 0
            val vars = init.toMutableMap()
            for (line in range) {
                when (val op = ops[line]) {
                    is Op.In -> {
                        vars[op.variable] = input[idx].toLong()
                        idx += 1
                    }
                    is Op.BiOp.Add -> {
                        vars[op.variable] = vars.getValue(op.variable) + value(vars, op.addend)
                    }
                    is Op.BiOp.Mul -> {
                        vars[op.variable] = vars.getValue(op.variable) * value(vars, op.addend)
                    }
                    is Op.BiOp.Mod -> {
                        val value = vars.getValue(op.variable)
                        val addend = value(vars, op.addend)
                        if (value < 0 || addend <= 0)
                            throw Error("Mod")
                        vars[op.variable] = vars.getValue(op.variable) % value(vars, op.addend)
                    }
                    is Op.BiOp.Div -> {
                        val addend = value(vars, op.addend)
                        if (addend == 0L)
                            throw Error("Division")
                        vars[op.variable] = vars.getValue(op.variable) / addend
                    }
                    is Op.BiOp.Eql -> {
                        vars[op.variable] = if (vars.getValue(op.variable) == value(vars, op.addend)) {
                            1L
                        } else {
                            0L
                        }
                    }
                }
            }
            return vars
        }
    }

    fun ofZ(z: Long): Map<String, Long> {
        return mapOf("z" to z, "x" to 0L, "y" to 0L, "w" to 0L)
    }

    fun runZero(prefix: List<Int>, ranges: List<IntRange>, init: Map<String, Long>, program: Program): List<Int> {
        val idx = prefix.size
        if (idx == 14) {
            if (init.getValue("z") == 0L) {
                println("${prefix.joinToString("")} = ${init.getValue("z")}")
            }
            return prefix
        }
        val divZOp = (program.ops[ranges[idx].start + 4] as Op.BiOp).addend as Addend.Number
        if (divZOp.number == 26) {
            for (i in 9 downTo 1) {
                val run = program.run(listOf(i), ranges[idx], init)
                if (run.getValue("x") == 0L) {
                    runZero(prefix.plus(i), ranges, run, program)
                }
            }
        } else {
            for (i in 9 downTo 1) {
                val run = program.run(listOf(i), ranges[idx], init)
                runZero(prefix.plus(i), ranges, run, program)
            }
        }
        return listOf()
    }

    override fun calc(input: List<Op>): Long {
        val program = Program(input)
        val ranges = program.splitInputs()
        runZero(listOf(), ranges, ofZ(0L), program)
        return 0L
    }

    fun split(value: Long, prefix: List<Int> = listOf()): List<Int> {
        return if (value == 0L) {
            prefix.reversed()
        } else {
            split(value / 10, prefix.plus((value % 10).toInt()))
        }
    }

    override fun calc2(input: List<Op>): Long = TODO()
    override fun parse(content: String): List<Op> {
        return content.split("\n").map {
            val op = it.split(" ")
            when (op[0]) {
                "inp" -> Op.In(op[1])
                "add" -> Op.BiOp.Add(op[1], Addend.from(op[2]))
                "mul" -> Op.BiOp.Mul(op[1], Addend.from(op[2]))
                "mod" -> Op.BiOp.Mod(op[1], Addend.from(op[2]))
                "div" -> Op.BiOp.Div(op[1], Addend.from(op[2]))
                "eql" -> Op.BiOp.Eql(op[1], Addend.from(op[2]))
                else -> throw Error("Unexpected: $op")
            }
        }
    }
}

fun main() {
    with(Aoc24) {
//        println(
//            calc(
//                parse(
//                    """
//inp w
//add z w
//mod z 2
//div w 2
//add y w
//mod y 2
//div w 2
//add x w
//mod x 2
//div w 2
//mod w 2
//""".trimIndent()
//                )
//            )
//        )
        val input = "aoc24.txt".asResource { content ->
            parse(content)
        }
        println(calc(input))
//        println(calc2(input))
    }
}
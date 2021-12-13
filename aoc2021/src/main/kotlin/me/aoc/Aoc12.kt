package me.aoc

import me.aoc.Aoc.Companion.asResource

object Aoc12 : Aoc<Aoc12.Graph, Long> {
    data class Node(val id: String) {
        fun once(): Boolean = id.lowercase() == id

        companion object {
            val start = Node("start")
            val end = Node("end")
        }
    }

    data class Edge(val lhs: Node, val rhs: Node)
    data class Graph(val neighs: Map<Node, List<Node>>) {
        companion object {
            fun of(edges: List<Edge>): Graph {
                val neigs = mutableMapOf<Node, MutableList<Node>>()
                for (edge in edges) {
                    val (lhs, rhs) = edge
                    if (!neigs.containsKey(lhs)) {
                        neigs[lhs] = mutableListOf()
                    }
                    if (!neigs.containsKey(rhs)) {
                        neigs[rhs] = mutableListOf()
                    }
                    neigs.getValue(lhs).add(rhs)
                    neigs.getValue(rhs).add(lhs)
                }
                return Graph(neigs)
            }
        }
    }

    override fun calc(input: Graph): Long {
        fun paths(cur: Node, visited: Set<Node>, numWays: Long): Long {
            return if (cur == Node.end) {
                numWays
            } else {
                var result = 0L
                val neigs = input.neighs.getValue(cur)
                for (n in neigs) {
                    if (!n.once() || n !in visited) {
                        result += paths(n, visited.plus(n), numWays)
                    }
                }
                result
            }
        }
        return paths(Node.start, setOf(Node.start), 1L)
    }

    override fun calc2(input: Graph): Long {
        fun paths(cur: Node, visited: Set<Node>, spot: Boolean): Long {
            return if (cur == Node.end) {
                1
            } else {
                var result = 0L
                val neigs = input.neighs.getValue(cur)
                for (n in neigs) {
                    if (n !in setOf(Node.start, Node.end)) {
                        if (!n.once()) {
                            result += paths(n, visited.plus(n), spot)
                        } else { // once nodes
                            if (n in visited && spot) { // small twice
                                result += paths(n, visited, false)
                            } else if (n !in visited) {
                                result += paths(n, visited.plus(n), spot)
                            }
                        }
                    } else if (n !in visited) {
                        result += paths(n, visited.plus(n), spot)
                    }
                }
                result
            }
        }
        return paths(Node.start, setOf(Node.start), true)
    }

    override fun parse(content: String): Graph {
        return Graph.of(content.split("\n").map {
            val (lhs, rhs) = it.split("-")
            Edge(Node(lhs), Node(rhs))
        })
    }
}

fun main() {
    with(Aoc12) {
        println(
            calc2(
                parse(
                    """
dc-end
HN-start
start-kj
dc-start
dc-HN
LN-dc
HN-end
kj-sa
kj-HN
kj-dc
""".trimIndent()
                )
            )
        )
        val input = "aoc12.txt".asResource { content ->
            parse(content)
        }
        println(calc(input))
        println(calc2(input))
    }
}
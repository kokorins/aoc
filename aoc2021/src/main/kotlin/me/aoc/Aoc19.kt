package me.aoc

import me.aoc.Aoc.Companion.asResource
import org.jetbrains.kotlinx.multik.api.identity
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.linalg.inv
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarrayOf
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.jetbrains.kotlinx.multik.ndarray.operations.map
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import java.lang.StrictMath.abs

object Aoc19 : Aoc<List<Aoc19.Scanner>, Long> {
    data class Point(val x: Int, val y: Int, val z: Int) {
        constructor(xs: List<Int>) : this(xs[0], xs[1], xs[2])
        constructor(xs: D1Array<Int>) : this(xs[0], xs[1], xs[2])

        fun toArray() = mk.ndarrayOf(x, y, z)
    }

    data class Scanner(val id: String, val beacons: List<Point>)

    override fun calc(input: List<Scanner>): Long {
        val matches = mutableListOf<CoordMatch>()
        for (i in input.indices) {
            val lhs = input[i]
            for (j in i + 1 until input.size) {
                val rhs = input[j]
                val coordMatch = tryMatch(lhs, rhs, 12)
                if (coordMatch != null) {
                    matches.add(coordMatch)
                }
            }
        }
        val absCoords = fixCoords(matches)
        val results = mutableSetOf<Point>()
        for (scanner in input) {
            for (beacon in scanner.beacons) {
                results.add(absCoords.getValue(scanner).transform(beacon))
            }
        }
        return results.size.toLong()
    }

    interface Transformer {
        fun transform(pt: Point): Point
        fun inv(): Transformer

        companion object {
            data class Sequential(val first: Transformer, val second: Transformer) : Transformer {
                override fun transform(pt: Point): Point = second.transform(first.transform(pt))
                override fun inv(): Transformer = Sequential(second.inv(), first.inv())
            }
        }
    }

    data class Inv(val shift: D1Array<Int>, val rot: D2Array<Int>) : Transformer {
        override fun transform(pt: Point): Point {
            return Point(mk.linalg.dot(rot, pt.toArray()).plus(shift))
        }

        override fun inv(): Transformer = Transition(shift.times(-1), mk.linalg.inv(rot).map { it.toInt() })
    }

    data class Transition(val shift: D1Array<Int>, val rot: D2Array<Int>) : Transformer {
        override fun transform(pt: Point): Point {
            return Point(mk.linalg.dot(rot, pt.toArray().plus(shift)))
        }

        override fun inv(): Transformer = Inv(shift.times(-1), mk.linalg.inv(rot).map { it.toInt() })

        companion object {
            fun rotations(): List<D2Array<Int>> {
                val zero = mk.zeros<Int>(3, 3)
                val results = mutableListOf<D2Array<Int>>()
                for (i in listOf(1, -1)) {
                    for (j in listOf(1, -1)) {
                        for (k in listOf(1, -1)) {
                            val rotXYZ = zero.copy()
                            rotXYZ[0, 0] = i
                            rotXYZ[1, 1] = j
                            rotXYZ[2, 2] = k
                            results.add(rotXYZ)

                            val rotXZY = zero.copy()
                            rotXZY[0, 0] = i
                            rotXZY[1, 2] = j
                            rotXZY[2, 1] = k
                            results.add(rotXZY)

                            val rotYZX = zero.copy()
                            rotYZX[0, 2] = i
                            rotYZX[1, 0] = j
                            rotYZX[2, 1] = k
                            results.add(rotYZX)

                            val rotYXZ = zero.copy()
                            rotYXZ[0, 1] = i
                            rotYXZ[1, 0] = j
                            rotYXZ[2, 2] = k
                            results.add(rotYXZ)

                            val rotZXY = zero.copy()
                            rotZXY[0, 1] = i
                            rotZXY[1, 2] = j
                            rotZXY[2, 0] = k
                            results.add(rotZXY)

                            val rotZYX = zero.copy()
                            rotZYX[0, 2] = i
                            rotZYX[1, 1] = j
                            rotZYX[2, 0] = k
                            results.add(rotZYX)
                        }
                    }
                }

                return results
            }
        }
    }


    data class CoordMatch(val lhs: Scanner, val rhs: Scanner, val transition: Transformer)

    fun tryMatch(lhs: Scanner, rhs: Scanner, minBeacons: Int): CoordMatch? {
        for (lBeacon in lhs.beacons) {
            val lShift = lBeacon.toArray()
            val lTrans = Transition(lShift * -1, mk.identity(3))
            val lPoints = lhs.beacons.map { lTrans.transform(it) }.toSet()
            for (rBeacon in rhs.beacons) {
                val rShift = rBeacon.toArray()
                for (rot in Transition.rotations()) {
                    val rTrans = Transition(rShift.times(-1), rot)
                    val rPoints = rhs.beacons.map { rTrans.transform(it) }.toSet()
                    if (lPoints.intersect(rPoints).size >= minBeacons) {
                        val result =
                            CoordMatch(
                                lhs,
                                rhs,
                                Transformer.Companion.Sequential(rTrans, lTrans.inv())
                            ) // rhs to lhs coordinates
                        println(
                            "Match: ${result.lhs.id} ${result.rhs.id} with center: ${
                                result.transition.transform(
                                    Point(0, 0, 0)
                                )
                            }"
                        )
                        return result
                    }
                }
            }
        }
        return null
    }

    fun fixCoords(coordMatches: List<CoordMatch>): Map<Scanner, Transformer> {
        val match = coordMatches.first()
        val result = mutableMapOf<Scanner, Transformer>(match.lhs to Transition(mk.ndarrayOf(0, 0, 0), mk.identity(3)))
        val scanners = coordMatches.flatMap { listOf(it.lhs.id, it.rhs.id) }.toMutableList()
        scanners.remove(match.lhs.id)
        while (scanners.isNotEmpty()) {
            val id = scanners.first()
            scanners.remove(id)
            val match = coordMatches.firstOrNull { it.rhs.id == id && it.lhs in result.keys }
            if (match != null) {
                val trans = result.getValue(match.lhs)
                result[match.rhs] = Transformer.Companion.Sequential(match.transition, trans)
                println("Center ${id}: ${result.getValue(match.rhs).transform(Point(0, 0, 0))}")
            } else {
                val match = coordMatches.firstOrNull { it.lhs.id == id && it.rhs in result.keys }
                if (match != null) {
                    val trans = result.getValue(match.rhs)
                    result[match.lhs] = Transformer.Companion.Sequential(match.transition.inv(), trans)
                    println("Center with inv ${id}: ${result.getValue(match.lhs).transform(Point(0, 0, 0))}")
                } else {
                    println("Cant find anything for $id")
                    scanners.add(id)
                }
            }
        }
//        println(result)
        return result
    }


    override fun calc2(input: List<Scanner>): Long {
        val matches = mutableListOf<CoordMatch>()
        for (i in input.indices) {
            val lhs = input[i]
            for (j in i + 1 until input.size) {
                val rhs = input[j]
                val coordMatch = tryMatch(lhs, rhs, 12)
                if (coordMatch != null) {
                    matches.add(coordMatch)
                }
            }
        }
        val absCoords = fixCoords(matches)
        val results = mutableSetOf<Point>()
        for (scanner in input) {
            for (beacon in scanner.beacons) {
                results.add(absCoords.getValue(scanner).transform(Point(0, 0, 0)))
            }
        }
        var result = 0L
        for (l in results) {
            for (r in results) {
                val cur = abs(l.x - r.x) + abs(l.y - r.y) + abs(l.z - r.z)
                if (cur > result)
                    result = cur.toLong()
            }
        }
        return result
    }

    override fun parse(content: String): List<Scanner> {
        return content.split("\n\n").map { scannerBlock ->
            val scannerLines = scannerBlock.split("\n")
            val name = scannerLines[0].trim('-', ' ')
            val beacons = scannerLines.drop(1).map { pt -> Point(pt.split(",").map { it.toInt() }) }
            Scanner(name, beacons)
        }
    }
}

fun main() {
    with(Aoc19) {
        println(
            calc2(
                parse(
                    """
--- scanner 0 ---
404,-588,-901
528,-643,409
-838,591,734
390,-675,-793
-537,-823,-458
-485,-357,347
-345,-311,381
-661,-816,-575
-876,649,763
-618,-824,-621
553,345,-567
474,580,667
-447,-329,318
-584,868,-557
544,-627,-890
564,392,-477
455,729,728
-892,524,684
-689,845,-530
423,-701,434
7,-33,-71
630,319,-379
443,580,662
-789,900,-551
459,-707,401

--- scanner 1 ---
686,422,578
605,423,415
515,917,-361
-336,658,858
95,138,22
-476,619,847
-340,-569,-846
567,-361,727
-460,603,-452
669,-402,600
729,430,532
-500,-761,534
-322,571,750
-466,-666,-811
-429,-592,574
-355,545,-477
703,-491,-529
-328,-685,520
413,935,-424
-391,539,-444
586,-435,557
-364,-763,-893
807,-499,-711
755,-354,-619
553,889,-390

--- scanner 2 ---
649,640,665
682,-795,504
-784,533,-524
-644,584,-595
-588,-843,648
-30,6,44
-674,560,763
500,723,-460
609,671,-379
-555,-800,653
-675,-892,-343
697,-426,-610
578,704,681
493,664,-388
-671,-858,530
-667,343,800
571,-461,-707
-138,-166,112
-889,563,-600
646,-828,498
640,759,510
-630,509,768
-681,-892,-333
673,-379,-804
-742,-814,-386
577,-820,562

--- scanner 3 ---
-589,542,597
605,-692,669
-500,565,-823
-660,373,557
-458,-679,-417
-488,449,543
-626,468,-788
338,-750,-386
528,-832,-391
562,-778,733
-938,-730,414
543,643,-506
-524,371,-870
407,773,750
-104,29,83
378,-903,-323
-778,-728,485
426,699,580
-438,-605,-362
-469,-447,-387
509,732,623
647,635,-688
-868,-804,481
614,-800,639
595,780,-596

--- scanner 4 ---
727,592,562
-293,-554,779
441,611,-461
-714,465,-776
-743,427,-804
-660,-479,-426
832,-632,460
927,-485,-438
408,393,-506
466,436,-512
110,16,151
-258,-428,682
-393,719,612
-211,-452,876
808,-476,-593
-575,615,604
-485,667,467
-680,325,-822
-627,-443,-432
872,-547,-609
833,512,582
807,604,487
839,-516,451
891,-625,532
-652,-548,-490
30,-46,-14
""".trimIndent()
                )
            )
        )
        /**
         * Centers:
         * 1. 68,-1246,-43
         * 2. 1105,-1205,1229
         * 3. -92,-2380,-20
         * 4. -20,-1133,1061
         */
        val input = "aoc19.txt".asResource { content ->
            parse(content)
        }
//        println(calc(input))
        println(calc2(input))
    }
}
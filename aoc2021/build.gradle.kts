plugins {
    kotlin("jvm") version "1.6.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.guava:guava:30.1-jre")
}

val numSolved = 24
for(i in 1..numSolved) {
    task("$i", JavaExec::class) {
        main = "me.aoc.Aoc${i}Kt"
        classpath = sourceSets["main"].runtimeClasspath
        standardInput = System.`in`
    }
}
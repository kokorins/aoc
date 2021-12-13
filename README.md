# Advent of Code

## Adding new Day

1. Create/Download personal input to `resources/aoc##.txt`
2. Copy `Aoc0.kt` to `Aoc##.kt` where `##` day
3. Rename class `Aoc0` to Aoc##.
4. Define input type argument, `Long` is good enough output time for almost all cases.
5. Insert test example and rename resource file in use.

## Build

> gradle aoc####:##

where `####:##` is `year:day`
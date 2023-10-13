package io.github.opletter.courseevals.fsu

import io.github.opletter.courseevals.common.*
import io.github.opletter.courseevals.common.data.*
import java.io.File

val campusMap = mapOf(
    "Tallahassee Main Campus" to "Main",
    "Florida State University" to "Main",
    "Sarasota Campus" to "Main",
    "Panama City Campus" to "Pnm",
    "International Campuses" to "Intl",
    "Main" to "Tallahassee Main Campus",
    "Pnm" to "Panama City Campus",
    "Intl" to "International Campuses",
)

// CoursePrefixes.txt comes from https://registrar.fsu.edu/bulletin/undergraduate/information/course_prefix/
fun getDeptNames(writeDir: String): Map<String, String> {
    val coursePrefixHTML = readResource("CoursePrefixes.txt")
        .split("<tr class=\"TableAllLeft\">")
        .drop(2)
        .associate { row ->
            row
                .split("<span class=\"pTables9pt\">")
                .drop(1)
                .map { it.substringBefore("</span>") }
                .take(2)
                .let { it[0] to it[1] }
        } + ("IFS" to "Independent Florida State")
    makeFileAndDir("$writeDir/dept-names.json").writeAsJson(coursePrefixHTML.toSortedMap().toMap())
    return coursePrefixHTML
}

fun organizeReports(readDir: String, writeDir: String): SchoolDeptsMap<List<Report>> {
    val list = readResource("Areas.txt").lines().map { AreaEntry.fromString(it) }

    val uniqueCodes = list.groupBy({ it.code }, { it.code }).filter { it.value.size == 1 }.keys

    val nodes = buildTree(list)
    val childParentMap = buildChildParentMap(nodes, uniqueCodes)
        .onEach { println(it) }

    return CourseSearchKeys
        .flatMap { File("$readDir/$it.json").decodeJson<List<Report>>() }
        .groupBy { report ->
            val newArea = report.area
                .replace("HSFCS-", "HSFCS - ")
                .replace("ASCOP-", "ASCOP - ")
                .replace("HSRMP -", "ETRMP - ")
                .replace("  ", " ")
                .filter { it != ',' }
                .let { childParentMap[it] ?: childParentMap[it.substringBefore(" -")] ?: it }
            campusMap[newArea] ?: error("Unknown area: $newArea")
        }.mapValues { (_, keys) ->
            keys
                .distinctBy { it.ids }
                .groupBy { it.courseCode.take(3) }
        }.writeToFiles(writeDir)
}

fun getStatsByProf(
    readDir: String,
    writeDir: String,
    includeQuestions: List<Int> = QuestionsLimited.indices - setOf(0, 3, 4, 11),
): SchoolDeptsMap<Map<String, InstructorStats>> {
    return getCompleteSchoolDeptsMap<List<Report>>(readDir).mapEachDept { _, _, reports ->
        val allNames = reports.map { it.htmlInstructor.uppercase() }.toSet() - ""

        val nameMappings = allNames.sorted().flatMap { name ->
            val (last, first) = name.split(", ")
            val lastParts = last.split(" ", "-")
            val matching = allNames.filter { otherName ->
                val (otherLast, otherFirst) = otherName.split(", ")
                val otherLastParts = otherLast.split(" ", "-")
                (first.startsWith(otherFirst) || otherFirst.startsWith(first)) &&
                        (otherLastParts.any { it in lastParts } || lastParts.any { it in otherLastParts })
            }
            val chosen = matching.maxByOrNull { it.length } ?: return@flatMap emptyList()
            matching.map { it to chosen }
        }.toMap()

        reports
            .filter { it.htmlInstructor.uppercase().isNotBlank() }
            .groupBy {
                nameMappings[it.htmlInstructor.uppercase()]
                    ?: error("${it.htmlInstructor.uppercase()}\n${nameMappings}")
            }.mapValues { (_, reports) ->
                val filteredReports = reports
                    .filter { report ->
                        includeQuestions.all { it in report.ratings }
                    }.takeIf { it.isNotEmpty() }
                    ?: return@mapValues null

                InstructorStats(
                    lastSem = reports.maxOf {
                        val term = it.term.split(" ").reversed().joinToString(" ")
                        Semester.Triple.valueOf(term).numValue
                    },
                    overallStats = filteredReports.getTotalRatings(includeQuestions),
                    courseStats = filteredReports.flatMap { report ->
                        "[A-Z]{3}\\d{4}[A-Z]?".toRegex().findAll(report.courseCode)
                            .map { it.value.drop(3) }
                            .toSet()
                            .associateWith { report }
                            .entries
                    }.groupBy({ it.key }, { it.value })
                        .mapValues { (_, reports) -> reports.getTotalRatings(includeQuestions) }
                )
            }.filterValues { it != null }.mapValues { it.value!! }
    }.writeToFiles(writeDir)
}

// returns list of (# of 1s, # of 2s, ... # of 5s) for each question
// note that entries must have scores.size>=100 - maybe throw error?
// ***IMPORTANT NOTE*** By default, don't give ratings for question index 7 - as it's mostly irrelevant
fun List<Report>.getTotalRatings(includeQuestions: List<Int>): Ratings {
    return mapNotNull { report ->
        includeQuestions.map { report.ratings.getValue(it) }
    }.combine().map { it.reversed() } // reversed so that rankings go from 0-5
}

fun createAllInstructors(
    readDir: String,
    writeDir: String,
): Map<String, List<Instructor>> {
    val profList = getCompleteSchoolDeptsMap<Map<String, InstructorStats>>(readDir)
        .mapValues { (_, deptMap) ->
            deptMap.flatMap { (dept, entries) ->
                entries.map { (name, stats) -> Instructor(name, dept, stats.lastSem) }
            }.sortedBy { it.name }
        }
    makeFileAndDir("$writeDir/instructors.json").writeAsJson(profList.toSortedMap().toMap())
    return profList
}
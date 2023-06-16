package io.github.opletter.courseevals.common.remote

class WebsitePaths(
    private val baseDir: String = "jsonData",
    private val extraDir: String = "$baseDir/extraData",
    val statsByProfDir: String = "$baseDir/statsByProf",
    val courseNamesDir: String = "$extraDir/courseNames",
    val teachingDataDir: String = "$extraDir/teachingF23",
    val allInstructorsFile: String = "$statsByProfDir/allInstructors.json",
    val deptNamesFile: String = "$extraDir/deptNames.json",
    val schoolsByCodeFile: String = "$statsByProfDir/schools.json",
)
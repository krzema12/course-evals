package io.github.opletter.courseevals.usf

import io.github.opletter.courseevals.common.data.pmap
import io.github.opletter.courseevals.common.writeAsJson
import java.nio.file.Path
import kotlin.io.path.appendText

suspend fun getData(writeDir: Path, terms: List<String> = getTerms(), prefixes: List<String> = Prefixes) {
    prefixes.forEach { prefix ->
        terms.sortedDescending().chunked(terms.size / 2 + 1).forEach { chunk ->
            chunk.pmap { year ->
                println("$prefix $year")
                try {
                    val reportId = getReportIdByPrefix(prefix, year)
                    val data = getReports(reportId)
                    writeDir.resolve(prefix).resolve("$year.json").writeAsJson(data)
                } catch (e: Exception) {
                    e.printStackTrace()
                    writeDir.resolve("errors.txt").appendText("$prefix $year ${e.message}\n")
                }
            }
        }
    }
}
import java.io.File

fun main() {
    val (a, b) = readDataFromFile("input.dat")

    a.display()
    println(b)
}

/**
 * Reads matrix and column of free (?) members from file and parses them (?).
 */
fun readDataFromFile(fileName: String): Pair<Matrix2D<Double>, List<Double>> {
    val lines = File(fileName).readLines()

    val matrix = lines
            .dropLast(2)
            .map {
                it.parseDoubles().toTypedArray()
            }.toTypedArray()

    val b = lines.last().parseDoubles()

    return Pair(matrix, b)
}
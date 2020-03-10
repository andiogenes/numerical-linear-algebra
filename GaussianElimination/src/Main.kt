import java.io.File

fun main() {
    val (a, b) = readDataFromFile("input.dat")

    a.display()
    println(b)

    val system = System(a.clone(), b.toTypedArray())

    val swaps = try {
        system.eliminate()
    } catch (exception: Exception) {
        println(exception.message)
        return
    }

    val solutions = system.substitute()

    system.matrix.display()
    println(solutions)

    println(system.determinant(swaps))

    a.inverseMatrix().display()
}

/**
 * Reads matrix and column of constant terms from file and parses them.
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
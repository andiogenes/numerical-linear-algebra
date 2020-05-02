import kotlin.math.abs

internal enum class Sign {
    Plus, Minus;

    fun invert() =
            when (this) {
                Plus -> Minus
                Minus -> Plus
            }
}

class Solver(matrix: Matrix2D<Double>, terms: List<Double>, private val usePivotElement: Boolean = true) {
    private val matrix = matrix
            .mapIndexed { index, doubles ->
                val unitRow = Array(doubles.size) { 0.0 }.also { it[index] = 1.0 }
                doubles.clone().plus(unitRow)
            }
            .toTypedArray()
    private val terms = terms.toTypedArray()

    private var sign = Sign.Plus

    val solution: List<Double> by lazy {
        substitution
    }
    val determinant: Double by lazy {
        elimination
        findDeterminant()
    }
    val residual: List<Double> by lazy {
        solution
        listOf(0.0)
    }
    val inverseMatrix: Matrix2D<Double> by lazy {
        elimination
        findInverseMatrix()
    }

    private val elimination: Unit by lazy {
        eliminate()
    }
    private val substitution: List<Double> by lazy {
        elimination
        substitute()
    }

    private fun eliminate(start: Int = 0, end: Int = matrix.size - 1) {
        for (i in start until end) {
            var maxIndex = i

            if (usePivotElement) {
                for (j in i..end) {
                    if (abs(matrix[j][i]) > matrix[maxIndex][i]) {
                        maxIndex = j
                    }
                }

                if (abs(matrix[maxIndex][i]) <= Double.MIN_VALUE) {
                    throw Exception("Matrix is degenerate")
                }
            }

            if (i != maxIndex) {
                swapRows(i, maxIndex)
            }

            for (j in i + 1..end) {
                val c = -matrix[j][i] / matrix[i][i]

                increaseRow(j, matrix[i] * c)
                terms[j] += terms[i] * c
            }
        }
    }

    private fun substitute(): List<Double> {
        val solutions = MutableList(matrix.size) { 0.0 }

        for (i in matrix.indices.reversed()) {
            val readySolutions = (i + 1 until matrix.size)
                    .map { matrix[i][it] * solutions[it] }
                    .sum()

            solutions[i] = (terms[i] - readySolutions) / matrix[i][i]
        }

        return solutions
    }


    private fun findInverseMatrix(): Matrix2D<Double> {
        return matrix
                .map { it.drop(it.size / 2).toTypedArray() }.toTypedArray()
                .also {
                    for (i in it.indices) {
                        for (j in it[i].indices.drop(i)) {
                            it[i] = it[j].also { s -> s[j] = s[i] }
                        }
                    }
                }
                .map {
                    val solutions = MutableList(matrix.size) { 0.0 }
                    for (i in matrix.indices.reversed()) {
                        val readySolutions = (i + 1 until matrix.size)
                                .map { j -> matrix[i][j] * solutions[j] }
                                .sum()

                        solutions[i] = (it[i] - readySolutions) / matrix[i][i]
                    }
                    solutions.toTypedArray()
                }.toTypedArray()
    }

    private fun findDeterminant(): Double {
        val determinant = matrix.indices
                .map { matrix[it][it] }
                .reduce { acc, d -> acc * d }

        if (sign == Sign.Minus) {
            return -determinant
        }

        return determinant
    }

    private fun swapRows(i: Int, j: Int) {
        matrix[i] = matrix[j].also { matrix[j] = matrix[i] }
        terms[i] = terms[j].also { terms[j] = terms[i] }
        sign = sign.invert()
    }

    private fun increaseRow(index: Int, other: Array<Double>) {
        other.forEachIndexed { j, v -> matrix[index][j] += v }
    }

    operator fun Array<Double>.times(c: Double): Array<Double> {
        return this.map { it * c }.toTypedArray()
    }
}
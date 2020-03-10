import kotlin.math.abs

class System<T>(val matrix: Matrix2D<T>, var terms: Array<T>)

/**
 * Прямой ход метода Гаусса.
 * Возвращает количество перестановок строк.
 */
fun System<Double>.eliminate(start: Int = 0, end: Int = this.matrix.size - 1, usePivotElement: Boolean = true): Int {
    var swapCount = 0

    for (i in start until end) {
        var maxIndex = i

        if (usePivotElement) {
            // Поиск опорного элемента
            for (j in i..end) {
                if (abs(matrix[j][i]) > matrix[maxIndex][i]) {
                    maxIndex = j
                }
            }

            // Если все элементы в столбцы ниже диагонали = 0 => матрица вырожденная
            if (abs(matrix[maxIndex][i]) <= Double.MIN_VALUE) {
                throw Exception("Matrix is degenerate")
            }
        }

        if (i != maxIndex) {
            matrix[i] = matrix[maxIndex].also { matrix[maxIndex] = matrix[i] }
            terms[i] = terms[maxIndex].also { terms[maxIndex] = terms[i] }
            swapCount++
        }

        for (j in i + 1..end) {
            val c = -matrix[j][i] / matrix[i][i]

            matrix.increaseRow(j, matrix[i] * c)
            terms[j] += terms[i] * c
        }
    }

    return swapCount
}

/**
 * Обратный ход метода Гаусса.
 * Возвращает список чисел - решение системы.
 */
fun System<Double>.substitute(): List<Double> {
    val solutions = MutableList(matrix.size) { 0.0 }

    for (i in matrix.indices.reversed()) {
        val readySolutions = (i + 1 until matrix.size)
                .map { matrix[i][it] * solutions[it] }.sum()

        solutions[i] = (terms[i] - readySolutions) / matrix[i][i]
    }

    return solutions
}

/**
 * Вычисляет определитель матрицы.
 * @param swapCount Количество перестановок строк.
 */
fun System<Double>.determinant(swapCount: Int): Double {
    val determinant = matrix.indices
            .map { matrix[it][it] }
            .reduce { acc, d -> acc * d }

    if (swapCount % 2 != 0) {
        return -determinant
    }

    return determinant
}
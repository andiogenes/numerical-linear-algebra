import kotlin.math.abs

private class InverseSystem(val matrix: Matrix2D<Double>, val orderTerms: Array<Int>)

/**
 * Прямой ход метода Гаусса, но:
 * 1) В специальной правой части меняется только позиция элементов
 * 2) Элементы правой части имеют целый тип, подразумевается, что правая часть - перечисление индексов 0, 1, ..., m.
 */
private fun InverseSystem.eliminate(start: Int = 0, end: Int = this.matrix.size - 1, usePivotElement: Boolean = true) {
    for (i in start until end) {
        /* DRY нарушен, но что поделать? */
        var maxIndex = i

        if (usePivotElement) {
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
            orderTerms[i] = orderTerms[maxIndex].also { orderTerms[maxIndex] = orderTerms[i] }
        }

        for (j in i + 1..end) {
            val c = -matrix[j][i] / matrix[i][i]

            matrix.increaseRow(j, matrix[i] * c)
        }
    }
}

/**
 * Возвращает обратную матрицу.
 */
fun Matrix2D<Double>.inverseMatrix(): Matrix2D<Double> {
    val inverseSystem = InverseSystem(this.clone(), this.indices.toList().toTypedArray())
    // Получение новых позиций столбцов единичной матрицы.
    inverseSystem.eliminate()

    // Получение трансформированной единичной матрицы.
    val transformedUnitMatrix = inverseSystem.orderTerms.map {
        val array = Array(this.size) { 0.0 }
        array[it] = 1.0

        array
    }

    // Получение обратной матрицы.
    return transformedUnitMatrix.map {
        val system = System(inverseSystem.matrix.clone(), it)
        system.substitute().toTypedArray()
    }.toTypedArray().transpose()
}
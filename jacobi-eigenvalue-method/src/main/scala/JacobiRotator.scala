import breeze.linalg.{DenseMatrix, argmax, diag}
import scala.util.control.Breaks._

object JacobiRotator {
  /**
   * Построение матрицы Ukl
   * @param k k-я кооридната
   * @param l l-я координата
   * @param a Число α
   * @param b Число β
   * @param size Размерность матрицы
   * @return Ukl
   */
  private def uKL(k: Int, l: Int, a: Double, b: Double, size: Int): DenseMatrix[Double] = {
    val u = DenseMatrix.eye[Double](size)

    u(k, k) = a
    u(l, l) = a
    u(l, k) = b
    u(k, l) = -b

    u
  }

  /**
   * Вычисление α и β
   * @param matrix Матрица
   * @param k k-я координата
   * @param l l-я координата
   * @return α и β
   */
  private def ab(matrix: DenseMatrix[Double], k: Int, l: Int): (Double, Double) =
    (matrix(k, k), matrix(l, l)) match {
      case (akk, all) if Math.abs(akk - all) < Double.MinPositiveValue =>
        val v = Math.sqrt(0.5)
        (v, v)
      case (akk, all) =>
        val mu = (2 * matrix(k, l)) / (akk - all)

        val alpha = Math.sqrt((1 + 1 / Math.sqrt(1 + mu * mu)) / 2)
        val beta = mu.sign * Math.sqrt((1 - 1 / Math.sqrt(1 + mu * mu)) / 2)

        (alpha, beta)
    }

  /**
   * Поиск максимального недиагонального элемента матрицы
   * @param matrix Матрица
   * @return ((x,y), m), где x, y - строка и столбец, m - модуль максимального недиагонального элемента
   */
  private def maxNonDiagonalNaive(matrix: DenseMatrix[Double]): ((Int, Int), Double) = {
    val _matrix = matrix.copy.map(v => Math.abs(v))
    diag(matrix).foreachPair((i, _) => _matrix(i, i) = 0)

    argmax(_matrix) match {
      case (k, v) if k == v => ((0, 1), _matrix(0, 1))
      case (k, v) => ((k, v), _matrix(k, v))
    }
  }

  /**
   * Вычисление собственных чисел и собственных векторов матрицы
   * @param matrix Матрица
   * @param eps Точность вычислений
   * @return (val, vec), где val - вектор собственных чисел, vec - список собственных векторов
   */
  def compute(matrix: DenseMatrix[Double], eps: Double): (Vector[Double], List[Vector[Double]]) = {
    var a = matrix
    var d = DenseMatrix.eye[Double](a.rows)

    breakable {
      while (true) {
        val ((k, l), _) = maxNonDiagonalNaive(a)
        val (alpha, beta) = ab(a, k, l)
        val u = uKL(k, l, alpha, beta, a.rows)

        val c = a * u
        val b = u.t * c

        a = b
        d = d * u

        if (maxNonDiagonalNaive(a)._2 < eps) break
      }
    }

    val eigenvalues = diag(a).toScalaVector()
    val eigenvectors = (0 until d.cols).map(i => d(::, i).toScalaVector()).toList

    (eigenvalues, eigenvectors)
  }
}

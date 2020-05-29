import breeze.linalg.{DenseMatrix, argmax, diag, max}
import scala.util.control.Breaks._

object JacobiRotator {
  private def uKL(k: Int, l: Int, a: Double, b: Double, size: Int): DenseMatrix[Double] = {
    val u = DenseMatrix.eye[Double](size)

    u(k, k) = a
    u(l, l) = a
    u(l, k) = b
    u(k, l) = -b

    u
  }

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

  //noinspection DuplicatedCode
  private def maxNonDiagonalNaive(matrix: DenseMatrix[Double]): (Int, Int) = {
    val diagonal = DenseMatrix.eye[Double](matrix.rows)
    diag(matrix).foreachPair((i, v) => diagonal(i, i) = v)

    argmax(matrix - diagonal)
  }

  //noinspection DuplicatedCode
  private def estimate(matrix: DenseMatrix[Double]): Double = {
    val diagonal = DenseMatrix.eye[Double](matrix.rows)
    diag(matrix).foreachPair((i, v) => diagonal(i, i) = v)

    max(matrix - diagonal)
  }

  def compute(matrix: DenseMatrix[Double], eps: Double): (Vector[Double], List[Vector[Double]]) = {
    var a = matrix
    val d = DenseMatrix.eye[Double](a.rows)

    breakable {
      while (true) {
        val (k, l) = maxNonDiagonalNaive(a)
        val (alpha, beta) = ab(a, k, l)
        val u = uKL(k, l, alpha, beta, a.rows)

        val c = a * u
        val b = u.t * c

        if (estimate(b.map(v => Math.abs(v))) < eps) break

        a = b
        u *= d
      }
    }

    val eigenvalues = diag(a).toScalaVector()
    val eigenvectors = (0 until d.cols).map(i => d(::, i).toScalaVector()).toList

    (eigenvalues, eigenvectors)
  }
}

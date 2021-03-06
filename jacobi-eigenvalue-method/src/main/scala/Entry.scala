import java.beans.BeanProperty
import java.io.{File, FileInputStream}
import java.util

import breeze.linalg.DenseMatrix
import com.jakewharton.fliptables.FlipTable
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

import scala.jdk.CollectionConverters._

object Entry extends App {
  val options = CliParser.parse(args)

  val source = options(CliParser.Source).toString
  val destination = options(CliParser.Destination).toString

  /**
   * Загружает YAML-данные из `filename` в формате `Config`
   * @param filename Относительный путь к файлу
   * @return Объект с загруженными данными
   */
  def loadData(filename: String) = {
    val input = new FileInputStream(new File(filename))
    val yaml = new Yaml(new Constructor(classOf[Config]))

    yaml.load(input).asInstanceOf[Config]
  }

  /**
   * Преобразует матрицу из списочного представления в эффективно-разреженное
   * @param matrix Матрица
   * @return Матрица в новом представлении
   */
  def convertMatrix(matrix: util.ArrayList[util.ArrayList[Number]]) = {
    val rows = matrix.size()
    val cols = matrix.get(0).size()

    val values = matrix.asScala
      .flatMap(v => v.asScala
        .map(n => n.doubleValue()))
      .toArray

    new DenseMatrix(rows, cols, values)
  }

  /**
   * Конвертирует вектор в строку в формате (x0, x1, ..., xn)
   * @param vector Вектор
   * @return Форматированная строка
   */
  def formatVector(vector: Vector[Any]): String =
    s"(${vector.tail.foldLeft(vector.head.toString) { (x, y) => s"$x, $y" }})"

  /**
   * Вычисляет невязку R = Ae - λe
   * @param matrix Матрица A
   * @param eigenvalue Собственное число λ
   * @param eigenvector Собственный вектор e
   * @return Величина невязки
   */
  def residual(matrix: DenseMatrix[Double], eigenvalue: Double, eigenvector: Vector[Double]) = {
    val vector = DenseMatrix(eigenvector).t
    (matrix * vector - eigenvalue * vector).valuesIterator.toVector
  }


  val config = loadData(source)

  val matrix = convertMatrix(config.matrix)
  val eps = config.eps

  val (eigenvalues, eigenvectors) = JacobiRotator.compute(matrix, eps)

  val headers = Array("Eigenvalue", "Eigenvector", "Residual")
  val data = eigenvalues
    .zip(eigenvectors)
    .map(v => Array(v._1.toString, formatVector(v._2), formatVector(residual(matrix, v._1, v._2))))
    .toArray

  println(FlipTable.of(headers, data))
}

class Config {
  @BeanProperty var matrix = new util.ArrayList[util.ArrayList[Number]]()
  @BeanProperty var eps: Double = 0
}
import java.beans.BeanProperty
import java.io.{File, FileInputStream}
import java.util

import breeze.linalg.DenseMatrix
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

import scala.jdk.CollectionConverters._

object Entry extends App {
  val options = CliParser.parse(args)

  val source = options(CliParser.Source).toString
  val destination = options(CliParser.Destination).toString

  def loadData(filename: String) = {
    val input = new FileInputStream(new File(filename))
    val yaml = new Yaml(new Constructor(classOf[Config]))

    yaml.load(input).asInstanceOf[Config]
  }

  def convertMatrix(matrix: util.ArrayList[util.ArrayList[Number]]) = {
    val rows = matrix.size()
    val cols = matrix.get(0).size()

    val values = matrix.asScala
      .flatMap(v => v.asScala
        .map(n => n.doubleValue()))
      .toArray

    new DenseMatrix(rows, cols, values)
  }

  val config = loadData(source)

  val matrix = convertMatrix(config.matrix)
  val eps = config.eps

  val (eigenvalues, eigenvectors) = JacobiRotator.compute(matrix, eps)

  println(eigenvalues)
  println(eigenvectors)
}

class Config {
  @BeanProperty var matrix = new util.ArrayList[util.ArrayList[Number]]()
  @BeanProperty var eps: Double = 0
}
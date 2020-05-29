import java.beans.BeanProperty
import java.io.{File, FileInputStream}
import java.util

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

object Entry extends App {
  val options = CliParser.parse(args)

  val source = options(CliParser.Source).toString
  val destination = options(CliParser.Destination).toString

  def loadData(filename: String) = {
    val input = new FileInputStream(new File(filename))
    val yaml = new Yaml(new Constructor(classOf[Config]))

    yaml.load(input).asInstanceOf[Config]
  }

  val config = loadData(source)
  println(config.matrix)
}

class Config {
  @BeanProperty var matrix = new util.ArrayList[util.ArrayList[Double]]()
  @BeanProperty var eps: Double = 0
}
import kotlin.math.absoluteValue

object Data {
    object Diameter {
        const val outerDiameter = 45.0
        private const val regularPrecisionBias = 0.4
        private const val increasedPrecisionBias = 0.35

        val biases = listOf(regularPrecisionBias.leafs(), increasedPrecisionBias.leafs())
            .flatMap { (it.increaseBy(outerDiameter)).toList() }
            .plus(outerDiameter)
    }

    object Thickness {
        const val wallThickness = 1.8
        private const val biasPercentage = 0.1

        val biases = listOf((wallThickness * biasPercentage).leafs())
            .flatMap { (it.increaseBy(wallThickness)).toList() }
            .plus(wallThickness)
    }

    const val mass = 1.92
    val density: Double by lazy {
        val volume = calculateVolume(Diameter.outerDiameter, Thickness.wallThickness)

        mass / volume
    }

    fun calculateVolume(diameter: Double, thickness: Double): Double {
        val id = diameter - thickness

        return Math.PI / 4 * (diameter * diameter - id * id)
    }
}

fun main() {
    val argumentsTable = Data.Diameter.biases * Data.Thickness.biases

    println("Possible arguments:")
    println("\tD (mm):\t\th (mm):")

    argumentsTable.withIndex().forEach {
        println("%d:\t%.4g\t\t%.4g".format(it.index+1, it.value.first, it.value.second))
    }
    println()

    val masses = argumentsTable
        .map { Data.calculateVolume(it.first, it.second) * Data.density }

    println("\tm = r*V (kg):")
    masses.withIndex().forEach { println("${it.index+1}\t${it.value}") }
    println()

    println("\t|m - m*| (kg):")
    val bias = masses.map { (it - Data.mass).absoluteValue }
    bias.withIndex().forEach { println("${it.index+1}\t${it.value}") }
    println()

    val marginalAbsoluteError = bias.withIndex().maxBy { it.value }
    println("Marginal absolute error: ${marginalAbsoluteError?.value} (${marginalAbsoluteError?.index?.plus(1)})")

    val marginalRelativeError = marginalAbsoluteError?.value?.div(Data.mass)
    println("Marginal relative error: $marginalRelativeError")
}
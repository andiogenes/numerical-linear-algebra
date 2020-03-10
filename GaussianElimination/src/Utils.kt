/**
 * Splits string with ' ' delimiter and maps obtained strings to doubles.
 */
fun String.parseDoubles(): List<Double> =
        this.split(' ').map { e -> e.toDouble() }

/**
 * Multiplies array elements by `c` times.
 */
operator fun Array<Double>.times(c: Double): Array<Double> =
        this.map { it * c }.toTypedArray()
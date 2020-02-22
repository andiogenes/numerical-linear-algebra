/**
 * Splits string with ' ' delimiter and maps obtained strings to doubles.
 */
fun String.parseDoubles(): List<Double> =
        this.split(' ').map { e -> e.toDouble() }
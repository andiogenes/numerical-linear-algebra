typealias Matrix2D<T> = Array<Array<T>>

/**
 * Iterates over two-dimensional matrix, performs  given  action
 * for each element (supposed to be an output activity) and puts
 * line-breaks between rows.
 */
inline fun <T> Matrix2D<T>.displayBy(rule: (T) -> Unit) {
    this.forEach {
        it.forEach { e ->
            rule(e)
        }
        println()
    }
}

/**
 * Displays two-dimensional matrix of doubles in output stream.
 */
fun Matrix2D<Double>.display() {
    this.displayBy { print("%5.5g\t".format(it)) }
}
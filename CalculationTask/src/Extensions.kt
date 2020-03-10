/**
 * Cartesian product of collections A and B.
 */
operator fun <T, U> Collection<T>.times(c: Collection<U>): List<Pair<T, U>> =
    this.flatMap { l -> c.map { r -> Pair(l, r) } }

fun Double.leafs() = if (this > 0) Pair(-this, this) else Pair(this, -this)

fun Pair<Double, Double>.increaseBy(c: Double): Pair<Double, Double> =
    Pair(this.first + c, this.second + c)
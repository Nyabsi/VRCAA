package cc.sovellus.vrcaa.helper

object MathHelper {
    fun getLimits(value: Int): List<Int> {
        return (1..50).map { idx ->
            val result = value / idx
            if (result in 1..50) {
                if (result * idx != value) {
                    val byProduct = value - (result * idx)
                    return listOf(result, byProduct)
                } else {
                    return listOf(result, -1)
                }
            } else { null }
        }.first() ?: listOf(-1, -1)
    }

    fun isWithinByProduct(offset: Int, byProduct: Int, value: Int): Boolean {
        return (offset + byProduct) == value
    }
}
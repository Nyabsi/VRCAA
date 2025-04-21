/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
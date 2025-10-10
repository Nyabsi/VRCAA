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

package cc.sovellus.vrcaa.api.vrchat.http.interfaces

import android.net.Uri
import cc.sovellus.vrcaa.api.vrchat.http.models.Print
import java.time.LocalDateTime

interface IPrints {

    suspend fun fetchPrintsByUserId(userId: String, n: Int = 100, offset: Int = 0, prints: ArrayList<Print> = arrayListOf()): ArrayList<Print>
    suspend fun fetchPrint(printId: String): Print?
    suspend fun deletePrint(printId: String): Print?
    suspend fun editPrint(printId: String): Print?
    suspend fun uploadPrint(file: Uri, note: String, timestamp: LocalDateTime, border: Boolean): Print?
}
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

import cc.sovellus.vrcaa.api.vrchat.http.models.Inventory

interface IInventory {
    suspend fun fetchEmojis(ugc: Boolean, archived: Boolean, n: Int = 100, offset: Int = 0, order: String = "newest_created", emojis: ArrayList<Inventory.Data> = arrayListOf()): ArrayList<Inventory.Data>
    suspend fun fetchStickers(ugc: Boolean, archived: Boolean, n: Int = 100, offset: Int = 0, order: String = "newest_created", stickers: ArrayList<Inventory.Data> = arrayListOf()): ArrayList<Inventory.Data>
    suspend fun fetchProps(ugc: Boolean, archived: Boolean, n: Int = 100, offset: Int = 0, order: String = "newest_created", props: ArrayList<Inventory.Data> = arrayListOf()): ArrayList<Inventory.Data>
}
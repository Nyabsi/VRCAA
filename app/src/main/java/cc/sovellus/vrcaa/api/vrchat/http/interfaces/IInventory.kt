package cc.sovellus.vrcaa.api.vrchat.http.interfaces

import cc.sovellus.vrcaa.api.vrchat.http.models.Inventory

interface IInventory {
    suspend fun fetchEmojis(ugc: Boolean, archived: Boolean, n: Int = 100, offset: Int = 0, order: String = "newest_created", emojis: ArrayList<Inventory.Data> = arrayListOf()): ArrayList<Inventory.Data>
    suspend fun fetchStickers(ugc: Boolean, archived: Boolean, n: Int = 100, offset: Int = 0, order: String = "newest_created", stickers: ArrayList<Inventory.Data> = arrayListOf()): ArrayList<Inventory.Data>
    suspend fun fetchProps(ugc: Boolean, archived: Boolean, n: Int = 100, offset: Int = 0, order: String = "newest_created", props: ArrayList<Inventory.Data> = arrayListOf()): ArrayList<Inventory.Data>
}
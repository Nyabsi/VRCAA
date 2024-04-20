package cc.sovellus.vrcaa.api.vrchat.models


import com.google.gson.annotations.SerializedName

class Favorites : ArrayList<Favorites.FavoritesItem>(){
    data class FavoritesItem(
        @SerializedName("favoriteId")
        val favoriteId: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("tags")
        val tags: List<String>,
        @SerializedName("type")
        val type: String
    )
}
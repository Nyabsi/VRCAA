package cc.sovellus.vrcaa.api.search.avtrdb.models


import com.google.gson.annotations.SerializedName

data class SearchAvatar(
    @SerializedName("acknowledgements")
    var acknowledgements: String = "",
    @SerializedName("author")
    var author: Author = Author(),
    @SerializedName("compatibility")
    var compatibility: List<String> = listOf(),
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("description")
    var description: String = "",
    @SerializedName("explicit")
    var explicit: Boolean = false,
    @SerializedName("image_url")
    var imageUrl: String?,
    @SerializedName("marketplace")
    var marketplace: Any = Any(),
    @SerializedName("name")
    var name: String = "",
    @SerializedName("performance")
    var performance: Performance = Performance(),
    @SerializedName("styles")
    var styles: Styles = Styles(),
    @SerializedName("tags")
    var tags: Tags = Tags(),
    @SerializedName("updated_at")
    var updatedAt: String = "",
    @SerializedName("vrc_id")
    var vrcId: String = ""
) {
    data class Author(
        @SerializedName("name")
        var name: String = "",
        @SerializedName("vrc_id")
        var vrcId: String = ""
    )

    data class Performance(
        @SerializedName("android_rating")
        var androidRating: String = "",
        @SerializedName("has_impostor")
        var hasImpostor: Boolean = false,
        @SerializedName("ios_rating")
        var iosRating: Any = Any(),
        @SerializedName("pc_rating")
        var pcRating: String = ""
    )

    data class Styles(
        @SerializedName("primary")
        var primary: String = "",
        @SerializedName("secondary")
        var secondary: String = ""
    )

    data class Tags(
        @SerializedName("author_tags")
        var authorTags: List<String> = listOf(),
        @SerializedName("content_tags")
        var contentTags: List<String> = listOf(),
        @SerializedName("non_content_tags")
        var nonContentTags: List<String> = listOf()
    )
}
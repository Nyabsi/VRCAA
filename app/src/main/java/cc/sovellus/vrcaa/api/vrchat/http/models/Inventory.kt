package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class Inventory(
    @SerializedName("data")
    var `data`: List<Data>,
    @SerializedName("totalCount")
    var totalCount: Int
) {
    data class Data(
        @SerializedName("collections")
        var collections: List<String>,
        @SerializedName("created_at")
        var createdAt: String,
        @SerializedName("description")
        var description: String,
        @SerializedName("expiryDate")
        var expiryDate: Any,
        @SerializedName("flags")
        var flags: List<String>,
        @SerializedName("holderId")
        var holderId: String,
        @SerializedName("id")
        var id: String,
        @SerializedName("imageUrl")
        var imageUrl: String,
        @SerializedName("isArchived")
        var isArchived: Boolean,
        @SerializedName("isSeen")
        var isSeen: Boolean,
        @SerializedName("itemType")
        var itemType: String,
        @SerializedName("itemTypeLabel")
        var itemTypeLabel: String,
        @SerializedName("metadata")
        var metadata: Metadata,
        @SerializedName("name")
        var name: String,
        @SerializedName("tags")
        var tags: List<String>,
        @SerializedName("template_created_at")
        var templateCreatedAt: String,
        @SerializedName("templateId")
        var templateId: String,
        @SerializedName("template_updated_at")
        var templateUpdatedAt: String,
        @SerializedName("updated_at")
        var updatedAt: String
    ) {
        data class Metadata(
            @SerializedName("animated")
            var animated: Boolean,
            @SerializedName("animationStyle")
            var animationStyle: String,
            @SerializedName("assetBundleId")
            var assetBundleId: String,
            @SerializedName("fileId")
            var fileId: String,
            @SerializedName("imageUrl")
            var imageUrl: String
        )
    }
}
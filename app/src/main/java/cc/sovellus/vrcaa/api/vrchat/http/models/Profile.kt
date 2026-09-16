package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class Profile(
    @SerializedName("ageVerificationStatus")
    var ageVerificationStatus: String = "",
    @SerializedName("ageVerified")
    var ageVerified: Boolean = false,
    @SerializedName("backgroundGradientBottom")
    var backgroundGradientBottom: String = "",
    @SerializedName("backgroundGradientTop")
    var backgroundGradientTop: String = "",
    @SerializedName("backgroundTemplateId")
    var backgroundTemplateId: String = "",
    @SerializedName("backgroundTextureId")
    var backgroundTextureId: String = "",
    @SerializedName("backgroundType")
    var backgroundType: String = "",
    @SerializedName("badges")
    var badges: List<Badge> = listOf(),
    @SerializedName("bannerColor")
    var bannerColor: String = "",
    @SerializedName("bannerCustomUrl")
    var bannerCustomUrl: String = "",
    @SerializedName("bannerType")
    var bannerType: String = "",
    @SerializedName("bannerUrl")
    var bannerUrl: String = "",
    @SerializedName("bio")
    var bio: String = "",
    @SerializedName("bioLinks")
    var bioLinks: List<String> = listOf(),
    @SerializedName("currentAvatar")
    var currentAvatar: String = "",
    @SerializedName("currentAvatarAuthorName")
    var currentAvatarAuthorName: String = "",
    @SerializedName("currentAvatarImageUrl")
    var currentAvatarImageUrl: String = "",
    @SerializedName("currentAvatarName")
    var currentAvatarName: String = "",
    @SerializedName("currentAvatarTags")
    var currentAvatarTags: List<Any> = listOf(),
    @SerializedName("currentAvatarThumbnailImageUrl")
    var currentAvatarThumbnailImageUrl: String = "",
    @SerializedName("displayName")
    var displayName: String = "",
    @SerializedName("groups")
    var groups: Groups = Groups(),
    @SerializedName("hasVrcPlus")
    var hasVrcPlus: Boolean = false,
    @SerializedName("iconFrame")
    var iconFrame: String = "",
    @SerializedName("iconType")
    var iconType: String = "",
    @SerializedName("iconUrl")
    var iconUrl: String = "",
    @SerializedName("id")
    var id: String = "",
    @SerializedName("isEconomyCreator")
    var isEconomyCreator: Boolean = false,
    @SerializedName("languages")
    var languages: List<String> = listOf(),
    @SerializedName("nameplateEffect")
    var nameplateEffect: String = "",
    @SerializedName("profileEffect")
    var profileEffect: String = "",
    @SerializedName("pronouns")
    var pronouns: String = "",
    @SerializedName("publicWorlds")
    var publicWorlds: List<PublicWorld> = listOf(),
    @SerializedName("representedGroup")
    var representedGroup: RepresentedGroup = RepresentedGroup(),
    @SerializedName("status")
    var status: String = "",
    @SerializedName("statusDescription")
    var statusDescription: String = "",
    @SerializedName("themeButtonColor")
    var themeButtonColor: String = "",
    @SerializedName("themeIconColor")
    var themeIconColor: String = "",
    @SerializedName("themeId")
    var themeId: String = "",
    @SerializedName("themeSubtextColor")
    var themeSubtextColor: String = "",
    @SerializedName("themes")
    var themes: List<Theme> = listOf(),
    @SerializedName("totalPublicWorldsCount")
    var totalPublicWorldsCount: Int = 0,
    @SerializedName("trustTags")
    var trustTags: List<String> = listOf(),
    @SerializedName("userIcon")
    var userIcon: String = "",
    @SerializedName("worldFavoriteLists")
    var worldFavoriteLists: List<WorldFavoriteLists> = listOf()
) {
    data class Badge(
        @SerializedName("assignedAt")
        var assignedAt: String = "",
        @SerializedName("badgeDescription")
        var badgeDescription: String = "",
        @SerializedName("badgeId")
        var badgeId: String = "",
        @SerializedName("badgeImageUrl")
        var badgeImageUrl: String = "",
        @SerializedName("badgeName")
        var badgeName: String = "",
        @SerializedName("hidden")
        var hidden: Boolean = false,
        @SerializedName("showcased")
        var showcased: Boolean = false,
        @SerializedName("updatedAt")
        var updatedAt: String = ""
    )

    data class Groups(
        @SerializedName("count")
        var count: Int = 0,
        @SerializedName("list")
        var list: List<Item0> = listOf()
    ) {
        data class Item0(
            @SerializedName("iconUrl")
            var iconUrl: String = "",
            @SerializedName("id")
            var id: String = "",
            @SerializedName("name")
            var name: String = ""
        )
    }

    data class PublicWorld(
        @SerializedName("authorId")
        var authorId: String = "",
        @SerializedName("authorName")
        var authorName: String = "",
        @SerializedName("capacity")
        var capacity: Int = 0,
        @SerializedName("created_at")
        var createdAt: String = "",
        @SerializedName("defaultContentSettings")
        var defaultContentSettings: DefaultContentSettings = DefaultContentSettings(),
        @SerializedName("disabledPropAbilities")
        var disabledPropAbilities: List<Any> = listOf(),
        @SerializedName("favorites")
        var favorites: Int = 0,
        @SerializedName("heat")
        var heat: Int = 0,
        @SerializedName("id")
        var id: String = "",
        @SerializedName("imageUrl")
        var imageUrl: String = "",
        @SerializedName("isHypeTrainEligible")
        var isHypeTrainEligible: Boolean = false,
        @SerializedName("labsPublicationDate")
        var labsPublicationDate: String = "",
        @SerializedName("name")
        var name: String = "",
        @SerializedName("occupants")
        var occupants: Int = 0,
        @SerializedName("organization")
        var organization: String = "",
        @SerializedName("popularity")
        var popularity: Int = 0,
        @SerializedName("previewYoutubeId")
        var previewYoutubeId: Any = Any(),
        @SerializedName("publicationDate")
        var publicationDate: String = "",
        @SerializedName("recommendedCapacity")
        var recommendedCapacity: Int = 0,
        @SerializedName("releaseStatus")
        var releaseStatus: String = "",
        @SerializedName("tags")
        var tags: List<String> = listOf(),
        @SerializedName("thumbnailImageUrl")
        var thumbnailImageUrl: String = "",
        @SerializedName("udonProducts")
        var udonProducts: List<Any> = listOf(),
        @SerializedName("unityPackages")
        var unityPackages: List<UnityPackage> = listOf(),
        @SerializedName("updated_at")
        var updatedAt: String? = ""
    ) {
        class DefaultContentSettings

        data class UnityPackage(
            @SerializedName("created_at")
            var createdAt: Any = Any(),
            @SerializedName("platform")
            var platform: String = "",
            @SerializedName("unityVersion")
            var unityVersion: String = ""
        )
    }

    data class RepresentedGroup(
        @SerializedName("bannerUrl")
        var bannerUrl: String = "",
        @SerializedName("iconUrl")
        var iconUrl: String = "",
        @SerializedName("id")
        var id: String = "",
        @SerializedName("name")
        var name: String = ""
    )

    data class Theme(
        @SerializedName("buttonColor")
        var buttonColor: String = "",
        @SerializedName("iconColor")
        var iconColor: String = "",
        @SerializedName("id")
        var id: String = "",
        @SerializedName("name")
        var name: String = "",
        @SerializedName("subtextColor")
        var subtextColor: String = ""
    )

    data class WorldFavoriteLists(
        @SerializedName("count")
        var count: Int = 0,
        @SerializedName("id")
        var id: String = "",
        @SerializedName("name")
        var name: String = "",
        @SerializedName("thumbnails")
        var thumbnails: List<String> = listOf()
    )
}
package cc.sovellus.vrcaa.api.vrchat.models


import com.google.gson.annotations.SerializedName

data class Group(
    @SerializedName("badges")
    val badges: List<Any>,
    @SerializedName("bannerId")
    val bannerId: String,
    @SerializedName("bannerUrl")
    val bannerUrl: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("discriminator")
    val discriminator: String,
    @SerializedName("galleries")
    val galleries: List<Gallery>,
    @SerializedName("iconId")
    val iconId: String,
    @SerializedName("iconUrl")
    val iconUrl: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("isVerified")
    val isVerified: Boolean,
    @SerializedName("joinState")
    val joinState: String,
    @SerializedName("languages")
    val languages: List<String>,
    @SerializedName("lastPostCreatedAt")
    val lastPostCreatedAt: Any?,
    @SerializedName("links")
    val links: List<Any>,
    @SerializedName("memberCount")
    val memberCount: Int,
    @SerializedName("memberCountSyncedAt")
    val memberCountSyncedAt: String,
    @SerializedName("membershipStatus")
    val membershipStatus: String,
    @SerializedName("myMember")
    val myMember: MyMember,
    @SerializedName("name")
    val name: String,
    @SerializedName("onlineMemberCount")
    val onlineMemberCount: Int,
    @SerializedName("ownerId")
    val ownerId: String,
    @SerializedName("privacy")
    val privacy: String,
    @SerializedName("roles")
    val roles: List<Role>,
    @SerializedName("rules")
    val rules: String,
    @SerializedName("shortCode")
    val shortCode: String,
    @SerializedName("tags")
    val tags: List<Any>,
    @SerializedName("updatedAt")
    val updatedAt: String
) {
    data class Gallery(
        @SerializedName("createdAt")
        val createdAt: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("membersOnly")
        val membersOnly: Boolean,
        @SerializedName("name")
        val name: String,
        @SerializedName("roleIdsToAutoApprove")
        val roleIdsToAutoApprove: List<Any>,
        @SerializedName("roleIdsToManage")
        val roleIdsToManage: List<Any>,
        @SerializedName("roleIdsToSubmit")
        val roleIdsToSubmit: List<Any>,
        @SerializedName("roleIdsToView")
        val roleIdsToView: Any?,
        @SerializedName("updatedAt")
        val updatedAt: String
    )

    data class MyMember(
        @SerializedName("groupId")
        val groupId: String,
        @SerializedName("has2FA")
        val has2FA: Boolean,
        @SerializedName("id")
        val id: String,
        @SerializedName("isRepresenting")
        val isRepresenting: Boolean,
        @SerializedName("isSubscribedToAnnouncements")
        val isSubscribedToAnnouncements: Boolean,
        @SerializedName("joinedAt")
        val joinedAt: String,
        @SerializedName("lastPostReadAt")
        val lastPostReadAt: Any?,
        @SerializedName("mRoleIds")
        val mRoleIds: List<Any>,
        @SerializedName("membershipStatus")
        val membershipStatus: String,
        @SerializedName("permissions")
        val permissions: List<String>,
        @SerializedName("roleIds")
        val roleIds: List<String>,
        @SerializedName("userId")
        val userId: String,
        @SerializedName("visibility")
        val visibility: String
    )

    data class Role(
        @SerializedName("createdAt")
        val createdAt: String,
        @SerializedName("defaultRole")
        val defaultRole: Boolean?,
        @SerializedName("description")
        val description: String,
        @SerializedName("groupId")
        val groupId: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("isAddedOnJoin")
        val isAddedOnJoin: Boolean,
        @SerializedName("isManagementRole")
        val isManagementRole: Boolean,
        @SerializedName("isSelfAssignable")
        val isSelfAssignable: Boolean,
        @SerializedName("name")
        val name: String,
        @SerializedName("order")
        val order: Int,
        @SerializedName("permissions")
        val permissions: List<String>,
        @SerializedName("requiresPurchase")
        val requiresPurchase: Boolean,
        @SerializedName("requiresTwoFactor")
        val requiresTwoFactor: Boolean,
        @SerializedName("updatedAt")
        val updatedAt: String
    )
}
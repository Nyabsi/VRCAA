package cc.sovellus.vrcaa.api.updater.models


import com.google.gson.annotations.SerializedName

class Commits : ArrayList<Commits.CommitsItem>(){
    data class CommitsItem(
        @SerializedName("author")
        val author: Author,
        @SerializedName("comments_url")
        val commentsUrl: String,
        @SerializedName("commit")
        val commit: Commit,
        @SerializedName("committer")
        val committer: Committer,
        @SerializedName("html_url")
        val htmlUrl: String,
        @SerializedName("node_id")
        val nodeId: String,
        @SerializedName("parents")
        val parents: List<Parent>,
        @SerializedName("sha")
        val sha: String,
        @SerializedName("url")
        val url: String
    ) {
        data class Author(
            @SerializedName("avatar_url")
            val avatarUrl: String,
            @SerializedName("events_url")
            val eventsUrl: String,
            @SerializedName("followers_url")
            val followersUrl: String,
            @SerializedName("following_url")
            val followingUrl: String,
            @SerializedName("gists_url")
            val gistsUrl: String,
            @SerializedName("gravatar_id")
            val gravatarId: String,
            @SerializedName("html_url")
            val htmlUrl: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("login")
            val login: String,
            @SerializedName("node_id")
            val nodeId: String,
            @SerializedName("organizations_url")
            val organizationsUrl: String,
            @SerializedName("received_events_url")
            val receivedEventsUrl: String,
            @SerializedName("repos_url")
            val reposUrl: String,
            @SerializedName("site_admin")
            val siteAdmin: Boolean,
            @SerializedName("starred_url")
            val starredUrl: String,
            @SerializedName("subscriptions_url")
            val subscriptionsUrl: String,
            @SerializedName("type")
            val type: String,
            @SerializedName("url")
            val url: String
        )
    
        data class Commit(
            @SerializedName("author")
            val author: Author,
            @SerializedName("comment_count")
            val commentCount: Int,
            @SerializedName("committer")
            val committer: Committer,
            @SerializedName("message")
            val message: String,
            @SerializedName("tree")
            val tree: Tree,
            @SerializedName("url")
            val url: String,
            @SerializedName("verification")
            val verification: Verification
        ) {
            data class Author(
                @SerializedName("date")
                val date: String,
                @SerializedName("email")
                val email: String,
                @SerializedName("name")
                val name: String
            )
    
            data class Committer(
                @SerializedName("date")
                val date: String,
                @SerializedName("email")
                val email: String,
                @SerializedName("name")
                val name: String
            )
    
            data class Tree(
                @SerializedName("sha")
                val sha: String,
                @SerializedName("url")
                val url: String
            )
    
            data class Verification(
                @SerializedName("payload")
                val payload: String?,
                @SerializedName("reason")
                val reason: String,
                @SerializedName("signature")
                val signature: String?,
                @SerializedName("verified")
                val verified: Boolean
            )
        }
    
        data class Committer(
            @SerializedName("avatar_url")
            val avatarUrl: String,
            @SerializedName("events_url")
            val eventsUrl: String,
            @SerializedName("followers_url")
            val followersUrl: String,
            @SerializedName("following_url")
            val followingUrl: String,
            @SerializedName("gists_url")
            val gistsUrl: String,
            @SerializedName("gravatar_id")
            val gravatarId: String,
            @SerializedName("html_url")
            val htmlUrl: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("login")
            val login: String,
            @SerializedName("node_id")
            val nodeId: String,
            @SerializedName("organizations_url")
            val organizationsUrl: String,
            @SerializedName("received_events_url")
            val receivedEventsUrl: String,
            @SerializedName("repos_url")
            val reposUrl: String,
            @SerializedName("site_admin")
            val siteAdmin: Boolean,
            @SerializedName("starred_url")
            val starredUrl: String,
            @SerializedName("subscriptions_url")
            val subscriptionsUrl: String,
            @SerializedName("type")
            val type: String,
            @SerializedName("url")
            val url: String
        )
    
        data class Parent(
            @SerializedName("html_url")
            val htmlUrl: String,
            @SerializedName("sha")
            val sha: String,
            @SerializedName("url")
            val url: String
        )
    }
}
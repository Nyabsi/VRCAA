package cc.sovellus.vrcaa.api.justhparty

import cc.sovellus.vrcaa.api.BaseClient
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.Headers
import java.net.URLEncoder

class JustHPartyProvider : BaseClient() {

    private suspend fun sendRequest(params: String): String?
    {
        val headers = Headers.Builder()
        headers["User-Agent"] = "VRCAA/0.1"
        headers["Referer"] = "vrcaa.sovellus.cc"

        val result = doRequest(
            method = "GET",
            url = "https://avtr.just-h.party/vrcx_search.php$params",
            headers = headers,
            body = null
        )

        return when (result) {
            is Result.Succeeded -> {
                return result.body
            }
            else -> {
                null
            }
        }
    }

    suspend fun search(type: String, query: String, n: Int): ArrayList<Avatar>?
    {
        return when (val result = sendRequest("?${type}=${URLEncoder.encode(query)}&n=5000")) {
            is String -> {
                Gson().fromJson(result, Avatars::class.java)
            }
            else -> null
        }
    }

    class Avatars : ArrayList<Avatar>()

    data class Avatar(
        @SerializedName("authorId")
        val authorId: String,
        @SerializedName("authorName")
        val authorName: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("imageUrl")
        val imageUrl: Any?,
        @SerializedName("name")
        val name: String,
        @SerializedName("releaseStatus")
        val releaseStatus: String,
        @SerializedName("thumbnailImageUrl")
        val thumbnailImageUrl: String
    )
}
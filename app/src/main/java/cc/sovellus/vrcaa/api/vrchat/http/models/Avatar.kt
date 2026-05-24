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

package cc.sovellus.vrcaa.api.vrchat.http.models


import com.google.gson.annotations.SerializedName

data class Avatar(
    @SerializedName("attribution")
    var attribution: Any = Any(),
    @SerializedName("authorId")
    var authorId: String = "",
    @SerializedName("authorName")
    var authorName: String = "",
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("description")
    var description: String = "",
    @SerializedName("featured")
    var featured: Boolean = false,
    @SerializedName("id")
    var id: String = "",
    @SerializedName("imageUrl")
    var imageUrl: String = "",
    @SerializedName("listingDate")
    var listingDate: Any = Any(),
    @SerializedName("name")
    var name: String = "",
    @SerializedName("performance")
    var performance: Performance = Performance(),
    @SerializedName("releaseStatus")
    var releaseStatus: String = "",
    @SerializedName("searchable")
    var searchable: Boolean = false,
    @SerializedName("styles")
    var styles: Styles = Styles(),
    @SerializedName("tags")
    var tags: List<String> = listOf(),
    @SerializedName("thumbnailImageUrl")
    var thumbnailImageUrl: String = "",
    @SerializedName("unityPackages")
    var unityPackages: List<UnityPackage> = listOf(),
    @SerializedName("updated_at")
    var updatedAt: String = "",
    @SerializedName("version")
    var version: Int = 0
) {
    data class Performance(
        @SerializedName("android")
        var android: String = "",
        @SerializedName("android-sort")
        var androidSort: Int = 0,
        @SerializedName("standalonewindows")
        var standalonewindows: String = "",
        @SerializedName("standalonewindows-sort")
        var standalonewindowsSort: Int = 0
    )

    data class Styles(
        @SerializedName("primary")
        var primary: String = "",
        @SerializedName("secondary")
        var secondary: String = ""
    )

    data class UnityPackage(
        @SerializedName("assetVersion")
        var assetVersion: Int = 0,
        @SerializedName("created_at")
        var createdAt: String = "",
        @SerializedName("id")
        var id: String = "",
        @SerializedName("impostorizerVersion")
        var impostorizerVersion: String = "",
        @SerializedName("performanceRating")
        var performanceRating: String = "",
        @SerializedName("platform")
        var platform: String = "",
        @SerializedName("scanStatus")
        var scanStatus: String = "",
        @SerializedName("unityVersion")
        var unityVersion: String = "",
        @SerializedName("variant")
        var variant: String = ""
    )
}
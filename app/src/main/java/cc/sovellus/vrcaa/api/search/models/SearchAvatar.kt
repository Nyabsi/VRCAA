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

package cc.sovellus.vrcaa.api.search.models

import com.google.gson.annotations.SerializedName

data class SearchAvatar(
    @SerializedName("acknowledgements")
    var acknowledgements: Any = Any(),
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
    var imageUrl: String = "",
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
        var androidRating: Any = Any(),
        @SerializedName("has_impostor")
        var hasImpostor: Boolean = false,
        @SerializedName("ios_rating")
        var iosRating: Any = Any(),
        @SerializedName("pc_rating")
        var pcRating: String = ""
    )

    data class Styles(
        @SerializedName("primary")
        var primary: Any = Any(),
        @SerializedName("secondary")
        var secondary: Any = Any()
    )

    data class Tags(
        @SerializedName("author_tags")
        var authorTags: List<Any> = listOf(),
        @SerializedName("content_tags")
        var contentTags: List<Any> = listOf(),
        @SerializedName("non_content_tags")
        var nonContentTags: List<Any> = listOf()
    )
}
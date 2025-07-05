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

data class File(
    @SerializedName("extension")
    var extension: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("mimeType")
    var mimeType: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("ownerId")
    var ownerId: String,
    @SerializedName("tags")
    var tags: List<String>,
    @SerializedName("versions")
    var versions: List<Version>
) {
    data class Version(
        @SerializedName("created_at")
        var createdAt: String,
        @SerializedName("file")
        var `file`: File,
        @SerializedName("status")
        var status: String,
        @SerializedName("version")
        var version: Int
    ) {
        data class File(
            @SerializedName("category")
            var category: String,
            @SerializedName("fileName")
            var fileName: String,
            @SerializedName("sizeInBytes")
            var sizeInBytes: Int,
            @SerializedName("status")
            var status: String,
            @SerializedName("uploadId")
            var uploadId: String,
            @SerializedName("url")
            var url: String
        )
    }
}
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

data class Print(
    @SerializedName("authorId")
    var authorId: String,
    @SerializedName("authorName")
    var authorName: String,
    @SerializedName("createdAt")
    var createdAt: String,
    @SerializedName("files")
    var files: Files,
    @SerializedName("id")
    var id: String,
    @SerializedName("note")
    var note: String,
    @SerializedName("ownerId")
    var ownerId: String,
    @SerializedName("sharedBy")
    var sharedBy: String,
    @SerializedName("timestamp")
    var timestamp: String,
    @SerializedName("worldId")
    var worldId: String,
    @SerializedName("worldName")
    var worldName: String
) {
    data class Files(
        @SerializedName("fileId")
        var fileId: String,
        @SerializedName("image")
        var image: String
    )
}
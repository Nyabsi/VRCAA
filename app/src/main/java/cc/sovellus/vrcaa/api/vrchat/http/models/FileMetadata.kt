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

data class FileMetadata(
    @SerializedName("extension")
    val extension: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("mimeType")
    val mimeType: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("ownerId")
    val ownerId: String,
    @SerializedName("tags")
    val tags: List<Any>,
    @SerializedName("versions")
    val versions: List<Version>
) {
    data class Version(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("delta")
        val delta: Delta?,
        @SerializedName("file")
        val `file`: File?,
        @SerializedName("signature")
        val signature: Signature?,
        @SerializedName("status")
        val status: String,
        @SerializedName("version")
        val version: Int
    ) {
        data class Delta(
            @SerializedName("category")
            val category: String,
            @SerializedName("fileName")
            val fileName: String,
            @SerializedName("md5")
            val md5: String,
            @SerializedName("sizeInBytes")
            val sizeInBytes: Int,
            @SerializedName("status")
            val status: String,
            @SerializedName("uploadId")
            val uploadId: String,
            @SerializedName("url")
            val url: String
        )

        data class File(
            @SerializedName("category")
            val category: String,
            @SerializedName("fileName")
            val fileName: String,
            @SerializedName("md5")
            val md5: String,
            @SerializedName("sizeInBytes")
            val sizeInBytes: Int,
            @SerializedName("status")
            val status: String,
            @SerializedName("uploadId")
            val uploadId: String,
            @SerializedName("url")
            val url: String
        )

        data class Signature(
            @SerializedName("category")
            val category: String,
            @SerializedName("fileName")
            val fileName: String,
            @SerializedName("md5")
            val md5: String,
            @SerializedName("sizeInBytes")
            val sizeInBytes: Int,
            @SerializedName("status")
            val status: String,
            @SerializedName("uploadId")
            val uploadId: String,
            @SerializedName("url")
            val url: String
        )
    }
}
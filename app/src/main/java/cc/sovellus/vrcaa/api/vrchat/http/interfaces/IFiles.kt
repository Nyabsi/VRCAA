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

package cc.sovellus.vrcaa.api.vrchat.http.interfaces

import android.net.Uri
import cc.sovellus.vrcaa.api.vrchat.http.models.File
import cc.sovellus.vrcaa.api.vrchat.http.models.FileMetadata

interface IFiles {
    enum class ImageAspectRatio {
        IMAGE_ASPECT_RATIO_ANY,
        IMAGE_ASPECT_RATIO_SQUARE
    }

    suspend fun fetchMetadataByFileId(fileId: String): FileMetadata?
    suspend fun fetchFilesByTag(tag: String, n: Int = 100, offset: Int = 0): ArrayList<File>
    suspend fun fetchFilesByTagWithUserId(tag: String, userId: String, n: Int = 100, offset: Int = 0): ArrayList<File>
    suspend fun uploadImage(tag: String, file: Uri, aspectRatio: ImageAspectRatio = ImageAspectRatio.IMAGE_ASPECT_RATIO_ANY): File?
    suspend fun uploadEmoji(type: String, file: Uri): File?
}
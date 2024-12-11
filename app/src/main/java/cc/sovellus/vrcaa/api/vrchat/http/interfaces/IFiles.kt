package cc.sovellus.vrcaa.api.vrchat.http.interfaces

import cc.sovellus.vrcaa.api.vrchat.http.models.FileMetadata

interface IFiles {
    suspend fun fetchMetadataByFileId(fileId: String): FileMetadata?
}
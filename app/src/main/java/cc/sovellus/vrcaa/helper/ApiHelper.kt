package cc.sovellus.vrcaa.helper

object ApiHelper {
    fun extractFileIdFromUrl(imageUrl: String): String? {
        val startIndex = imageUrl.indexOf("file_")
        val endIndex = imageUrl.indexOf("/", startIndex)
        if (startIndex != -1 && endIndex != -1) {
            val fileId = imageUrl.substring(startIndex, endIndex)
            return fileId
        }
        return null
    }
}
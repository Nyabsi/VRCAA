package cc.sovellus.vrcaa.api.vrchat.http.interfaces

import android.net.Uri
import cc.sovellus.vrcaa.api.vrchat.http.models.Print
import java.time.LocalDateTime

interface IPrints {

    suspend fun fetchPrintsByUserId(userId: String): ArrayList<Print>
    suspend fun fetchPrint(printId: String): Print?
    suspend fun deletePrint(printId: String): Print?
    suspend fun editPrint(printId: String): Print?
    suspend fun uploadPrint(file: Uri, note: String, timestamp: LocalDateTime): Print?
}
package cc.sovellus.vrcaa.api.vrchat.http.interfaces

import cc.sovellus.vrcaa.api.vrchat.http.models.Print

interface IPrints {

    suspend fun fetchPrintsByUserId(userId: String): ArrayList<Print>
    suspend fun fetchPrint(printId: String): Print?
    suspend fun deletePrint(printId: String): Print?
    suspend fun editPrint(printId: String): Print?
}
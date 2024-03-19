package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.api.http.ApiContext
import kotlinx.coroutines.InternalCoroutinesApi

object ApiManager {

    @Volatile lateinit var api: ApiContext

    @Synchronized
    fun set(apiContext: ApiContext) {
        synchronized(this) {
            api = apiContext
        }
    }
}
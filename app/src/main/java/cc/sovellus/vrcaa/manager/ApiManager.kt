package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.api.http.ApiContext

object ApiManager {

    @Volatile lateinit var api: ApiContext

    @Synchronized
    fun set(apiContext: ApiContext) {
        synchronized(this) {
            api = apiContext
        }
    }
}
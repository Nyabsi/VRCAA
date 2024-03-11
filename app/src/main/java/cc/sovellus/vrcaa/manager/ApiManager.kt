package cc.sovellus.vrcaa.manager

import android.content.Context
import cc.sovellus.vrcaa.api.http.ApiContext
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@OptIn(InternalCoroutinesApi::class)
class ApiManager(
    private val context: Context
) {
    companion object {
        @Volatile
        var api: ApiContext? = null
    }

    fun set(apiContext: ApiContext) {
        synchronized(apiContext) {
            api = apiContext
        }
    }

    fun get(): ApiContext {
        if (api == null) {
            val apiContext = ApiContext(context)
            synchronized(apiContext) {
                api = apiContext
            }
        }
        return api!!
    }
}
package cc.sovellus.vrcaa.manager

import android.content.Context
import cc.sovellus.vrcaa.api.ApiContext
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@OptIn(InternalCoroutinesApi::class)
class ApiManager(
    private val context: Context
) {
    companion object {
        @Volatile var api: ApiContext? = null
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
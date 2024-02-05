package cc.sovellus.vrcaa.helper

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import cc.sovellus.vrcaa.api.ApiContext
import cc.sovellus.vrcaa.manager.ApiManager

fun Context.isMyServiceRunning(serviceClass: Class<out Service>) = try {
    (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Int.MAX_VALUE)
        .any { it.service.className == serviceClass.name }
} catch (e: Exception) {
    false
}

internal val Context.api: ApiContext
    get() = ApiManager(this).get()
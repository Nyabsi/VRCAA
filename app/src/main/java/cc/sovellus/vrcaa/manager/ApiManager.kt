package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.api.vrchat.http.HttpClient

object ApiManager {
    val api by lazy { HttpClient() }
}
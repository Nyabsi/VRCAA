package cc.sovellus.vrcaa.api.vrchat

import cc.sovellus.vrcaa.BuildConfig

object Config {
    const val API_BASE_URL = "https://api.vrchat.cloud/api/1"
    const val API_USER_AGENT = "VRCAA/${BuildConfig.VERSION_NAME} nyabsi@sovellus.cc"
    const val PIPELINE_BASE_URL = "wss://pipeline.vrchat.cloud"
}
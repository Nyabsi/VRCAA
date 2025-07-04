/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.sovellus.vrcaa.api.vrchat

import cc.sovellus.vrcaa.BuildConfig

object Config {
    /* General */
    const val API_USER_AGENT = "VRCAA/${BuildConfig.VERSION_NAME} nyabsi@sovellus.cc"
    /* API */
    const val API_BASE_URL = "https://api.vrchat.cloud/api/1"
    const val MAX_TOKEN_REFRESH_ATTEMPT = 1
    /* Pipeline */
    const val PIPELINE_BASE_URL = "wss://pipeline.vrchat.cloud"
    const val RECONNECTION_INTERVAL: Long = 15000
}
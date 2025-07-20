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

package cc.sovellus.vrcaa.api.search

import cc.sovellus.vrcaa.BuildConfig

object Config {
    const val API_USER_AGENT = "VRCAA/${BuildConfig.VERSION_NAME} nyabsi@sovellus.cc"
    const val API_REFERER = "vrcaa.sovellus.cc"
    const val AVTR_DB_API_BASE_URL = "https://api.avtrdb.com/v2"
    const val JUST_H_PARTY_API_BASE_URL = "https://avtr.just-h.party"
}
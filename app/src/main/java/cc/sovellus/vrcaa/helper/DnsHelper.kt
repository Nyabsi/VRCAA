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

package cc.sovellus.vrcaa.helper

import okhttp3.Dns
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface

class DnsHelper() : Dns {
    override fun lookup(hostname: String): List<InetAddress> {
        val query = Dns.SYSTEM.lookup(hostname)
        val queryIPv4Filtered = query.filter { Inet4Address::class.java.isInstance(it) }
        // if IPv4 exists, prioritize IPv4 if it doesn't then fallback to whatever is available.
        return if (queryIPv4Filtered.isNotEmpty() && hasIPv4()) {
            queryIPv4Filtered
        } else {
            query
        }
    }

    private fun hasIPv4(): Boolean {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        for (i in interfaces) {
            for (address in i.inetAddresses) {
                if (!address.isLoopbackAddress && address is Inet4Address) {
                    return true
                }
            }
        }
        return false
    }
}
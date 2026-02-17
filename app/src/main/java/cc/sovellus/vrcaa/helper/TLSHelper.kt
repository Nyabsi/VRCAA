package cc.sovellus.vrcaa.helper

import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class TLSHelper {
    val sslContext: SSLContext = try {
        SSLContext.getInstance("TLSv1.3")
    } catch (_: NoSuchAlgorithmException) {
        SSLContext.getInstance("TLSv1.2")
    }

    init {
        sslContext.init(null, null, null)
    }

    fun getSSLContext(): SSLContext {
        return sslContext
    }

    fun systemDefaultTrustManager(): X509TrustManager {
        val trustManagerFactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        )
        trustManagerFactory.init(null as KeyStore?)
        val trustManagers = trustManagerFactory.trustManagers
        return trustManagers
            .filterIsInstance<X509TrustManager>()
            .firstOrNull()
            ?: throw IllegalStateException("No X509TrustManager found")
    }
}

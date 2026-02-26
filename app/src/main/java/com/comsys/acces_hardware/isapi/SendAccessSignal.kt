package com.comsys.acces_hardware.isapi

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.DigestAuthCredentials
import io.ktor.client.plugins.auth.providers.digest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class AccessController {

    // Trusts all certificates — equivalent to curl -k, safe for known local LAN devices
    private val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    })

    private val sslContext = SSLContext.getInstance("TLS").also {
        it.init(null, trustAllCerts, SecureRandom())
    }

    private val client = HttpClient(Android) {
        expectSuccess = false
        install(Auth) {
            digest {
                credentials {
                    DigestAuthCredentials(
                        username = "",
                        password = ""
                    )
                }
                // No realm filter — accepts any realm the server advertises
            }
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }

        engine {
            connectTimeout = 30_000
            socketTimeout = 30_000
            sslManager = { httpsURLConnection ->
                httpsURLConnection.sslSocketFactory = sslContext.socketFactory
                httpsURLConnection.hostnameVerifier = HostnameVerifier { _, _ -> true }
            }
        }
    }

    suspend fun openDoor(doorNumber: Int = 1): Result<String> = runCatching {
        val xmlBody = """
            <RemoteControlDoor version="2.0" xmlns="http://www.isapi.org/ver20/XMLSchema">
                <cmd>open</cmd>
            </RemoteControlDoor>
        """.trimIndent()

        val response: HttpResponse = client.put("https://192.168.100.5/ISAPI/AccessControl/RemoteControl/door/$doorNumber") {
            contentType(ContentType.Application.Xml)
            setBody(xmlBody)
        }

        val body = response.bodyAsText()

        if (!response.status.isSuccess()) {
            throw Exception("HTTP ${response.status.value} ${response.status.description} — $body")
        }

        body
    }

    fun close() {
        client.close()
    }
}

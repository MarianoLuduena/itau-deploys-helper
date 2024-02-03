package com.itau.deploys.http

import sttp.client3.{HttpClientSyncBackend, Identity, SttpBackend}

import java.net.http.HttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.{SSLContext, X509TrustManager}

object HttpBackend {

  private def httpClient(): HttpClient = {
    val trustManager = new X509TrustManager {
      def checkClientTrusted(chain: Array[X509Certificate], authType: String): Unit = {}

      def checkServerTrusted(chain: Array[X509Certificate], authType: String): Unit = {}

      def getAcceptedIssuers: Array[X509Certificate] = null
    }
    val ssl = SSLContext.getInstance("TLS")
    ssl.init(Array(), Array(trustManager), SecureRandom())

    HttpClient.newBuilder().sslContext(ssl).build()
  }

  def backend(): SttpBackend[Identity, Any] = {
    HttpClientSyncBackend.usingClient(httpClient())
  }

}

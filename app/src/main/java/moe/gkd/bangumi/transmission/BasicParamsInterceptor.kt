package moe.gkd.bangumi.transmission

import android.util.Log
import moe.gkd.bangumi.*
import okhttp3.Interceptor
import okhttp3.Response

class BasicParamsInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        Log.e("BasicParamsInterceptor", "intercept: 执行")
        val request = chain.request()
        val url = request.url
        val requestBuilder = request.newBuilder()
        val headerBuilder = request.headers.newBuilder()
        val host = MainApplication.INSTANCE.hashMap[TRANSMISSION_HOST].toString()
        val scheme =
            if (MainApplication.INSTANCE.hashMap[TRANSMISSION_SSL] == true) "https" else "http"
        val port = MainApplication.INSTANCE.hashMap[TRANSMISSION_PORT].toString().toInt()
        val newHttpUrl = url.newBuilder()
            .host(host)
            .scheme(scheme)
            .port(port)
            .build()
        requestBuilder.url(newHttpUrl)
        val sessionId =
            MainApplication.INSTANCE.hashMap.getOrDefault(TRANSMISSION_SESSION_ID, "").toString()
        val authenticator =
            MainApplication.INSTANCE.hashMap.getOrDefault(TRANSMISSION_AUTHORIZATION, "").toString()
        if (sessionId.isNotEmpty()) {
            headerBuilder.add("X-Transmission-Session-Id", sessionId)
        }
        if (authenticator.isNotEmpty()) {
            headerBuilder.add("Authorization", authenticator)
        }
        requestBuilder.headers(headerBuilder.build())
        val newReq = requestBuilder.build()
        Log.e("BasicParamsInterceptor", "intercept: ${newReq.url.host}")
        return chain.proceed(newReq).also {
            if (it.code == 401) {
                MainApplication.INSTANCE.hashMap.remove(TRANSMISSION_AUTHORIZATION)
            } else if (it.code == 409) {
                MainApplication.INSTANCE.hashMap.remove(TRANSMISSION_SESSION_ID)
            }
        }
    }
}
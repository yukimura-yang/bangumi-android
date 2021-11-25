package moe.gkd.bangumi.transmission

import moe.gkd.bangumi.MainApplication
import moe.gkd.bangumi.TRANSMISSION_AUTHORIZATION
import moe.gkd.bangumi.TRANSMISSION_SESSION_ID
import okhttp3.Interceptor
import okhttp3.Response

class BasicParamsInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()
        val headerBuilder = request.headers.newBuilder()
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
        val response = chain.proceed(requestBuilder.build())
        if (response.code == 401) {
            MainApplication.INSTANCE.hashMap.remove(TRANSMISSION_AUTHORIZATION)
        } else if (response.code == 409) {
            MainApplication.INSTANCE.hashMap.remove(TRANSMISSION_SESSION_ID)
        }
        return response
    }
}
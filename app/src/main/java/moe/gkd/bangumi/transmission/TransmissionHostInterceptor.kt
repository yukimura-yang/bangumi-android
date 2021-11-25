package moe.gkd.bangumi.transmission

import moe.gkd.bangumi.*
import okhttp3.Interceptor
import okhttp3.Response
import java.lang.Exception

class TransmissionHostInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val oldUrl = originalRequest.url

        try {
            if (oldUrl.host == TRANSMISSION_DEFAULT_HOST) {
                val host = MainApplication.INSTANCE.hashMap[TRANSMISSION_HOST].toString()
                val scheme =
                    if (MainApplication.INSTANCE.hashMap[TRANSMISSION_SSL] == true) "https" else "http"
                val port = MainApplication.INSTANCE.hashMap[TRANSMISSION_PORT].toString().toInt()
                val builder = originalRequest.newBuilder()
                val newHttpUrl = oldUrl.newBuilder()
                    .host(host)
                    .scheme(scheme)
                    .port(port)
                    .build()
                val request = builder.url(newHttpUrl).build()
                return chain.proceed(request)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return chain.proceed(originalRequest)
    }
}
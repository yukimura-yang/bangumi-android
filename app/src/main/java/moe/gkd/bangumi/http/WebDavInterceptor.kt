package moe.gkd.bangumi.http

import okhttp3.Interceptor
import okhttp3.Response
import java.net.SocketTimeoutException

class WebDavInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        try {
            return chain.proceed(request)
        } catch (e: SocketTimeoutException) {
            return intercept(chain)
        }
    }
}
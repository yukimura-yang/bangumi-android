package moe.gkd.bangumi.http

import android.util.Log
import moe.gkd.bangumi.*
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 切换CDN
 */
class MoreBaseUrlInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val oldUrl = originalRequest.url
        Log.e("okhttp", "${oldUrl.encodedPath}")
        if (oldUrl.host == BANGUMI_MOE_HOST) {
            if (MainApplication.INSTANCE.hashMap[USE_BANGUMI_MOE_CDN] == true) {
                //切换CDN
                val builder = originalRequest.newBuilder()
                val newHttpUrl = oldUrl.newBuilder()
                    .host(BANGUMI_MOE_CDN_HOST)
                    .build()
                val request = builder.url(newHttpUrl).build()
                return chain.proceed(request)
            }
        }
        return chain.proceed(originalRequest)
    }
}
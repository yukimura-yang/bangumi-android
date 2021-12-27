package moe.gkd.bangumi.ui.utils

import android.util.Log
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import moe.gkd.bangumi.MainApplication
import moe.gkd.bangumi.WEBDAV_ADDRESS
import moe.gkd.bangumi.WEBDAV_PASSWORD
import moe.gkd.bangumi.WEBDAV_USERNAME
import moe.gkd.bangumi.http.DnsUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.InputStream
import java.util.concurrent.TimeUnit

object WebDavUtils {
    private val sardine = OkHttpSardine(
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .dns(DnsUtils.getDns())
            .build()
    )

    fun init() {
        sardine.setCredentials(getUserName(), getPassword(), true)
    }

    fun getUserName(): String {
        return MainApplication.INSTANCE.hashMap[WEBDAV_USERNAME].toString()
    }

    fun getPassword(): String {
        return MainApplication.INSTANCE.hashMap[WEBDAV_PASSWORD].toString()
    }

    fun getAddress(): String {
        return MainApplication.INSTANCE.hashMap[WEBDAV_ADDRESS].toString().let {
            if (it.endsWith("/")) {
                it.substring(0, it.length - 1)
            } else {
                it
            }
        }
    }

    fun download(path: String): InputStream {
        return sardine.get("${getAddress()}$path")
    }


    /*
  * 日志拦截器
  * */
    private fun initLogInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor { message -> Log.i("Retrofit", message) }
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    fun getSardine(): OkHttpSardine = sardine
}
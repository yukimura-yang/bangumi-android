package moe.gkd.bangumi.ui.utils

import android.util.Log
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import moe.gkd.bangumi.MainApplication
import moe.gkd.bangumi.WEBDAV_ADDRESS
import moe.gkd.bangumi.WEBDAV_PASSWORD
import moe.gkd.bangumi.WEBDAV_USERNAME
import moe.gkd.bangumi.http.DnsUtils
import moe.gkd.bangumi.http.WebDavInterceptor
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

object WebDavUtils {
    private val sardine = OkHttpSardine(
        OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(WebDavInterceptor())
//            .addInterceptor(initLogInterceptor())
            .addNetworkInterceptor(StethoInterceptor())
            .dns(DnsUtils.getDns())
            .build()
    )

    fun init() {
        sardine.setCredentials(getUserName(), getPassword(), true)
    }

    private fun getUserName(): String {
        return MainApplication.INSTANCE.hashMap[WEBDAV_USERNAME].toString()
    }

    private fun getPassword(): String {
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
        interceptor.level = HttpLoggingInterceptor.Level.HEADERS
        return interceptor
    }

    fun getSardine(): OkHttpSardine = sardine
}
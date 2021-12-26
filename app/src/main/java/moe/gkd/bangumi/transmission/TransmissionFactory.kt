package moe.gkd.bangumi.transmission

import android.util.Log
import com.facebook.stetho.okhttp3.StethoInterceptor
import moe.gkd.bangumi.TRANSMISSION_DEFAULT_URL
import moe.gkd.bangumi.http.DnsUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class TransmissionFactory private constructor() {
    private val retrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(TRANSMISSION_DEFAULT_URL)
            .client(initOkhttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    companion object {
        val instance: TransmissionFactory by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            TransmissionFactory()
        }
    }

    private fun initOkhttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .authenticator(TransmissionAuthenticator())
            .addInterceptor(BasicParamsInterceptor())
            .addInterceptor(initLogInterceptor())
            .addNetworkInterceptor(StethoInterceptor())
            .dns(DnsUtils.getDns())
            .build()
    }

    /*
    * 具体服务实例化
    * */
    fun <T> getService(service: Class<T>): T {
        return retrofit.create(service)
    }

    /*
    * 日志拦截器
    * */
    private fun initLogInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor { message -> Log.i("Transmission", message) }
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }
}
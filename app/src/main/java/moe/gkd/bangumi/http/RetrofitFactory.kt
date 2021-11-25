package moe.gkd.bangumi.http

import android.util.Log
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class RetrofitFactory private constructor() {
    private val retrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
            .baseUrl("https://bangumi.moe")
            .client(initOkhttpClient())
            .addConverterFactory(JsonOrXmlConverterFactory.create())
            .build()
    }

    companion object {
        val instance: RetrofitFactory by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RetrofitFactory()
        }
    }

    private fun initOkhttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(MoreBaseUrlInterceptor())
            .addInterceptor(initLogInterceptor())
            .addNetworkInterceptor(StethoInterceptor())
            .dns(CustomDns())
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
        val interceptor = HttpLoggingInterceptor { message -> Log.i("Retrofit", message) }
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }
}
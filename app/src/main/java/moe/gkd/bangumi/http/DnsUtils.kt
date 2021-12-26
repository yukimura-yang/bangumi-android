package moe.gkd.bangumi.http

import moe.gkd.bangumi.MainApplication
import okhttp3.Cache
import okhttp3.Dns
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import java.io.File

object DnsUtils {
    private lateinit var dns: Dns

    fun getDns(): Dns {
        if (!this::dns.isInitialized) {
            val appCache =
                Cache(File(MainApplication.INSTANCE.cacheDir, "okhttpcache"), 10 * 1024 * 1024)
            val bootstrapClient = OkHttpClient.Builder().cache(appCache).build()
            dns = DnsOverHttps.Builder().client(bootstrapClient)
                .url("https://223.5.5.5/dns-query".toHttpUrl())
                .build()
        }
        return dns
    }
}
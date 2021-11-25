package moe.gkd.bangumi.http

import okhttp3.Dns
import okhttp3.Dns.Companion.SYSTEM
import java.net.InetAddress
import java.util.*

class CustomDns : Dns {
    override fun lookup(hostname: String): List<InetAddress> {
        if (hostname == "bangumi.moe") {
            val byAddress = InetAddress.getByAddress(hostname, byteArrayOf(1, 1, 1, 1))
            return Collections.singletonList(byAddress)
        }
        return SYSTEM.lookup(hostname)
    }
}
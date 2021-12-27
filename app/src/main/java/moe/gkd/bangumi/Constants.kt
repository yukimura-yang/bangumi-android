package moe.gkd.bangumi

import android.graphics.Color
import com.thegrizzlylabs.sardineandroid.DavResource
import java.io.InputStream
import java.io.OutputStream
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

const val BANGUMI_ID = "bangumiid"
const val BANGUMI_TITLE = "bangumititle"
const val BANGUMI_MOE_HOST = "bangumi.moe"
const val BANGUMI_MOE_HOST_URL = "https://${BANGUMI_MOE_HOST}/"
const val BANGUMI_MOE_CDN_HOST = "bangumi-rss.gkdmoe.workers.dev"
const val BANGUMI_MOE_HOST_CDN_URL = "https://${BANGUMI_MOE_CDN_HOST}/"
const val TRANSMISSION_DEFAULT_HOST = "transmission.dev"
const val TRANSMISSION_DEFAULT_URL = "https://${TRANSMISSION_DEFAULT_HOST}/"

//HASHMAP KEY
const val USE_BANGUMI_MOE_CDN = "use_bangumi_moe_cdn"
const val TRANSMISSION_HOST = "transmission_host"
const val TRANSMISSION_PORT = "transmission_port"
const val TRANSMISSION_SSL = "transmission_ssl"
const val TRANSMISSION_USERNAME = "transmission_username"
const val TRANSMISSION_PASSWORD = "transmission_password"
const val TRANSMISSION_RPC = "transmission_rpc"
const val TRANSMISSION_SAVE_DIR = "transmission_save_dir"
const val TRANSMISSION_SESSION_ID = "transmission_session_id"
const val TRANSMISSION_AUTHORIZATION = "transmission_authorization"
const val WEBDAV_ADDRESS = "webdav_address"
const val WEBDAV_USERNAME = "webdav_username"
const val WEBDAV_PASSWORD = "webdav_password"

fun utc2Local(utcTime: String): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm E")
    return Instant.parse(utcTime)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}

fun timestamp2Local(timestamp: Long): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm E")
    return Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}

fun utc2Timestamp(utcTime: String): Long {
    return Instant.parse(utcTime).toEpochMilli()
}

fun gmt2utc(gmtTime: String): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(
        "E, dd MMM yyyy HH:mm:ss zzz",
        Locale.US
    )
    val parse = LocalDateTime.parse(gmtTime, formatter)
    val timestamp =
        LocalDateTime.from(parse).atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
    return timestamp2utc(timestamp)
}

fun gtm2Timestamp(gmtTime: String): Long {
    if (gmtTime.isEmpty()) return 0
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(
        "E, dd MMM yyyy HH:mm:ss zzz",
        Locale.US
    )
    val parse = LocalDateTime.parse(gmtTime, formatter)
    return LocalDateTime.from(parse).atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
}

fun timestamp2utc(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val str = formatter.format(
        LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp),
            ZoneId.systemDefault()
        )
    )
    return str
}

fun dayOfWeek2JpWeek(dayOfWeek: DayOfWeek): String {
    return when (dayOfWeek) {
        DayOfWeek.MONDAY -> "周一（月）"
        DayOfWeek.TUESDAY -> "周二（火）"
        DayOfWeek.WEDNESDAY -> "周三（水）"
        DayOfWeek.THURSDAY -> "周四（木）"
        DayOfWeek.FRIDAY -> "周五（金）"
        DayOfWeek.SATURDAY -> "周六（土）"
        DayOfWeek.SUNDAY -> "周日（日）"
    }
}

fun dayOfWeekColor(dayOfWeek: DayOfWeek): Int {
    return when (dayOfWeek) {
        DayOfWeek.MONDAY -> Color.parseColor("#FFD700")
        DayOfWeek.TUESDAY -> Color.parseColor("#FF69B4")
        DayOfWeek.WEDNESDAY -> Color.parseColor("#008000")
        DayOfWeek.THURSDAY -> Color.parseColor("#FF7F00")
        DayOfWeek.FRIDAY -> Color.parseColor("#4169E1")
        DayOfWeek.SATURDAY -> Color.parseColor("#8B00FF")
        DayOfWeek.SUNDAY -> Color.parseColor("#E60000")
    }
}

/**
 * transmission是否准备好了
 */
fun transmissionReady(): Boolean {
    val hashMap = MainApplication.INSTANCE.hashMap
    if (hashMap[TRANSMISSION_HOST].toString().isEmpty()) return false
    if (hashMap[TRANSMISSION_PORT].toString().isEmpty()) return false
    if (hashMap[TRANSMISSION_RPC].toString().isEmpty()) return false
    if (hashMap[TRANSMISSION_SAVE_DIR].toString().isEmpty()) return false
    return true
}

fun Any.TAG(): String {
    return this::class.simpleName ?: ""
}

fun DavResource.isVideoFile(): Boolean {
    if (this.isDirectory) return false
    val name = this.name.lowercase()
    if (name.endsWith(".mp4") || name.endsWith(".mkv")) {
        return true
    }
    return false
}

public fun InputStream.copyTo(
    out: OutputStream,
    onCopy: (bytesCopied: Long) -> Unit
): Long {
    var bytesCopied: Long = 0
    val bufferSize = DEFAULT_BUFFER_SIZE
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        bytesCopied += bytes
        onCopy(bytesCopied)
        bytes = read(buffer)
    }
    return bytesCopied
}

package moe.gkd.bangumi

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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

//合集tag，如果有这个tag就排除
const val BANGUMI_MOE_TAG_COLLECTION = "54967e14ff43b99e284d0bf7"

fun utc2Local(utcTime: String): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm E")
    return Instant.parse(utcTime)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}

fun utc2Timestamp(utcTime: String): Long {
    return Instant.parse(utcTime).toEpochMilli()
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
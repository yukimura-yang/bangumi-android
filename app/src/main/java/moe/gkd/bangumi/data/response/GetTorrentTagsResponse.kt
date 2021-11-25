package moe.gkd.bangumi.data.response

import com.google.gson.annotations.SerializedName

data class TorrentTag(
    val activity: Int,
    val locale: TorrentTagLocale,
    val name: String,
    val type: String,
    @SerializedName("_id")
    val id: String,
)

data class TorrentTagLocale(
    val en: String? = null,
    val ja: String? = null,
    val zh_cn: String? = null,
    val zh_tw: String? = null,
)
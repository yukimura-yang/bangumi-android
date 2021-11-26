package moe.gkd.bangumi.data.response

import com.google.gson.annotations.SerializedName

data class TorrentTag(
    val activity: Int,
    val locale: TorrentTagLocale,
    val name: String,
    val type: String,
    @SerializedName("_id")
    val id: String,
) {
    fun getRecommendName(): String {
        if (locale.zh_cn != null) {
            return locale.zh_cn
        } else if (locale.zh_tw != null) {
            return locale.zh_tw
        } else if (locale.ja != null) {
            return locale.ja
        } else {
            return name
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is TorrentTag) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        var result = 21
        result = result * 89 + id.hashCode()
        return result
    }
}

data class TorrentTagLocale(
    val en: String? = null,
    val ja: String? = null,
    val zh_cn: String? = null,
    val zh_tw: String? = null,
)
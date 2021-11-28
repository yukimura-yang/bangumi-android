package moe.gkd.bangumi.data.response

import com.google.gson.annotations.SerializedName

data class TorrentTeam(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val icon: String,
    val regDate: String
) {
    override fun equals(other: Any?): Boolean {
        if (other is TorrentTeam) {
            if (id == other.id) return true
        }
        return false
    }

    override fun hashCode(): Int {
        var result = 24
        result = result * 11 + id.hashCode()
        return result
    }
}

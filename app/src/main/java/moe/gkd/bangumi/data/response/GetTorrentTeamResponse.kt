package moe.gkd.bangumi.data.response

import com.google.gson.annotations.SerializedName

data class TorrentTeam(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val icon: String,
    val regDate: String
)

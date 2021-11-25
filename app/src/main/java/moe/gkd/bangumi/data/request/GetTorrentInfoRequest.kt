package moe.gkd.bangumi.data.request

import com.google.gson.annotations.SerializedName

data class GetTorrentInfoRequest(
    @SerializedName("_id")
    val id: String
)
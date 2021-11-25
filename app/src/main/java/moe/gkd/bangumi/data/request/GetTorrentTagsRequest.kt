package moe.gkd.bangumi.data.request

import com.google.gson.annotations.SerializedName

data class GetTorrentTagsRequest(
    @SerializedName("_ids")
    val ids: List<String>
)
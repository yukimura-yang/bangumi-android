package moe.gkd.bangumi.data.response

import com.google.gson.annotations.SerializedName

data class GetTorrentInfoResponse(
    @SerializedName("category_tag_id")
    val categoryTagId: String,
    val comments: Int,
    val downloads: Int,
    @SerializedName("file_id")
    val fileId: String,
    val finished: Int,
    val infoHash: String,
    val introduction: String,
    val leechers: Int,
    val magnet: String,
    @SerializedName("publish_time")
    val publishTime: String,
    val seeders: Int,
    val size: String,
    @SerializedName("tag_ids")
    val tagIds: List<String>,
    @SerializedName("team_id")
    val teamId: String?,
    val title: String,
    @SerializedName("uploader_id")
    val uploaderId: String,
    @SerializedName("_id")
    val id: String,
)
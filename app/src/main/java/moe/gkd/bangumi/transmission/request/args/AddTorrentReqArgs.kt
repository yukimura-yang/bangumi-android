package moe.gkd.bangumi.transmission.request.args

import com.google.gson.annotations.SerializedName

class AddTorrentReqArgs(
    //磁力链接
    val filename: String? = null,
    //torrent文件的base64编码
    val metainfo: String? = null,
    @SerializedName("download-dir")
    val downloadDir: String
)
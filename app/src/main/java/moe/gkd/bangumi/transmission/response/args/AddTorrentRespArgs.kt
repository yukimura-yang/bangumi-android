package moe.gkd.bangumi.transmission.response.args

import com.google.gson.annotations.SerializedName
import moe.gkd.bangumi.transmission.entity.TorrentAdded

class AddTorrentRespArgs(
    @SerializedName("torrent-added")
    val torrentAdded: TorrentAdded
)
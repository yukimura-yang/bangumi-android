package moe.gkd.bangumi.transmission.enum

import com.google.gson.annotations.SerializedName

enum class EnumTransmissionMethod {
    @SerializedName("torrent-add")
    TORRENT_ADD,

    @SerializedName("session-get")
    SESSION_GET,

    @SerializedName("torrent-get")
    TORRENT_GET,
    @SerializedName("torrent-set")
    TORRENT_SET,
}
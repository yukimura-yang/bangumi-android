package moe.gkd.bangumi.transmission.response.args

import com.google.gson.annotations.SerializedName

class GetSessionRespArgs(
    @SerializedName("session-id")
    val sessionId: String,
    val version: String
)
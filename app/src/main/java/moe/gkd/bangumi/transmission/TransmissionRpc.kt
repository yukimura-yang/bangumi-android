package moe.gkd.bangumi.transmission

import moe.gkd.bangumi.MainApplication
import moe.gkd.bangumi.TRANSMISSION_SAVE_DIR
import moe.gkd.bangumi.TRANSMISSION_SESSION_ID
import moe.gkd.bangumi.transmission.request.AddTorrentReqBody
import moe.gkd.bangumi.transmission.response.ResponseBody
import moe.gkd.bangumi.transmission.response.args.AddTorrentRespArgs
import moe.gkd.bangumi.transmission.response.args.GetSessionRespArgs
import retrofit2.HttpException

object TransmissionRpc {
    private val apiService by lazy { TransmissionFactory.instance.getService(TransmissionApiService::class.java) }
    suspend fun getSession(i: Int = 0): GetSessionRespArgs? {
        try {
            val resp = apiService.getSession()
            return resp.arguments
        } catch (e: HttpException) {
            if (i >= 3) {
                return null
            }
            val error = e.response()!!.errorBody()!!.string()
            val sessionId = error.substring(error.indexOf("<code>"), error.indexOf("</code>"))
                .split(":")[1].trim()
            MainApplication.INSTANCE.hashMap[TRANSMISSION_SESSION_ID] = sessionId
            return getSession(i + 1)
        }
    }

    /**
     * 添加下载
     */
    suspend fun addTorrentMagnet(magnet: String, name: String): ResponseBody<AddTorrentRespArgs> {
        val path = MainApplication.INSTANCE.hashMap[TRANSMISSION_SAVE_DIR].toString().let {
            if (it.endsWith("/")) {
                it
            } else {
                "$it/"
            }
        }
        return apiService.addTorrent(
            AddTorrentReqBody(
                magnet = magnet,
                dir = "${path}${name}"
            )
        )
    }
}
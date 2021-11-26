package moe.gkd.bangumi.transmission

import moe.gkd.bangumi.transmission.request.AddTorrentReqBody
import moe.gkd.bangumi.transmission.request.AddTrackerReqBody
import moe.gkd.bangumi.transmission.request.GetSessionBody
import moe.gkd.bangumi.transmission.request.GetTorrentsReqBody
import moe.gkd.bangumi.transmission.response.ResponseBody
import moe.gkd.bangumi.transmission.response.args.AddTorrentRespArgs
import moe.gkd.bangumi.transmission.response.args.AddTrackerRespArgs
import moe.gkd.bangumi.transmission.response.args.GetSessionRespArgs
import moe.gkd.bangumi.transmission.response.args.GetTorrentsRespArgs
import retrofit2.http.Body
import retrofit2.http.POST

interface TransmissionApiService {
    /**
     * 获取session
     */
    @POST("/transmission/rpc")
    suspend fun getSession(
        @Body body: GetSessionBody = GetSessionBody()
    ): ResponseBody<GetSessionRespArgs>

    /**
     * 添加下载
     */
    @POST("/transmission/rpc")
    suspend fun addTorrent(
        @Body body: AddTorrentReqBody
    ): ResponseBody<AddTorrentRespArgs>

    /**
     * 获取下载列表
     */
    @POST("/transmission/rpc")
    suspend fun getTorrents(
        @Body body: GetTorrentsReqBody = GetTorrentsReqBody()
    ): ResponseBody<GetTorrentsRespArgs>

    /**
     * 添加tracker
     */
    @POST("/transmission/rpc")
    suspend fun addTracker(
        @Body body: AddTrackerReqBody
    ): ResponseBody<AddTrackerRespArgs>
}
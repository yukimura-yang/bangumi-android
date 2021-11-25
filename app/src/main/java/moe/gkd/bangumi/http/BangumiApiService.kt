package moe.gkd.bangumi.http

import moe.gkd.bangumi.BANGUMI_MOE_HOST_URL
import moe.gkd.bangumi.data.entity.bangumi.BangumiFeed
import moe.gkd.bangumi.data.request.GetTorrentInfoRequest
import moe.gkd.bangumi.data.request.GetTorrentTagsRequest
import moe.gkd.bangumi.data.response.GetTorrentInfoResponse
import moe.gkd.bangumi.data.response.TorrentTag
import moe.gkd.bangumi.data.response.TorrentTeam
import moe.gkd.bangumi.http.ResponseFormat.Companion.XML
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BangumiApiService {
    @ResponseFormat(value = XML)
    @GET("${BANGUMI_MOE_HOST_URL}rss/tags/{tags}")
    suspend fun getRssSubscription(
        @Path("tags") tags: String
    ): BangumiFeed

    @POST("${BANGUMI_MOE_HOST_URL}api/torrent/fetch")
    suspend fun getTorrentInfo(
        @Body body: GetTorrentInfoRequest
    ): GetTorrentInfoResponse

    @POST("${BANGUMI_MOE_HOST_URL}api/team/fetch")
    suspend fun getTorrentTeam(
        @Body body: GetTorrentTagsRequest
    ): List<TorrentTeam>

    @POST("${BANGUMI_MOE_HOST_URL}api/tag/fetch")
    suspend fun getTorrentTags(
        @Body body: GetTorrentTagsRequest
    ): List<TorrentTag>
}
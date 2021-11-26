package moe.gkd.bangumi.http

import moe.gkd.bangumi.BANGUMI_MOE_HOST_URL
import moe.gkd.bangumi.data.entity.bangumi.BangumiFeed
import moe.gkd.bangumi.data.request.GetTorrentInfoRequest
import moe.gkd.bangumi.data.request.GetTorrentTagsRequest
import moe.gkd.bangumi.data.request.GetTorrentTeamRequest
import moe.gkd.bangumi.data.request.SearchTagsRequest
import moe.gkd.bangumi.data.response.GetTorrentInfoResponse
import moe.gkd.bangumi.data.response.SearchTagsResponse
import moe.gkd.bangumi.data.response.TorrentTag
import moe.gkd.bangumi.data.response.TorrentTeam
import moe.gkd.bangumi.http.ResponseFormat.Companion.XML
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BangumiApiService {
    /**
     * Feed
     */
    @ResponseFormat(value = XML)
    @GET("${BANGUMI_MOE_HOST_URL}rss/tags/{tags}")
    suspend fun getRssSubscription(
        @Path("tags") tags: String
    ): BangumiFeed

    /**
     * 获取种子详细
     */
    @POST("${BANGUMI_MOE_HOST_URL}api/torrent/fetch")
    suspend fun getTorrentInfo(
        @Body body: GetTorrentInfoRequest
    ): GetTorrentInfoResponse

    /**
     * 通过id查询用户
     */
    @POST("${BANGUMI_MOE_HOST_URL}api/team/fetch")
    suspend fun getTorrentTeam(
        @Body body: GetTorrentTeamRequest
    ): List<TorrentTeam>

    /**
     * 通过id查询标签
     */
    @POST("${BANGUMI_MOE_HOST_URL}api/tag/fetch")
    suspend fun getTorrentTags(
        @Body body: GetTorrentTagsRequest
    ): List<TorrentTag>

    /**
     * 推荐bangumi
     */
    @GET("${BANGUMI_MOE_HOST_URL}api/tag/popbangumi")
    suspend fun getPopbangumi(
    ): List<TorrentTag>

    /**
     * 推荐发布者
     */
    @GET("${BANGUMI_MOE_HOST_URL}api/tag/team")
    suspend fun getTeam(
    ): List<TorrentTag>

    /**
     * 常见的标签
     */
    @GET("${BANGUMI_MOE_HOST_URL}api/tag/common")
    suspend fun getCommon(
    ): List<TorrentTag>

    /**
     * 搜索标签
     */
    @POST("${BANGUMI_MOE_HOST_URL}api/tag/search")
    suspend fun searchTag(
        @Body body: SearchTagsRequest
    ): SearchTagsResponse
}
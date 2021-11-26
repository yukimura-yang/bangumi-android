package moe.gkd.bangumi.ui.bangumi

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import moe.gkd.bangumi.BANGUMI_MOE_HOST_URL
import moe.gkd.bangumi.data.AppDatabase
import moe.gkd.bangumi.data.entity.LoadStateEntity
import moe.gkd.bangumi.data.entity.TorrentEntity
import moe.gkd.bangumi.data.request.GetTorrentInfoRequest
import moe.gkd.bangumi.data.request.GetTorrentTagsRequest
import moe.gkd.bangumi.data.request.GetTorrentTeamRequest
import moe.gkd.bangumi.data.response.GetTorrentInfoResponse
import moe.gkd.bangumi.data.response.TorrentTeam
import moe.gkd.bangumi.http.BangumiApiService
import moe.gkd.bangumi.http.RetrofitFactory
import moe.gkd.bangumi.transmission.TransmissionRpc
import moe.gkd.bangumi.ui.BaseViewModel
import retrofit2.HttpException
import java.util.*

class BangumiViewModel(private val id: String) : BaseViewModel() {
    private val TAG = BangumiViewModel::class.simpleName
    private val bangumiApi = RetrofitFactory.instance.getService(BangumiApiService::class.java)
    val loadState = MutableLiveData<LoadStateEntity>()
    val bangumi = AppDatabase.getInstance().bangumiDao().getBangumiById(id)
    val toast = MutableLiveData<String>()

    fun addTorrentMagnet(torrent: TorrentEntity) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val resp = TransmissionRpc.addTorrentMagnet(
                        torrent.magnet,
                        bangumi.value!!.subscription.title
                    )
                    val newTorrent = torrent.copy(downloaded = true)
                    AppDatabase.getInstance().bangumiDao().update(newTorrent)
                    if (resp.arguments.torrentAdded != null) {
                        TransmissionRpc.addTracker(resp.arguments.torrentAdded.id)
                    } else if(resp.arguments.torrentDuplicate != null) {
                        TransmissionRpc.addTracker(resp.arguments.torrentDuplicate.id)
                    }
                    return@withContext
                }
            } catch (e: HttpException) {
                e.printStackTrace()
                if (e.code() == 409) {
                    try {
                        TransmissionRpc.getSession()
                        addTorrentMagnet(torrent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 取消订阅
     */
    fun unSubscribe() {
        updateSubscribeJob?.cancel()
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    AppDatabase.getInstance().bangumiDao().delete(bangumi.value!!.subscription)
                    AppDatabase.getInstance().bangumiDao().deleteTorrent(bangumi.value!!.torrents)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                toast.postValue("删除失败")
            }
        }
    }

    private var updateSubscribeJob: Job? = null

    /**
     * 更新订阅列表
     */
    fun updateSubscribe() {
        updateSubscribeJob = viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    loadState.postValue(LoadStateEntity(true, false))
                    val bangumi = this@BangumiViewModel.bangumi.value ?: return@withContext
                    val feed =
                        bangumi.subscription.feed.replace(
                            "${BANGUMI_MOE_HOST_URL}rss/tags/",
                            ""
                        )
                    val feedList = bangumiApi.getRssSubscription(feed).channel.items
                    val torrents = arrayListOf<TorrentEntity>()
                    //数据库里不存在的新torrent
                    val newTorrents = arrayListOf<GetTorrentInfoResponse>()
                    //标签id集合
                    val tagids = hashSetOf<String>()
                    //发布者id集合
                    val teamids = hashSetOf<String>()
                    for (item in feedList) {
                        val torrentId = item.link.replace(
                            "${BANGUMI_MOE_HOST_URL}torrent/",
                            ""
                        )
                        val oldTorrent = bangumi.torrents.find { it.id == torrentId }
                        if (oldTorrent != null) {
                            torrents.add(oldTorrent)
                            continue
                        }
                        val torrent = bangumiApi.getTorrentInfo(GetTorrentInfoRequest(torrentId))
                        newTorrents.add(torrent)
                        tagids.addAll(torrent.tagIds)
                        teamids.add(torrent.teamId)
                    }
                    val tags = if (tagids.size == 0) {
                        arrayListOf()
                    } else {
                        bangumiApi.getTorrentTags(GetTorrentTagsRequest(tagids.toList()))
                    }
                    val teams = if (teamids.size == 0) {
                        arrayListOf()
                    } else {
                        bangumiApi.getTorrentTeam(GetTorrentTeamRequest(teamids.toList()))
                    }
                    for (torrent in newTorrents) {
                        val currentTags = arrayListOf<String>()
                        var currentTeam: TorrentTeam? = null
                        for (tagId in torrent.tagIds) {
                            for (tag in tags) {
                                if (tag.id == tagId) {
                                    currentTags.add(tag.getRecommendName())
                                    continue
                                }
                            }
                            for (team in teams) {
                                if (torrent.teamId == team.id) {
                                    currentTeam = team
                                    continue
                                }
                            }
                        }
                        val torrentEntity = TorrentEntity(
                            uid = UUID.randomUUID().toString(),
                            parentId = id,
                            title = torrent.title,
                            size = torrent.size,
                            publishTime = torrent.publishTime,
                            team = currentTeam?.name ?: "NULL",
                            teamIcon = currentTeam?.icon ?: "",
                            tags = currentTags,
                            magnet = torrent.magnet,
                            id = torrent.id,
                        )
                        torrents.add(torrentEntity)
                    }
                    AppDatabase.getInstance().bangumiDao().insertAllTorrents(torrents)
                    loadState.postValue(LoadStateEntity(false, true))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                loadState.postValue(LoadStateEntity(false, false))
            }
        }
    }
}
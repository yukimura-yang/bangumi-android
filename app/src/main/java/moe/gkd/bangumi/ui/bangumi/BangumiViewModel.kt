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
import moe.gkd.bangumi.data.response.TorrentTag
import moe.gkd.bangumi.data.response.TorrentTeam
import moe.gkd.bangumi.http.BangumiApiService
import moe.gkd.bangumi.http.RetrofitFactory
import moe.gkd.bangumi.transmission.TransmissionRpc
import moe.gkd.bangumi.ui.BaseViewModel
import moe.gkd.bangumi.utc2Timestamp
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
                    val torrentId = if (resp.arguments.torrentAdded != null) {
                        resp.arguments.torrentAdded.id
                    } else if (resp.arguments.torrentDuplicate != null) {
                        resp.arguments.torrentDuplicate.id
                    } else {
                        null
                    }
                    val newTorrent = torrent.copy(transmissionId = torrentId)
                    AppDatabase.getInstance().bangumiDao().update(newTorrent)
                    if (torrentId != null) {
                        TransmissionRpc.addTracker(torrentId)
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

    /**
     * 单个刷新
     */
    fun updateTorrent(torrent: TorrentEntity) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val resp = bangumiApi.getTorrentInfo(GetTorrentInfoRequest(torrent.id))
                    val tags: List<TorrentTag> =
                        bangumiApi.getTorrentTags(GetTorrentTagsRequest(resp.tagIds))
                    val team: TorrentTeam = resp.teamId.let {
                        it ?: return@let TorrentTeam(UUID.randomUUID().toString(), "NULL", "", "")
                        bangumiApi.getTorrentTeam(
                            GetTorrentTeamRequest(
                                arrayListOf(it)
                            )
                        ).firstOrNull() ?: TorrentTeam(UUID.randomUUID().toString(), "NULL", "", "")
                    }
                    val newTorrent = TorrentEntity(
                        uid = torrent.uid,
                        parentId = torrent.parentId,
                        title = resp.title,
                        size = resp.size,
                        publishTimestamp = utc2Timestamp(resp.publishTime),
                        tags = tags,
                        team = team,
                        magnet = resp.magnet,
                        id = resp.id,
                        transmissionId = torrent.transmissionId
                    )
                    AppDatabase.getInstance().bangumiDao().update(newTorrent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private var updateSubscribeJob: Job? = null

    /**
     * 更新订阅列表
     */
    fun updateSubscribe() {
        updateSubscribeJob?.cancel()
        updateSubscribeJob = viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    loadState.postValue(LoadStateEntity(true, false))
                    val bangumi = this@BangumiViewModel.bangumi.value ?: return@withContext
                    val feed =
                        bangumi.subscription.getFeedTags()
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
                        if (torrent.teamId != null) {
                            teamids.add(torrent.teamId)
                        }
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
                        val currentTags = arrayListOf<TorrentTag>()
                        var currentTeam: TorrentTeam? = null
                        for (tagId in torrent.tagIds) {
                            for (tag in tags) {
                                if (tag.id == tagId) {
                                    currentTags.add(tag)
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
                        if (currentTeam == null) {
                            currentTeam = TorrentTeam(
                                UUID.randomUUID().toString(),
                                "NULL",
                                "",
                                ""
                            )
                        }
                        val torrentEntity = TorrentEntity(
                            uid = UUID.randomUUID().toString(),
                            parentId = id,
                            title = torrent.title,
                            size = torrent.size,
                            publishTimestamp = utc2Timestamp(torrent.publishTime),
                            team = currentTeam,
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

    /**
     * 强制刷新
     */
    fun updateSubscribeForced() {
        updateSubscribeJob?.cancel()
        updateSubscribeJob = viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    loadState.postValue(LoadStateEntity(true, false))
                    val bangumi = this@BangumiViewModel.bangumi.value ?: return@withContext
                    val feed =
                        bangumi.subscription.getFeedTags()
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
                        val torrent = bangumiApi.getTorrentInfo(GetTorrentInfoRequest(torrentId))
                        newTorrents.add(torrent)
                        tagids.addAll(torrent.tagIds)
                        if (torrent.teamId != null) {
                            teamids.add(torrent.teamId)
                        }
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
                        val currentTags = arrayListOf<TorrentTag>()
                        var currentTeam: TorrentTeam? = null
                        for (tagId in torrent.tagIds) {
                            for (tag in tags) {
                                if (tag.id == tagId) {
                                    currentTags.add(tag)
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
                        if (currentTeam == null) {
                            currentTeam = TorrentTeam(
                                UUID.randomUUID().toString(),
                                "NULL",
                                "",
                                ""
                            )
                        }
                        val oldTorrent = bangumi.torrents.find { it.id == torrent.id }
                        val torrentEntity = TorrentEntity(
                            uid = oldTorrent?.uid ?: UUID.randomUUID().toString(),
                            parentId = id,
                            title = torrent.title,
                            size = torrent.size,
                            publishTimestamp = utc2Timestamp(torrent.publishTime),
                            team = currentTeam,
                            tags = currentTags,
                            magnet = torrent.magnet,
                            id = torrent.id,
                            transmissionId = oldTorrent?.transmissionId
                        )
                        torrents.add(torrentEntity)
                    }
                    AppDatabase.getInstance().bangumiDao().clearTorrent(bangumi.subscription.id)
                    AppDatabase.getInstance().bangumiDao().insertAllTorrents(torrents)
                    loadState.postValue(LoadStateEntity(false, true))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                loadState.postValue(LoadStateEntity(false, false))
            }
        }
    }

    fun downloadAll() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val torrents = bangumi.value?.torrents ?: return@withContext
                    for (torrent in torrents) {
                        val resp = TransmissionRpc.addTorrentMagnet(
                            torrent.magnet,
                            bangumi.value!!.subscription.title
                        )
                        val torrentId = if (resp.arguments.torrentAdded != null) {
                            resp.arguments.torrentAdded.id
                        } else if (resp.arguments.torrentDuplicate != null) {
                            resp.arguments.torrentDuplicate.id
                        } else {
                            null
                        }
                        val newTorrent = torrent.copy(transmissionId = torrentId)
                        AppDatabase.getInstance().bangumiDao().update(newTorrent)
                        if (torrentId != null) {
                            TransmissionRpc.addTracker(torrentId)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun delete(torrent: TorrentEntity) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    AppDatabase.getInstance().bangumiDao().delete(torrent)
                }
            } catch (e: Exception) {
            }
        }
    }
}
package moe.gkd.bangumi.ui.bangumi

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.gkd.bangumi.BANGUMI_MOE_HOST_URL
import moe.gkd.bangumi.BANGUMI_MOE_TAG_COLLECTION
import moe.gkd.bangumi.data.AppDatabase
import moe.gkd.bangumi.data.entity.LoadStateEntity
import moe.gkd.bangumi.data.entity.TorrentEntity
import moe.gkd.bangumi.data.request.GetTorrentInfoRequest
import moe.gkd.bangumi.data.request.GetTorrentTagsRequest
import moe.gkd.bangumi.data.response.TorrentTeam
import moe.gkd.bangumi.http.BangumiApiService
import moe.gkd.bangumi.http.RetrofitFactory
import moe.gkd.bangumi.transmission.TransmissionRpc
import moe.gkd.bangumi.ui.BaseViewModel
import retrofit2.HttpException
import java.util.*
import kotlin.collections.HashMap

class BangumiViewModel(private val id: String) : BaseViewModel() {
    private val TAG = BangumiViewModel::class.simpleName
    private val bangumiApi = RetrofitFactory.instance.getService(BangumiApiService::class.java)
    val cache = HashMap<String, Any>()
    val loadState = MutableLiveData<LoadStateEntity>()
    val bangumi = AppDatabase.getInstance().bangumiDao().getBangumiById(id)


    fun addTorrentMagnet(torrent: TorrentEntity) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    TransmissionRpc.addTorrentMagnet(
                        torrent.magnet,
                        bangumi.value!!.subscription.title
                    )
                    val newTorrent = torrent.copy(downloaded = true)
                    AppDatabase.getInstance().bangumiDao().update(newTorrent)
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
     * 更新订阅列表
     */
    fun updateSubscribe() {
        viewModelScope.launch {
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
                    for (item in feedList) {
                        val torrentId = item.link.replace(
                            "${BANGUMI_MOE_HOST_URL}torrent/",
                            ""
                        )
                        val torrent = bangumiApi.getTorrentInfo(GetTorrentInfoRequest(torrentId))
                        if (torrent.tagIds.indexOf(BANGUMI_MOE_TAG_COLLECTION) != -1) {
                            //如果存在完结tag，则跳过
                            continue
                        }
                        val isRequestTags = torrent.tagIds.let { tagids ->
                            //如果有缓存中不存在的TAG则请求接口
                            for (tag in tagids) {
                                if (!cache.containsKey(tag))
                                    return@let true
                            }
                            false
                        }
                        val tags: List<String>
                        //缓存中是否有标签
                        if (isRequestTags) {
                            tags = bangumiApi.getTorrentTags(GetTorrentTagsRequest(torrent.tagIds))
                                .let {
                                    val list = arrayListOf<String>()
                                    for (tag in it) {
                                        if (tag.locale.zh_tw != null) {
                                            list.add(tag.locale.zh_tw)
                                            cache[tag.id] = tag.locale.zh_tw
                                        } else if (tag.locale.zh_cn != null) {
                                            list.add(tag.locale.zh_cn)
                                            cache[tag.id] = tag.locale.zh_cn
                                        } else {
                                            list.add(tag.name)
                                            cache[tag.id] = tag.name
                                        }
                                    }
                                    list
                                }
                        } else {
                            tags = torrent.tagIds.let { tagids ->
                                val list = arrayListOf<String>()
                                for (tag in tagids) {
                                    list.add(cache[tag].toString())
                                }
                                list
                            }
                        }
                        val team: TorrentTeam?
                        //缓存中是否有作者
                        if (cache.containsKey(torrent.teamId)) {
                            team = cache[torrent.teamId] as TorrentTeam
                        } else {
                            team =
                                bangumiApi.getTorrentTeam(GetTorrentTagsRequest(arrayListOf(torrent.teamId)))
                                    .firstOrNull()
                            if (team != null) {
                                cache[torrent.teamId] = team
                            }
                        }
                        val oldTorrent = bangumi.torrents.find { it.id == torrent.id }
                        torrents.add(
                            TorrentEntity(
                                uid = oldTorrent?.uid ?: UUID.randomUUID().toString(),
                                parentId = id,
                                title = torrent.title,
                                size = torrent.size,
                                publishTime = torrent.publishTime,
                                team = team?.name ?: "NULL",
                                teamIcon = team?.icon ?: "",
                                tags = tags,
                                magnet = torrent.magnet,
                                id = torrent.id,
                                downloaded = oldTorrent?.downloaded ?: false
                            )
                        )
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
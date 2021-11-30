package moe.gkd.bangumi.ui.addsubscription

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import moe.gkd.bangumi.BANGUMI_MOE_HOST_URL
import moe.gkd.bangumi.data.AppDatabase
import moe.gkd.bangumi.data.entity.LoadStateEntity
import moe.gkd.bangumi.data.entity.SubscriptionEntity
import moe.gkd.bangumi.data.entity.TorrentEntity
import moe.gkd.bangumi.data.request.SearchTagsRequest
import moe.gkd.bangumi.data.response.TorrentTag
import moe.gkd.bangumi.data.response.TorrentTeam
import moe.gkd.bangumi.gtm2Timestamp
import moe.gkd.bangumi.http.BangumiApiService
import moe.gkd.bangumi.http.RetrofitFactory
import moe.gkd.bangumi.ui.BaseViewModel
import java.util.*
import kotlin.collections.LinkedHashSet

class AddSubscriptionViewModel : BaseViewModel() {
    private val TAG = AddSubscriptionViewModel::class.simpleName
    private val bangumiApi = RetrofitFactory.instance.getService(BangumiApiService::class.java)
    val loadState = MutableLiveData<LoadStateEntity>()

    val recommendTags = MutableLiveData<List<TorrentTag>>()
    val searchTags = MutableLiveData<List<TorrentTag>>()
    val selectedTags = MutableLiveData<LinkedHashSet<TorrentTag>>(LinkedHashSet())
    val toast = MutableLiveData<String>()

    //正在搜索tag
    val searching = MutableLiveData<Boolean>()

    //推荐tag正在加载
    val loading = MutableLiveData<Boolean>()

    val bangumi = MutableLiveData<List<TorrentEntity>>()

    init {
        loadRecommendTags()
    }

    fun search(query: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    searching.postValue(true)
                    searchTags.postValue(arrayListOf())
                    val list = arrayListOf<TorrentTag>()
                    val resp = bangumiApi.searchTag(SearchTagsRequest(name = query))
                    if (resp.success && resp.found) {
                        list.addAll(resp.tag)
                    }
                    searchTags.postValue(list)
                    searching.postValue(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                searching.postValue(false)
            }
        }
    }

    fun loadRecommendTags() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    loading.postValue(true)
                    val pop = bangumiApi.getPopbangumi()
                    val team = bangumiApi.getTeam()
                    val comment = bangumiApi.getCommon()
                    val list = arrayListOf<TorrentTag>()
                    list.addAll(pop)
                    list.addAll(team)
                    list.addAll(comment)
                    recommendTags.postValue(list)
                    loading.postValue(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                loading.postValue(false)
            }
        }
    }

    private var searchTorrentJob: Job? = null

    fun searchTorrent() {
        searchTorrentJob?.cancel()
        searchTorrentJob = viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    bangumi.postValue(arrayListOf())
                    if (selectedTags.value == null || selectedTags.value!!.size == 0) {
                        loadState.postValue(LoadStateEntity(false, false, "没有选择标签"))
                        return@withContext
                    }
                    loadState.postValue(LoadStateEntity(true, false, "正在搜索"))
                    val tagids = selectedTags.value?.let {
                        val sb = StringBuilder()
                        it.forEachIndexed { index, tag ->
                            sb.append(tag.id)
                            if (index < it.size - 1) {
                                sb.append("+")
                            }
                        }
                        sb.toString()
                    } ?: ""
                    val feedList = bangumiApi.getRssSubscription(tagids).channel.items
                    val bangumi = arrayListOf<TorrentEntity>()
                    for (item in feedList) {
                        val torrent = TorrentEntity(
                            uid = UUID.randomUUID().toString(),
                            parentId = "",
                            title = item.title,
                            size = "",
                            publishTimestamp = gtm2Timestamp(item.pubDate),
                            tags = arrayListOf(),
                            team = TorrentTeam(UUID.randomUUID().toString(), "NULL", "", ""),
                            magnet = "",
                            id = item.link.replace(
                                "${BANGUMI_MOE_HOST_URL}torrent/",
                                ""
                            )
                        )
                        bangumi.add(torrent)
                    }
                    this@AddSubscriptionViewModel.bangumi.postValue(bangumi)
                    loadState.postValue(LoadStateEntity(false, true, "搜索完成"))
                }
            } catch (e: Exception) {
                loadState.postValue(LoadStateEntity(false, false, "搜索异常"))
                e.printStackTrace()
            }
        }
    }

    /**
     * 订阅
     */
    fun subscribe(name: String) {
        viewModelScope.launch {
            try {
                val tagids = selectedTags.value?.let {
                    val sb = StringBuilder()
                    it.forEachIndexed { index, tag ->
                        sb.append(tag.id)
                        if (index < it.size - 1) {
                            sb.append("+")
                        }
                    }
                    sb.toString()
                } ?: ""
                val lastUpdateTime = bangumi.value?.let {
                    if (it.isEmpty()) return@let 0
                    it.first().publishTimestamp ?: 0
                } ?: 0
                val subscription = SubscriptionEntity(
                    UUID.randomUUID().toString(),
                    name,
                    "${BANGUMI_MOE_HOST_URL}rss/tags/${tagids}",
                    lastUpdateTime = lastUpdateTime
                )
                withContext(Dispatchers.IO) {
                    AppDatabase.getInstance().bangumiDao().insertAll(subscription)
                    toast.postValue("订阅成功")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                toast.postValue("订阅失败")
            }
        }
    }
}
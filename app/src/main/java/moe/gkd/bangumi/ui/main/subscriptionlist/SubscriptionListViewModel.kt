package moe.gkd.bangumi.ui.main.subscriptionlist

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.gkd.bangumi.data.AppDatabase
import moe.gkd.bangumi.http.BangumiApiService
import moe.gkd.bangumi.http.RetrofitFactory
import moe.gkd.bangumi.ui.BaseViewModel

class SubscriptionListViewModel : BaseViewModel() {
    private val TAG = SubscriptionListViewModel::class.simpleName
    private val bangumiApi = RetrofitFactory.instance.getService(BangumiApiService::class.java)
    val bangumis = AppDatabase.getInstance().bangumiDao().getBangumis()

    /**
     * 检查订阅是否有更新
     */
    fun checkSubscription() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    bangumis.value ?: return@withContext
                    for (bangumi in bangumis.value!!) {
                        val feed =
                            bangumiApi.getRssSubscription(bangumi.subscription.getFeedTags()).channel.items
                        if (feed.size != bangumi.subscription.feedSize) {
                            val subscription = bangumi.subscription.copy(feedSize = feed.size)
                            AppDatabase.getInstance().bangumiDao().update(subscription)
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
    }
}
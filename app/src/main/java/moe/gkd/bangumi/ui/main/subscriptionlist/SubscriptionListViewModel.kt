package moe.gkd.bangumi.ui.main.subscriptionlist

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.gkd.bangumi.BANGUMI_MOE_HOST_URL
import moe.gkd.bangumi.data.AppDatabase
import moe.gkd.bangumi.data.entity.SubscriptionEntity
import moe.gkd.bangumi.ui.BaseViewModel
import java.util.*

class SubscriptionListViewModel : BaseViewModel() {
    private val TAG = SubscriptionListViewModel::class.simpleName
    val bangumis = AppDatabase.getInstance().bangumiDao().getBangumis()

    fun initBangumis() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                AppDatabase.getInstance().bangumiDao().insertAll(
                    SubscriptionEntity(
                        UUID.randomUUID().toString(),
                        "真の仲間",
                        "${BANGUMI_MOE_HOST_URL}rss/tags/615bb923d7f73dd4ed5c441d+581be821ee98e9ca20730eae"
                    ),
                    SubscriptionEntity(
                        UUID.randomUUID().toString(),
                        "无职转生",
                        "${BANGUMI_MOE_HOST_URL}rss/tags/5d8b3245306f1a0007bd7aca+615bb91fd7f73dd4ed5c4405"
                    ),
                    SubscriptionEntity(
                        UUID.randomUUID().toString(),
                        "阴阳眼见子",
                        "${BANGUMI_MOE_HOST_URL}rss/tags/615bb91ed7f73dd4ed5c43ff+548ee1204ab7379536f56357+548ee2ce4ab7379536f56358+5d8b3245306f1a0007bd7aca"
                    ),
                )
            }
        }
    }
//    fun getBangumis() {
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                val list = AppDatabase.getInstance().bangumiDao().getBangumis()
//                val newList = list.sortedByDescending {
//                    it.torrents.firstOrNull()?.getTimestamp() ?: Long.MAX_VALUE
//                }
//                bangumis.postValue(newList)
//            }
//        }
//    }
}
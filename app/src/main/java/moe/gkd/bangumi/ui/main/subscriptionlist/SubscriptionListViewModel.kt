package moe.gkd.bangumi.ui.main.subscriptionlist

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.gkd.bangumi.data.AppDatabase
import moe.gkd.bangumi.data.entity.BangumiEntity
import moe.gkd.bangumi.data.entity.BangumiListGroup
import moe.gkd.bangumi.data.entity.BangumiListInterface
import moe.gkd.bangumi.gtm2Timestamp
import moe.gkd.bangumi.http.BangumiApiService
import moe.gkd.bangumi.http.RetrofitFactory
import moe.gkd.bangumi.ui.BaseViewModel
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId

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
                        val subscription = bangumi.subscription.copy(
                            feedSize = feed.size,
                            lastUpdateTime = gtm2Timestamp(feed.firstOrNull()?.pubDate ?: "")
                        )
                        AppDatabase.getInstance().bangumiDao().update(subscription)
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    fun formatList(bangumis: List<BangumiEntity>): List<BangumiListInterface> {
        var monday: MutableList<BangumiListInterface> = arrayListOf()
        var tuesday: MutableList<BangumiListInterface> = arrayListOf()
        var wednesday: MutableList<BangumiListInterface> = arrayListOf()
        var thursday: MutableList<BangumiListInterface> = arrayListOf()
        var friday: MutableList<BangumiListInterface> = arrayListOf()
        var saturday: MutableList<BangumiListInterface> = arrayListOf()
        var sunday: MutableList<BangumiListInterface> = arrayListOf()
        for (bangumi in bangumis) {
            val local = Instant.ofEpochMilli(bangumi.subscription.lastUpdateTime)
                .atZone(ZoneId.systemDefault()).toLocalDate()
            when (local.dayOfWeek) {
                DayOfWeek.MONDAY -> monday.add(bangumi)
                DayOfWeek.TUESDAY -> tuesday.add(bangumi)
                DayOfWeek.WEDNESDAY -> wednesday.add(bangumi)
                DayOfWeek.THURSDAY -> thursday.add(bangumi)
                DayOfWeek.FRIDAY -> friday.add(bangumi)
                DayOfWeek.SATURDAY -> saturday.add(bangumi)
                DayOfWeek.SUNDAY -> sunday.add(bangumi)
            }
        }
        monday = monday.sortedBy {
            val subscription = (it as BangumiEntity).subscription
            val localTime =
                Instant.ofEpochMilli(subscription.lastUpdateTime).atZone(ZoneId.systemDefault())
                    .toLocalTime()
            localTime.toSecondOfDay()
        }.toMutableList()
        tuesday = tuesday.sortedBy {
            val subscription = (it as BangumiEntity).subscription
            val localTime =
                Instant.ofEpochMilli(subscription.lastUpdateTime).atZone(ZoneId.systemDefault())
                    .toLocalTime()
            localTime.toSecondOfDay()
        }.toMutableList()
        wednesday = wednesday.sortedBy {
            val subscription = (it as BangumiEntity).subscription
            val localTime =
                Instant.ofEpochMilli(subscription.lastUpdateTime).atZone(ZoneId.systemDefault())
                    .toLocalTime()
            localTime.toSecondOfDay()
        }.toMutableList()
        friday = friday.sortedBy {
            val subscription = (it as BangumiEntity).subscription
            val localTime =
                Instant.ofEpochMilli(subscription.lastUpdateTime).atZone(ZoneId.systemDefault())
                    .toLocalTime()
            localTime.toSecondOfDay()
        }.toMutableList()
        saturday = saturday.sortedBy {
            val subscription = (it as BangumiEntity).subscription
            val localTime =
                Instant.ofEpochMilli(subscription.lastUpdateTime).atZone(ZoneId.systemDefault())
                    .toLocalTime()
            localTime.toSecondOfDay()
        }.toMutableList()
        sunday = sunday.sortedBy {
            val subscription = (it as BangumiEntity).subscription
            val localTime =
                Instant.ofEpochMilli(subscription.lastUpdateTime).atZone(ZoneId.systemDefault())
                    .toLocalTime()
            localTime.toSecondOfDay()
        }.toMutableList()
        monday.sortedBy {
            val subscription = (it as BangumiEntity).subscription
            val localTime =
                Instant.ofEpochMilli(subscription.lastUpdateTime).atZone(ZoneId.systemDefault())
                    .toLocalTime()
            localTime.toSecondOfDay()
        }.toMutableList()
        if (monday.isNotEmpty()) monday.add(0, BangumiListGroup(DayOfWeek.MONDAY))
        if (tuesday.isNotEmpty()) tuesday.add(0, BangumiListGroup(DayOfWeek.TUESDAY))
        if (wednesday.isNotEmpty()) wednesday.add(0, BangumiListGroup(DayOfWeek.WEDNESDAY))
        if (thursday.isNotEmpty()) thursday.add(0, BangumiListGroup(DayOfWeek.THURSDAY))
        if (friday.isNotEmpty()) friday.add(0, BangumiListGroup(DayOfWeek.FRIDAY))
        if (saturday.isNotEmpty()) saturday.add(0, BangumiListGroup(DayOfWeek.SATURDAY))
        if (sunday.isNotEmpty()) sunday.add(0, BangumiListGroup(DayOfWeek.SUNDAY))

        val newList = monday + tuesday + wednesday + thursday + friday + saturday + sunday
        return newList
    }
}
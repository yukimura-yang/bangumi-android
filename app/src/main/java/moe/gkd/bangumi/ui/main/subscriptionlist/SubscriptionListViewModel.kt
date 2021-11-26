package moe.gkd.bangumi.ui.main.subscriptionlist

import moe.gkd.bangumi.data.AppDatabase
import moe.gkd.bangumi.ui.BaseViewModel

class SubscriptionListViewModel : BaseViewModel() {
    private val TAG = SubscriptionListViewModel::class.simpleName
    val bangumis = AppDatabase.getInstance().bangumiDao().getBangumis()

}
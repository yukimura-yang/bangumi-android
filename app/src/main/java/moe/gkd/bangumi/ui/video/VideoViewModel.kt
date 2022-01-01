package moe.gkd.bangumi.ui.video

import moe.gkd.bangumi.data.AppDatabase
import moe.gkd.bangumi.data.entity.PlayHistory
import moe.gkd.bangumi.ui.BaseViewModel

class VideoViewModel(private val path: String) : BaseViewModel() {
    val playHistory = AppDatabase.getInstance().playHistoryDao().get(path)
    var isEnd = false

    fun setPlayHistory(isCompleted: Boolean, duration: Long) {
        val history = PlayHistory(
            path,
            isCompleted,
            duration
        )
        AppDatabase.getInstance().playHistoryDao().update(history)
    }
}
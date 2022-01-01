package moe.gkd.bangumi.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playHistory")
class PlayHistory(
    //文件路径
    @PrimaryKey
    val path: String,
    //是否播放完成
    var isCompleted: Boolean = false,
    //播放进度
    var duration: Long = 0
)
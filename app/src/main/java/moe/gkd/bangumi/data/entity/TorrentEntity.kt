package moe.gkd.bangumi.data.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import moe.gkd.bangumi.data.response.TorrentTag
import moe.gkd.bangumi.data.response.TorrentTeam
import moe.gkd.bangumi.timestamp2Local
import moe.gkd.bangumi.utc2Timestamp

@Entity(tableName = "torrents")
data class TorrentEntity(
    @PrimaryKey
    val uid: String,
    //父id
    val parentId: String,
    //标题
    val title: String,
    //文件大小
    val size: String,
    //上传时间
    @Deprecated("使用 publishTimestamp 代替", level = DeprecationLevel.ERROR)
    val publishTime: String = "",
    //上传时间
    val publishTimestamp: Long = utc2Timestamp(publishTime),
    //标签
    val tags: List<TorrentTag>,
    //上传者
    val team: TorrentTeam,
    @Deprecated("removed", level = DeprecationLevel.ERROR)
    val teamIcon: String = "",
    //磁力链
    val magnet: String,
    //id
    val id: String,
    //下载ID
    var transmissionId: Long? = null,
    @Deprecated("removed", level = DeprecationLevel.ERROR)
    var downloaded: Boolean = false
) {
    fun getFormatTime(): String {
        return timestamp2Local(publishTimestamp)
    }

    override fun equals(other: Any?): Boolean {
        if (other is TorrentEntity) {
            return uid == other.uid && parentId == other.parentId && title == other.title && size == other.size && publishTimestamp == other.publishTimestamp && tags.size == other.tags.size &&
                    tags.containsAll(other.tags) && team == other.team && magnet == other.magnet && id == other.id && transmissionId == other.transmissionId
        } else {
            return false
        }
    }

    override fun hashCode(): Int {
        var result = 17
        result = result * 31 + uid.hashCode()
        result = result * 31 + transmissionId.hashCode()
        return result
    }
}
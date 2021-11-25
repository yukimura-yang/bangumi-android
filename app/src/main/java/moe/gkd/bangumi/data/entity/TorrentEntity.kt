package moe.gkd.bangumi.data.entity

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import moe.gkd.bangumi.utc2Local
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
    val publishTime: String,
    //标签
    val tags: List<String>,
    //上传者
    val team: String,
    //上传者头像
    val teamIcon: String,
    //磁力链
    val magnet: String,
    //id
    val id: String,
    //是否下载过
    var downloaded: Boolean = false
) {
    fun getFormatTime(): String {
        return utc2Local(publishTime)
    }

    fun getTimestamp(): Long {
        return utc2Timestamp(publishTime)
    }

    override fun equals(other: Any?): Boolean {
        if (other is TorrentEntity) {
            return uid == other.uid && parentId == other.parentId && title == other.title && size == other.size && publishTime == other.publishTime && tags.size == other.tags.size &&
                    tags.containsAll(other.tags) && team == other.team && other.teamIcon == other.teamIcon && magnet == other.magnet && id == other.id && downloaded == other.downloaded
        } else {
            return false
        }
    }

    override fun hashCode(): Int {
        var result = 17
        result = result * 31 + uid.hashCode()
        result = result * 31 + downloaded.hashCode()
        return result
    }
}
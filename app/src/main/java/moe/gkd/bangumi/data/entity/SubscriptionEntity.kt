package moe.gkd.bangumi.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import moe.gkd.bangumi.BANGUMI_MOE_HOST_URL

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val feed: String,
    var feedSize: Int = 0,
    var lastUpdateTime: Long,
) {
    fun getFeedTags(): String {
        return feed.replace(
            "${BANGUMI_MOE_HOST_URL}rss/tags/",
            ""
        )
    }
}
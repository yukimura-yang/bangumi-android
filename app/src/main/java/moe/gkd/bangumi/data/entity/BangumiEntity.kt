package moe.gkd.bangumi.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class BangumiEntity(
    @Embedded
    val subscription: SubscriptionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "parentId"
    )
    val torrents: List<TorrentEntity>
) : BangumiListInterface {
    fun hasUpdate(): Boolean {
        return subscription.feedSize > torrents.size
    }
}
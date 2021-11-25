package moe.gkd.bangumi.data.entity

import androidx.room.Embedded
import androidx.room.Fts4
import androidx.room.Relation

data class BangumiEntity(
    @Embedded
    val subscription: SubscriptionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "parentId"
    )
    val torrents: List<TorrentEntity>
)
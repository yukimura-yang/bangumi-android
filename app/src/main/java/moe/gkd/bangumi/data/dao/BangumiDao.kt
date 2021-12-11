package moe.gkd.bangumi.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import moe.gkd.bangumi.data.entity.BangumiEntity
import moe.gkd.bangumi.data.entity.SubscriptionEntity
import moe.gkd.bangumi.data.entity.TorrentEntity

@Dao
interface BangumiDao {
    @Transaction
    @Query("SELECT * FROM subscriptions")
    fun getBangumis(): LiveData<List<BangumiEntity>>

    @Transaction
    @Query("SELECT * FROM subscriptions")
    fun getBangumis2(): List<BangumiEntity>

    @Transaction
    @Query("SELECT * FROM subscriptions WHERE id = :id")
    fun getBangumiById(id: String): LiveData<BangumiEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg subscriptions: SubscriptionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllSubscriptions(subscriptions: List<SubscriptionEntity>)

    @Update
    fun update(subscription: SubscriptionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg torrents: TorrentEntity)

    @Update
    fun update(torrent: TorrentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTorrents(torrents: List<TorrentEntity>)


    @Delete
    fun delete(vararg subscription: SubscriptionEntity)

    @Delete
    fun deleteSubscription(subscriptions: List<SubscriptionEntity>)

    @Delete
    fun delete(vararg torrent: TorrentEntity)

    @Query("DELETE FROM torrents WHERE parentId = :id")
    fun clearTorrent(id: String)

    @Delete
    fun deleteTorrent(torrents: List<TorrentEntity>)
}
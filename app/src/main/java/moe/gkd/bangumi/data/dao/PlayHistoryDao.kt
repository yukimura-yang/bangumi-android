package moe.gkd.bangumi.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import moe.gkd.bangumi.data.entity.PlayHistory

@Dao
interface PlayHistoryDao {

    @Query("SELECT * FROM playHistory WHERE path=:path")
    fun get(path: String): LiveData<PlayHistory?>

    @Query("SELECT * FROM playHistory")
    fun getAll(): LiveData<List<PlayHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(playHistory: PlayHistory)
}
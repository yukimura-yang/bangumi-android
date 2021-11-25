package moe.gkd.bangumi.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import moe.gkd.bangumi.MainApplication
import moe.gkd.bangumi.data.dao.BangumiDao
import moe.gkd.bangumi.data.entity.SubscriptionEntity
import moe.gkd.bangumi.data.entity.TorrentEntity

@Database(
    entities = [SubscriptionEntity::class, TorrentEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private const val DB_NAME = "bangumi_database.db"
        private var INSTANCE: AppDatabase? = null
        private var lock = Any()

        fun getInstance(): AppDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        MainApplication.INSTANCE.applicationContext,
                        AppDatabase::class.java,
                        DB_NAME
                    ).build()
                }
                return INSTANCE!!
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    public abstract fun bangumiDao(): BangumiDao
}
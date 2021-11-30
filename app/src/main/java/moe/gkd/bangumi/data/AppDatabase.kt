package moe.gkd.bangumi.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import moe.gkd.bangumi.MainApplication
import moe.gkd.bangumi.data.dao.BangumiDao
import moe.gkd.bangumi.data.entity.SubscriptionEntity
import moe.gkd.bangumi.data.entity.TorrentEntity

@Database(
    entities = [SubscriptionEntity::class, TorrentEntity::class],
    version = 3,
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
                    )
                        .addMigrations(MIGRATION_1_2)
                        .addMigrations(MIGRATION_2_3)
                        .build()
                }
                return INSTANCE!!
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE torrents ADD COLUMN transmissionId INTEGER")
                database.execSQL("ALTER TABLE subscriptions ADD COLUMN feedSize INTEGER NOT NULL DEFAULT 0")
            }
        }
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE torrents ADD COLUMN publishTimestamp INTEGER NOT NULL")
                database.execSQL("ALTER TABLE subscriptions ADD COLUMN lastUpdateTime INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    public abstract fun bangumiDao(): BangumiDao
}
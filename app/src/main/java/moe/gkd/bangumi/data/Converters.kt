package moe.gkd.bangumi.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import moe.gkd.bangumi.data.response.TorrentTag
import moe.gkd.bangumi.data.response.TorrentTeam
import java.util.*


class Converters {
    private val gson = Gson()

    @TypeConverter
    fun string2TagArray(value: String): List<TorrentTag> {
        return try {
            val type = object : TypeToken<List<TorrentTag>>() {}.type
            gson.fromJson(value, type)
        } catch (e: Exception) {
            arrayListOf()
        }
    }

    @TypeConverter
    fun tagArray2String(value: List<TorrentTag>): String {
        return try {
            gson.toJson(value)
        } catch (e: Exception) {
            "{}"
        }
    }

    @TypeConverter
    fun team2String(value: TorrentTeam): String {
        return try {
            gson.toJson(value)
        } catch (e: Exception) {
            "{}"
        }
    }

    @TypeConverter
    fun string2Team(value: String): TorrentTeam {
        return try {
            gson.fromJson(value, TorrentTeam::class.java)
        } catch (e: Exception) {
            TorrentTeam(UUID.randomUUID().toString(), "NULL", "", "")
        }
    }
}
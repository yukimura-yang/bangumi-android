package moe.gkd.bangumi.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {
    private val gson = Gson()

    @TypeConverter
    fun string2Array(value: String): List<String> {
        return try {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(value, type)
        } catch (e: Exception) {
            arrayListOf()
        }
    }

    @TypeConverter
    fun array2String(value: List<String>): String {
        return try {
            gson.toJson(value)
        } catch (e: Exception) {
            "{}"
        }
    }
}
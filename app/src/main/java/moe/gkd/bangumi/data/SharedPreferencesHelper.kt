package moe.gkd.bangumi.data

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import moe.gkd.bangumi.MainApplication
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object SharedPreferencesHelper {
    object SP : Delegates() {
        override fun getSharedPreferencesName(): String = this::class.java.simpleName
        var bangumiCDN by boolean(false)
        var transmissionHost by string("")
        var transmissionPort by string("")
        var transmissionSSL by boolean(false)
        var transmissionUsername by string("")
        var transmissionPassword by string("")
        var transmissionRpc by string("/transmission/rpc")
        var transmissionSaveDir by string("/volume2/public/Downloads")
        var webdavAddress by string("")
        var webdavUserName by string("")
        var webdavPassword by string("")
    }

    abstract class Delegates {
        private val preferences: SharedPreferences by lazy {
            val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
            val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
            val sharedPreferences = EncryptedSharedPreferences.create(
                getSharedPreferencesName(),
                masterKeyAlias,
                MainApplication.INSTANCE.applicationContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            sharedPreferences
        }

        fun boolean(defaultValue: Boolean = false) = object : ReadWriteProperty<Any, Boolean> {
            override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
                preferences.edit().putBoolean(property.name, value).apply()
            }

            override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
                return preferences.getBoolean(property.name, defaultValue)
            }

        }

        fun string(defaultValue: String? = null) = object : ReadWriteProperty<Any, String?> {
            override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
                preferences.edit().putString(property.name, value).apply()
            }

            override fun getValue(thisRef: Any, property: KProperty<*>): String? {
                return preferences.getString(property.name, defaultValue)
            }
        }

        fun int(defaultValue: Int = 0) = object : ReadWriteProperty<Any, Int> {
            override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
                preferences.edit().putInt(property.name, value).apply()
            }

            override fun getValue(thisRef: Any, property: KProperty<*>): Int {
                return preferences.getInt(property.name, defaultValue)
            }
        }

        abstract fun getSharedPreferencesName(): String

    }
}
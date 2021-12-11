package moe.gkd.bangumi

import android.app.Application
import com.facebook.stetho.Stetho
import moe.gkd.bangumi.data.AppDatabase
import moe.gkd.bangumi.data.SharedPreferencesHelper

class MainApplication : Application() {
    companion object {
        public lateinit var INSTANCE: MainApplication
    }

    public val hashMap = HashMap<String, Any>()

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        initHashMap()
        Stetho.initializeWithDefaults(this)
        AppDatabase.getInstance()
    }

    /**
     * 初始化变量
     */
    private fun initHashMap() {
        hashMap[USE_BANGUMI_MOE_CDN] = SharedPreferencesHelper.SP.bangumiCDN
        hashMap[TRANSMISSION_HOST] = SharedPreferencesHelper.SP.transmissionHost!!
        hashMap[TRANSMISSION_PORT] = SharedPreferencesHelper.SP.transmissionPort!!
        hashMap[TRANSMISSION_SSL] = SharedPreferencesHelper.SP.transmissionSSL
        hashMap[TRANSMISSION_USERNAME] = SharedPreferencesHelper.SP.transmissionUsername!!
        hashMap[TRANSMISSION_PASSWORD] = SharedPreferencesHelper.SP.transmissionPassword!!
        hashMap[TRANSMISSION_RPC] = SharedPreferencesHelper.SP.transmissionRpc!!
        hashMap[TRANSMISSION_SAVE_DIR] = SharedPreferencesHelper.SP.transmissionSaveDir!!
        hashMap[WEBDAV_ADDRESS] = SharedPreferencesHelper.SP.webdavAddress!!
        hashMap[WEBDAV_USERNAME] = SharedPreferencesHelper.SP.webdavUserName!!
        hashMap[WEBDAV_PASSWORD] = SharedPreferencesHelper.SP.webdavPassword!!
    }
}
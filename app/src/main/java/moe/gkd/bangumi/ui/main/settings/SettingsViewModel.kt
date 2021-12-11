package moe.gkd.bangumi.ui.main.settings

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.gkd.bangumi.transmission.TransmissionRpc
import moe.gkd.bangumi.ui.BaseViewModel

class SettingsViewModel : BaseViewModel() {

    val checking = MutableLiveData(false)
    val toast = MutableLiveData<String>()

    fun checkTransmission() {
        viewModelScope.launch {
            try {
                checking.postValue(true)
                val session = withContext(Dispatchers.IO) {
                    val session = TransmissionRpc.getSession()
                    session
                }
                if (session != null) {
                    Log.i(TAG, "checkTransmission: 成功 ${session.sessionId}")
                    toast.postValue("连接成功")
                } else {
                    Log.i(TAG, "checkTransmission: 失败")
                    toast.postValue("连接失败")
                }
                checking.postValue(false)
            } catch (e: Exception) {
                checking.postValue(false)
                toast.postValue("连接异常")
                e.printStackTrace()
            }
        }
    }
}
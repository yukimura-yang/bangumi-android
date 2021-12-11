package moe.gkd.bangumi.ui.main.webdav

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.thegrizzlylabs.sardineandroid.DavResource
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.gkd.bangumi.MainApplication
import moe.gkd.bangumi.WEBDAV_ADDRESS
import moe.gkd.bangumi.WEBDAV_PASSWORD
import moe.gkd.bangumi.WEBDAV_USERNAME
import moe.gkd.bangumi.data.entity.LoadStateEntity
import moe.gkd.bangumi.ui.BaseViewModel

class WebdavViewModel : BaseViewModel() {
    val files = MutableLiveData<List<DavResource>>()

    private val sardine = OkHttpSardine()

    val loadState = MutableLiveData<LoadStateEntity>()

    var lastPath = arrayListOf<String>()

    init {
        initClient()
    }

    var loadingJob: Job? = null

    fun loadFiles(path: String) {
        Log.d(TAG, "loadFiles: ${path}")
        if (loadingJob?.isActive == true) return
        loadingJob = viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val resources = sardine.list("${getAddress()}${path}")
                    lastPath.add(files.value?.firstOrNull()?.path ?: "/")
                    files.postValue(resources)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getAddress(): String {
        return MainApplication.INSTANCE.hashMap[WEBDAV_ADDRESS].toString().let {
            if (it.endsWith("/")) {
                it.substring(0, it.length - 1)
            } else {
                it
            }
        }
    }

    private fun getUserName(): String {
        return MainApplication.INSTANCE.hashMap[WEBDAV_USERNAME].toString()
    }

    private fun getPassword(): String {
        return MainApplication.INSTANCE.hashMap[WEBDAV_PASSWORD].toString()
    }


    private fun initClient() {
        sardine.setCredentials(getUserName(), getPassword())
        loadFiles("/")
    }
}
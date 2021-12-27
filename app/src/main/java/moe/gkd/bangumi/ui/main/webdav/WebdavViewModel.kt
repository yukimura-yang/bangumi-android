package moe.gkd.bangumi.ui.main.webdav

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.common.hash.Hashing
import com.thegrizzlylabs.sardineandroid.DavResource
import kotlinx.coroutines.*
import moe.gkd.bangumi.copyTo
import moe.gkd.bangumi.data.entity.LoadStateEntity
import moe.gkd.bangumi.ui.BaseViewModel
import moe.gkd.bangumi.ui.utils.WebDavUtils
import moe.gkd.bangumi.ui.utils.WebDavUtils.getAddress
import java.io.File
import java.net.SocketTimeoutException
import java.nio.charset.Charset

class WebdavViewModel : BaseViewModel() {
    val files = MutableLiveData<List<DavResource>>()

    val loadState = MutableLiveData<LoadStateEntity>()

    var lastPath = arrayListOf<String>()

    init {
        initClient()
    }

    var loadingJob: Job? = null

    fun loadFiles(path: String) {
        Log.d(TAG, "loadFiles: ${path}")
        if (loadingJob?.isActive == true) {
            loadingJob?.cancel()
        }
        loadingJob = viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val resources = try {
                        WebDavUtils.getSardine().list("${getAddress()}${path}")
                    } catch (e: SocketTimeoutException) {
                        Log.e(TAG, "loadFiles: 超时", e)
                        loadFiles(path)
                        return@withContext
                    }
                    lastPath.add(files.value?.firstOrNull()?.path ?: "/")
                    files.postValue(resources)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initClient() {
        loadFiles("/")
    }
}
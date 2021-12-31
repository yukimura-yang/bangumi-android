package moe.gkd.bangumi.ui.main.webdav

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.thegrizzlylabs.sardineandroid.DavResource
import kotlinx.coroutines.*
import moe.gkd.bangumi.ui.BaseViewModel
import moe.gkd.bangumi.ui.utils.WebDavUtils
import moe.gkd.bangumi.ui.utils.WebDavUtils.getAddress
import java.net.SocketTimeoutException
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class WebdavViewModel : BaseViewModel() {

    init {
        initClient()
    }

    val resources = MutableLiveData<List<DavResource>>()

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
                    if (!isActive) return@withContext
                    val newList = ArrayList(
                        resources.subList(1, resources.size)
                            .sortedWith({ o1, o2 ->
                                if (o1.isDirectory && o2.isDirectory) {
                                    o1.name.compareTo(o2.name)
                                } else if (o1.isDirectory) {
                                    -1
                                } else if (o2.isDirectory) {
                                    1
                                } else {
                                    o1.name.compareTo(o2.name)
                                }
                            })
                    )
                    newList.add(0, resources.first())
                    this@WebdavViewModel.resources.postValue(newList)
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
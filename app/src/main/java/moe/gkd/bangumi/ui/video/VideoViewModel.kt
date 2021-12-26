package moe.gkd.bangumi.ui.video

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.gkd.bangumi.ui.BaseViewModel
import moe.gkd.bangumi.ui.utils.WebDavUtils
import okio.buffer
import okio.sink
import okio.source
import java.io.File

class VideoViewModel : BaseViewModel() {

    fun loadVideo(url: String, outputFile: File) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val inputStream = WebDavUtils.getSardine()[url]
                val source = inputStream.source().buffer()
                val sink = outputFile.sink().buffer()
                source.use { input ->
                    sink.use { output ->
                        output.writeAll(input)
                    }
                }
            }
        }
    }
}
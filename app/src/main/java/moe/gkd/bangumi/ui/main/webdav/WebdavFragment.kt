package moe.gkd.bangumi.ui.main.webdav

import android.content.Intent
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.databinding.ObservableLong
import androidx.fragment.app.viewModels
import com.thegrizzlylabs.sardineandroid.DavResource
import moe.gkd.bangumi.databinding.FragmentWebdavBinding
import moe.gkd.bangumi.isVideoFile
import moe.gkd.bangumi.ui.BaseFragment
import moe.gkd.bangumi.ui.DownloadDialog
import moe.gkd.bangumi.ui.video.VideoActivity
import java.io.File
import java.util.concurrent.CancellationException

class WebdavFragment : BaseFragment<FragmentWebdavBinding>() {
    private val viewModel: WebdavViewModel by viewModels()
    private val progress = ObservableLong(0L)

    private val adapter = WebDavAdapter()

    override fun initViews() {
        binding.recyclerView.adapter = adapter
        adapter.setOnItemClickedListener {
            if (it.isDirectory) {
                viewModel.loadFiles(it.path)
            } else {
                if (it.isVideoFile()) {
                    playVideo(it)
                }
            }
        }
        binding.parentDirectory.setOnClickListener {
            val item = viewModel.files.value?.firstOrNull() ?: return@setOnClickListener
            var path: String
            try {
                path = item.path.substring(0, item.path.lastIndexOf("/"))
                path = path.substring(0, path.lastIndexOf("/"))
            } catch (e: Exception) {
                path = "/"
            }
            viewModel.loadFiles(path)
        }
    }

    private fun playVideo(file: File) {
        val intent = Intent(requireContext(), VideoActivity::class.java)
        intent.putExtra("data", file.absolutePath)
        startActivity(intent)
    }

    private var dialog: DownloadDialog? = null
    private fun playVideo(resource: DavResource) {
        progress.set(0)
        val dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES)!!
        if (viewModel.getDownloadFile(resource.path, dir).exists()) {
            playVideo(viewModel.getDownloadFile(resource.path, dir))
            return
        }
        val cache = requireContext().externalCacheDir ?: requireContext().cacheDir
        val job = viewModel.downloadFile(resource.path, cache, dir)
        dialog = DownloadDialog(resource.name, resource.contentLength, requireContext (), progress)
            .setOnClickedListener { dialog, _ ->
                job.cancel()
                dialog?.dismiss()
            }
        dialog!!.show()
    }

    override fun initViewModel() {
        viewModel.files.observe(this) {
            val newList = it.toMutableList()
            newList.removeAt(0)
            adapter.submitList(newList.sortedBy { it.name ?: "" })
        }
        viewModel.progress.observe(this) {
            progress.set(it)
        }
        viewModel.downloaded.observe(this) {
            if (it == null) return@observe
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
            playVideo(it)
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentWebdavBinding {
        return FragmentWebdavBinding.inflate(inflater, container, false)
    }
}
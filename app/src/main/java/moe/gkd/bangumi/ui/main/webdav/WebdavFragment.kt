package moe.gkd.bangumi.ui.main.webdav

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.thegrizzlylabs.sardineandroid.DavResource
import moe.gkd.bangumi.databinding.FragmentWebdavBinding
import moe.gkd.bangumi.isVideoFile
import moe.gkd.bangumi.ui.BaseFragment

class WebdavFragment : BaseFragment<FragmentWebdavBinding>() {
    private val viewModel: WebdavViewModel by viewModels()

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

    private fun playVideo(resource: DavResource) {

    }

    override fun initViewModel() {
        viewModel.files.observe(this) {
            val newList = it.toMutableList()
            newList.removeAt(0)
            adapter.submitList(newList.sortedBy { it.name ?: "" })
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentWebdavBinding {
        return FragmentWebdavBinding.inflate(inflater, container, false)
    }
}
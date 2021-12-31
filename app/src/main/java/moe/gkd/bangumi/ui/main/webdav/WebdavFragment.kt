package moe.gkd.bangumi.ui.main.webdav

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ObservableLong
import androidx.fragment.app.viewModels
import com.thegrizzlylabs.sardineandroid.DavResource
import moe.gkd.bangumi.databinding.FragmentWebdavBinding
import moe.gkd.bangumi.getParent
import moe.gkd.bangumi.isVideoFile
import moe.gkd.bangumi.ui.BaseFragment
import moe.gkd.bangumi.ui.video.VideoActivity
import kotlin.math.log

class WebdavFragment : BaseFragment<FragmentWebdavBinding>() {
    private val viewModel: WebdavViewModel by viewModels()

    private val adapter = WebDavAdapter()

    override fun initViews() {
        binding.recyclerView.adapter = adapter
        adapter.setOnItemClickedListener {
            if (it == null) {
                //返回上一级
                Log.e(TAG, "返回 ${viewModel.resources.value?.first()?.path}")
                val path = viewModel.resources.value?.firstOrNull()?.getParent() ?: "/"
                viewModel.loadFiles(path)
            } else {
                if (it.isDirectory) {
                    viewModel.loadFiles(it.path)
                } else {
                    if (it.isVideoFile()) {
                        playVideo(it)
                    }
                }
            }
        }
    }

    private fun playVideo(resource: DavResource) {
        val intent = Intent(requireContext(), VideoActivity::class.java)
        intent.putExtra("data", resource.path)
        intent.putExtra("title", resource.name)
        intent.putExtra("isOnline", true)
        startActivity(intent)
    }

    override fun initViewModel() {
        viewModel.resources.observe(this) {
            adapter.submitList(it)
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentWebdavBinding {
        return FragmentWebdavBinding.inflate(inflater, container, false)
    }
}
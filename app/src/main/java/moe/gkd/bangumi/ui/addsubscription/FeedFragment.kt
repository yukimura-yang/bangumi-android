package moe.gkd.bangumi.ui.addsubscription

import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import moe.gkd.bangumi.R
import moe.gkd.bangumi.databinding.DialogEditTextBinding
import moe.gkd.bangumi.databinding.FragmentFeedBinding
import moe.gkd.bangumi.ui.BaseFragment

class FeedFragment : BaseFragment<FragmentFeedBinding>(), SwipeRefreshLayout.OnRefreshListener {
    private val viewModel: AddSubscriptionViewModel by activityViewModels()
    private val TAG = FeedFragment::class.simpleName
    private val adapter = TorrentSearchAdapter()

    override fun initViews() {
        setHasOptionsMenu(true)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.refreshLayout.setOnRefreshListener(this)
        binding.recyclerView.also { recyclerView ->
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun initViewModel() {
        viewModel.selectedTags.observe(this) {
            viewModel.searchTorrent()
        }
        viewModel.bangumi.observe(this) {
            adapter.submitList(it)
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFeedBinding {
        return FragmentFeedBinding.inflate(inflater, container, false)
    }

    override fun onRefresh() {
        viewModel.searchTorrent()
    }

    private val subscribeDialog by lazy {
        val binding = DialogEditTextBinding.inflate(LayoutInflater.from(context))
        binding.editText.hint = "名称"
        AlertDialog.Builder(requireContext())
            .setTitle("添加订阅")
            .setView(binding.root)
            .setPositiveButton(
                "确定"
            ) { dialog, _ ->
                val str = binding.editText.text.toString()
                viewModel.subscribe(str)
                dialog.dismiss()
            }.setNegativeButton("取消", null)
            .create()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.subscribe) {
            if (viewModel.selectedTags.value != null && viewModel.selectedTags.value!!.size > 0) {
                subscribeDialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bangumi_menu_subscribe, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
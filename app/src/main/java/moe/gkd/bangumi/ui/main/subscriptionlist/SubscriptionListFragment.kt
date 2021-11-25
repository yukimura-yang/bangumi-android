package moe.gkd.bangumi.ui.main.subscriptionlist

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import moe.gkd.bangumi.BANGUMI_ID
import moe.gkd.bangumi.BANGUMI_TITLE
import moe.gkd.bangumi.databinding.FragmentSubscriptionListBinding
import moe.gkd.bangumi.ui.BaseFragment
import moe.gkd.bangumi.ui.bangumi.BangumiActivity

class SubscriptionListFragment : BaseFragment<FragmentSubscriptionListBinding>() {
    val TAG = SubscriptionListFragment::class.simpleName
    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSubscriptionListBinding {
        return FragmentSubscriptionListBinding.inflate(inflater, container, false)
    }

    private val viewModel: SubscriptionListViewModel by viewModels()
    private val adapter = SubscriptionListAdapter()

    override fun initViews() {
        adapter.setOnItemClickedListener {
            val intent = Intent(requireContext(), BangumiActivity::class.java).apply {
                putExtra(BANGUMI_ID, it.subscription.id)
                putExtra(BANGUMI_TITLE, it.subscription.title)
            }
            startActivity(intent)
        }
        binding.recyclerView.also { recyclerView ->
            recyclerView.adapter = adapter
            recyclerView.layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
        }
    }

    override fun initViewModel() {
        viewModel.bangumis.observe(this) {
            val newList = it.sortedByDescending { it.torrents.firstOrNull()?.getTimestamp() }
            adapter.submitList(newList)
            //初始化
            if (it.isEmpty()) viewModel.initBangumis()
        }
    }

    override fun onResume() {
        super.onResume()
    }
}
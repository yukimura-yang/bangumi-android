package moe.gkd.bangumi.ui.main.subscriptionlist

import android.content.Intent
import android.view.*
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import moe.gkd.bangumi.BANGUMI_ID
import moe.gkd.bangumi.BANGUMI_TITLE
import moe.gkd.bangumi.R
import moe.gkd.bangumi.data.entity.BangumiListGroup
import moe.gkd.bangumi.databinding.FragmentSubscriptionListBinding
import moe.gkd.bangumi.ui.BaseFragment
import moe.gkd.bangumi.ui.addsubscription.AddSubscriptionActivity
import moe.gkd.bangumi.ui.bangumi.BangumiActivity

class SubscriptionListFragment : BaseFragment<FragmentSubscriptionListBinding>() {
    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSubscriptionListBinding {
        return FragmentSubscriptionListBinding.inflate(inflater, container, false)
    }

    private val viewModel: SubscriptionListViewModel by viewModels()
    private val adapter = SubscriptionListAdapter()

    override fun initViews() {
        setHasOptionsMenu(true)
        adapter.setOnItemClickedListener {
            val intent = Intent(requireContext(), BangumiActivity::class.java).apply {
                putExtra(BANGUMI_ID, it.subscription.id)
                putExtra(BANGUMI_TITLE, it.subscription.title)
            }
            startActivity(intent)
        }
        binding.recyclerView.also { recyclerView ->
            recyclerView.adapter = adapter
            val manager = GridLayoutManager(context, 2)
            manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (adapter.getItemViewType(position) == 0) {
                        1
                    } else {
                        2
                    }
                }
            }
            recyclerView.layoutManager = manager
        }
    }

    override fun initViewModel() {
        viewModel.bangumis.observe(this) {
            val scroll = adapter.currentList.size == 0
            val newList = viewModel.formatList(it)
            if (adapter.currentList.size != newList.size) {
                viewModel.checkSubscription()
            }
            adapter.submitList(newList) {
                if (!scroll) return@submitList
                val item =
                    newList.find {
                        it is BangumiListGroup && it.week == adapter.dayOfWeek
                    }
                if (item != null) {
                    binding.recyclerView.layoutManager?.scrollToPosition(newList.indexOf(item))
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add) {
            val intent = Intent(requireActivity(), AddSubscriptionActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bangumi_menu_add, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
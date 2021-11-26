package moe.gkd.bangumi.ui.addsubscription

import android.view.MenuItem
import androidx.activity.viewModels
import androidx.databinding.adapters.AdapterViewBindingAdapter
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import moe.gkd.bangumi.R
import moe.gkd.bangumi.databinding.ActivityAddSubscriptionBinding
import moe.gkd.bangumi.ui.BaseActivity

class AddSubscriptionActivity :
    BaseActivity<ActivityAddSubscriptionBinding>(R.layout.activity_add_subscription) {
    private val viewModel: AddSubscriptionViewModel by viewModels()
    private val TAG = AddSubscriptionActivity::class.simpleName
    private lateinit var mediator: TabLayoutMediator

    override fun initViews() {
        title = "搜索"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.viewPager.adapter = AddSubscriptionViewPagerAdapter(this)
        mediator = TabLayoutMediator(
            binding.tabLayout, binding.viewPager
        ) { tab, position ->
            if (position == 0) {
                tab.text = "选择标签"
            } else {
                tab.text = "搜索结果"
            }
        }
        mediator.attach()
    }

    override fun initViewModel() {
        viewModel.toast.observe(this) {
            Snackbar.make(binding.root, it, BaseTransientBottomBar.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return false
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::mediator.isInitialized) {
            mediator.detach()
        }
    }
}
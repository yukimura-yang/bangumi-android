package moe.gkd.bangumi.ui.bangumi

import android.content.DialogInterface
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import moe.gkd.bangumi.BANGUMI_ID
import moe.gkd.bangumi.BANGUMI_TITLE
import moe.gkd.bangumi.R
import moe.gkd.bangumi.data.entity.TorrentEntity
import moe.gkd.bangumi.databinding.ActivityBangumiBinding
import moe.gkd.bangumi.transmissionReady
import moe.gkd.bangumi.ui.BaseActivity


class BangumiActivity : BaseActivity<ActivityBangumiBinding>(R.layout.activity_bangumi),
    SwipeRefreshLayout.OnRefreshListener {
    private val TAG = BangumiActivity::class.simpleName

    private val viewModel: BangumiViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return BangumiViewModel(intent.getStringExtra(BANGUMI_ID)!!) as T
            }
        }
    }
    private val adapter = BangumiListAdapter()

    override fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = intent.getStringExtra(BANGUMI_TITLE)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.refreshLayout.setOnRefreshListener(this)
        binding.recyclerView.also { recyclerView ->
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)
            adapter.setOnItemClickedListener {
                if (!transmissionReady()) {
                    Snackbar.make(binding.root, "Transmission没有准备好", LENGTH_SHORT).show()
                }
                viewModel.addTorrentMagnet(it)
            }
        }
    }

    override fun initViewModel() {
        viewModel.loadState.observe(this) {
            if (it.loading) {
                Snackbar.make(binding.root, "正在更新，请稍候。", LENGTH_INDEFINITE).show()
            } else {
                if (it.success) {
                    Snackbar.make(binding.root, "更新完成", LENGTH_SHORT).show()
                } else {
                    Snackbar.make(binding.root, "更新失败", LENGTH_SHORT).show()
                }
            }
        }
        viewModel.bangumi.observe(this) {
            if (it == null) {
                finish()
            }
            val list: List<TorrentEntity> = it?.torrents ?: arrayListOf()
            val newList = list.sortedByDescending { it.getTimestamp() }
            val oldList = adapter.currentList
            val topUp = newList.size > oldList.size
            adapter.submitList(newList) {
                if (topUp) {
                    binding.recyclerView.smoothScrollToPosition(0)
                }
            }
            if (list.isEmpty()) {
                viewModel.updateSubscribe()
            }
        }
        viewModel.toast.observe(this) {
            Snackbar.make(binding.root, it, LENGTH_SHORT).show()
        }
    }

    private val unSubscribeAlertDialog by lazy {
        AlertDialog.Builder(this)
            .setTitle("确定要取消订阅吗")
            .setPositiveButton(
                "确定"
            ) { dialog, _ ->
                viewModel.unSubscribe()
                dialog.dismiss()
            }
            .setNegativeButton("取消", null)
            .create()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bangumi_menu_unsubscribe, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return false
        } else if (item.itemId == R.id.unSubscribe) {
            unSubscribeAlertDialog.show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRefresh() {
        viewModel.updateSubscribe()
    }
}
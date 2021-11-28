package moe.gkd.bangumi.ui.bangumi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.google.android.material.chip.Chip
import moe.gkd.bangumi.BANGUMI_MOE_HOST_URL
import moe.gkd.bangumi.data.entity.TorrentEntity
import moe.gkd.bangumi.databinding.ItemBangumiListBinding
import moe.gkd.bangumi.ui.widget.BaseViewHolder
import moe.gkd.bangumi.ui.widget.OnItemClickedListener

class BangumiListAdapter : ListAdapter<TorrentEntity, BaseViewHolder<ItemBangumiListBinding>>(
    object : DiffUtil.ItemCallback<TorrentEntity>() {
        override fun areItemsTheSame(oldItem: TorrentEntity, newItem: TorrentEntity): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: TorrentEntity, newItem: TorrentEntity): Boolean {
            return oldItem == newItem
        }
    }
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemBangumiListBinding> {
        val holder = BaseViewHolder(
            ItemBangumiListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        holder.binding.host = BANGUMI_MOE_HOST_URL
        return holder
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemBangumiListBinding>, position: Int) {
        val item = getItem(position)
        holder.binding.also { binding ->
            binding.data = item
            binding.buttonText = if (item.transmissionId != null) {
                "重新下载 (${item.size})"
            } else {
                "下载 (${item.size})"
            }
            binding.chipGroup.removeAllViews()
            for (tag in item.tags) {
                val chip = Chip(holder.itemView.context)
                chip.text = tag.getRecommendName()
                binding.chipGroup.addView(chip)
            }
            binding.download.setOnClickListener { downloadListener?.onItemClicked(item) }
            binding.refresh.setOnClickListener { refreshListener?.onItemClicked(item) }
        }
    }

    private var downloadListener: OnItemClickedListener<TorrentEntity>? = null

    fun setOnDownloadClickedListener(listener: (TorrentEntity) -> Unit) {
        this.downloadListener = object : OnItemClickedListener<TorrentEntity> {
            override fun onItemClicked(item: TorrentEntity) {
                listener.invoke(item)
            }
        }
    }

    private var refreshListener: OnItemClickedListener<TorrentEntity>? = null
    fun setOnRefreshClickedListener(listener: (TorrentEntity) -> Unit) {
        this.refreshListener = object : OnItemClickedListener<TorrentEntity> {
            override fun onItemClicked(item: TorrentEntity) {
                listener.invoke(item)
            }
        }
    }
}
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
            binding.buttonText = if (item.downloaded) {
                "重新下载 (${item.size})"
            } else {
                "下载 (${item.size})"
            }
            binding.chipGroup.removeAllViews()
            for (tag in item.tags) {
                val chip = Chip(holder.itemView.context)
                chip.text = tag
                binding.chipGroup.addView(chip)
            }
            binding.button.setOnClickListener { listener?.onItemClicked(item) }
        }
    }

    private var listener: OnItemClickedListener<TorrentEntity>? = null
    fun setOnItemClickedListener(listener: (TorrentEntity) -> Unit) {
        this.listener = object : OnItemClickedListener<TorrentEntity> {
            override fun onItemClicked(item: TorrentEntity) {
                listener.invoke(item)
            }
        }
    }
}
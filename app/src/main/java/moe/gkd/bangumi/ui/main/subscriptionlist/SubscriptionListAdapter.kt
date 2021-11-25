package moe.gkd.bangumi.ui.main.subscriptionlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import moe.gkd.bangumi.data.entity.BangumiEntity
import moe.gkd.bangumi.databinding.ItemSubscriptionListBinding
import moe.gkd.bangumi.ui.widget.BaseViewHolder
import moe.gkd.bangumi.ui.widget.OnItemClickedListener

class SubscriptionListAdapter :
    ListAdapter<BangumiEntity, BaseViewHolder<ItemSubscriptionListBinding>>(
        object : DiffUtil.ItemCallback<BangumiEntity>() {
            override fun areItemsTheSame(
                oldItem: BangumiEntity,
                newItem: BangumiEntity
            ): Boolean {
                return oldItem.subscription.id == newItem.subscription.id
            }

            override fun areContentsTheSame(
                oldItem: BangumiEntity,
                newItem: BangumiEntity
            ): Boolean {
                if (oldItem.torrents.isEmpty() && newItem.torrents.isEmpty()) {
                    return true
                } else if (oldItem.torrents.isEmpty() || newItem.torrents.isEmpty()) {
                    return false
                } else {
                    return oldItem.torrents.first().uid == newItem.torrents.first().uid
                }
            }
        }
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemSubscriptionListBinding> {
        return BaseViewHolder(
            ItemSubscriptionListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemSubscriptionListBinding>,
        position: Int
    ) {
        val item = getItem(position)
        holder.binding.also { binding ->
            binding.title = item.subscription.title
            if (item.torrents.isNotEmpty()) {
                binding.lastTitle = item.torrents.first().title
                binding.updateDate = item.torrents.first().getFormatTime()
            } else {
                binding.lastTitle = "NULL"
                binding.updateDate = "NULL"
            }
        }
        holder.itemView.setOnClickListener {
            listener?.onItemClicked(item)
        }
    }

    private var listener: OnItemClickedListener<BangumiEntity>? = null
    fun setOnItemClickedListener(listener: (BangumiEntity) -> Unit) {
        this.listener = object : OnItemClickedListener<BangumiEntity> {
            override fun onItemClicked(item: BangumiEntity) {
                listener.invoke(item)
            }
        }
    }
}
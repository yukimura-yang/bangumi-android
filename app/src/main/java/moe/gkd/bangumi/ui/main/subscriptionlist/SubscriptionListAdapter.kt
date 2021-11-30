package moe.gkd.bangumi.ui.main.subscriptionlist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import moe.gkd.bangumi.data.entity.BangumiEntity
import moe.gkd.bangumi.data.entity.BangumiListGroup
import moe.gkd.bangumi.data.entity.BangumiListInterface
import moe.gkd.bangumi.databinding.GroupSubscriptionListBinding
import moe.gkd.bangumi.databinding.ItemSubscriptionListBinding
import moe.gkd.bangumi.dayOfWeek2JpWeek
import moe.gkd.bangumi.dayOfWeekColor
import moe.gkd.bangumi.timestamp2Local
import moe.gkd.bangumi.ui.widget.BaseViewHolder
import moe.gkd.bangumi.ui.widget.OnItemClickedListener
import java.time.DayOfWeek
import java.time.LocalDate

class SubscriptionListAdapter :
    ListAdapter<BangumiListInterface, BaseViewHolder<*>>(
        object : DiffUtil.ItemCallback<BangumiListInterface>() {
            override fun areItemsTheSame(
                oldItem: BangumiListInterface,
                newItem: BangumiListInterface
            ): Boolean {
                if (oldItem is BangumiListGroup && newItem is BangumiListGroup) {
                    return oldItem.week == newItem.week
                } else if (oldItem is BangumiEntity && newItem is BangumiEntity) {
                    return oldItem.subscription.id == newItem.subscription.id
                } else {
                    return false
                }
            }

            override fun areContentsTheSame(
                oldItem: BangumiListInterface,
                newItem: BangumiListInterface
            ): Boolean {
                if (oldItem is BangumiListGroup && newItem is BangumiListGroup) {
                    return oldItem.week == newItem.week
                } else if (oldItem is BangumiEntity && newItem is BangumiEntity) {
                    if (oldItem.torrents.isEmpty() && newItem.torrents.isEmpty()) {
                        return true
                    } else if (oldItem.torrents.isEmpty() || newItem.torrents.isEmpty()) {
                        return false
                    } else if (oldItem.subscription.lastUpdateTime != newItem.subscription.lastUpdateTime) {
                        return false
                    } else {
                        return oldItem.torrents.first().uid == newItem.torrents.first().uid
                    }
                } else {
                    return false
                }
            }
        }
    ) {

    val dayOfWeek: DayOfWeek

    init {
        val localDate = LocalDate.now()
        dayOfWeek = localDate.dayOfWeek
    }

    /**
     * 0 普通 1 group
     */
    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        if (item is BangumiListGroup) {
            return 1
        } else {
            return 0
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<*> {
        if (viewType == 0) {
            return BaseViewHolder(
                ItemSubscriptionListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            return BaseViewHolder(
                GroupSubscriptionListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }


    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        if (getItemViewType(position) == 0) {
            onBindNormalViewHolder(holder as BaseViewHolder<ItemSubscriptionListBinding>, position)
        } else {
            onBindGroupViewHolder(holder as BaseViewHolder<GroupSubscriptionListBinding>, position)
        }
    }

    private fun onBindNormalViewHolder(
        holder: BaseViewHolder<ItemSubscriptionListBinding>,
        position: Int
    ) {
        val item = getItem(position) as BangumiEntity
        holder.binding.also { binding ->
            binding.title = item.subscription.title
            if (item.torrents.isNotEmpty()) {
                binding.lastTitle = item.torrents.first().title
            } else {
                binding.lastTitle = "NULL"
            }
            binding.updateDate = timestamp2Local(item.subscription.lastUpdateTime)
            binding.hasUpdate = item.hasUpdate()
        }
        holder.itemView.setOnClickListener {
            listener?.onItemClicked(item)
        }
    }

    private fun onBindGroupViewHolder(
        holder: BaseViewHolder<GroupSubscriptionListBinding>,
        position: Int
    ) {
        val item = getItem(position) as BangumiListGroup
        holder.binding.today = item.week == dayOfWeek
        holder.binding.color = dayOfWeekColor(item.week)
        holder.binding.title = dayOfWeek2JpWeek(item.week)
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
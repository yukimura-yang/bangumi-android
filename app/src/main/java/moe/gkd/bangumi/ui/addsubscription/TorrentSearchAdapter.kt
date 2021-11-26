package moe.gkd.bangumi.ui.addsubscription

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import moe.gkd.bangumi.BANGUMI_MOE_HOST_URL
import moe.gkd.bangumi.data.entity.TorrentEntity
import moe.gkd.bangumi.databinding.ItemTorrentSearchBinding
import moe.gkd.bangumi.ui.widget.BaseViewHolder

class TorrentSearchAdapter : ListAdapter<TorrentEntity, BaseViewHolder<ItemTorrentSearchBinding>>(
    object : DiffUtil.ItemCallback<TorrentEntity>() {
        override fun areItemsTheSame(oldItem: TorrentEntity, newItem: TorrentEntity): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: TorrentEntity, newItem: TorrentEntity): Boolean {
            return oldItem == newItem
        }
    }) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemTorrentSearchBinding> {
        return BaseViewHolder(
            ItemTorrentSearchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemTorrentSearchBinding>, position: Int) {
        val item = getItem(position)
        holder.binding.data = item
    }
}
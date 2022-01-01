package moe.gkd.bangumi.ui.main.webdav

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.thegrizzlylabs.sardineandroid.DavResource
import moe.gkd.bangumi.databinding.ItemWebdavFileBinding
import moe.gkd.bangumi.isVideoFile
import moe.gkd.bangumi.ui.widget.BaseViewHolder
import moe.gkd.bangumi.ui.widget.OnItemClickedListener

class WebDavAdapter(private val viewModel: WebdavViewModel) :
    ListAdapter<DavResource, BaseViewHolder<ItemWebdavFileBinding>>(
        object : DiffUtil.ItemCallback<DavResource?>() {
            override fun areItemsTheSame(
                oldItem: DavResource,
                newItem: DavResource
            ): Boolean {
//                return oldItem == newItem
                return false
            }

            override fun areContentsTheSame(
                oldItem: DavResource,
                newItem: DavResource
            ): Boolean {
//                return oldItem.equals(newItem)
                return false
            }
        }
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemWebdavFileBinding> {
        return BaseViewHolder(
            ItemWebdavFileBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    /**
     * contentType  0 目录，1 视频文件，2 其他文件
     */
    override fun onBindViewHolder(holder: BaseViewHolder<ItemWebdavFileBinding>, position: Int) {
        val resource = getItem(position)
        if (position == 0) {
            holder.binding.name = "..."
            holder.binding.contentType = -1
            holder.binding.state = 0
        } else {
            holder.binding.name = resource.name
            holder.binding.contentType = resource.let {
                if (it.isDirectory) {
                    0
                } else if (it.isVideoFile()) {
                    1
                } else {
                    2
                }
            }
            val state: Int =
                if (resource.isVideoFile()) {
                    viewModel.getHistoryState(resource.path)
                } else {
                    0
                }
            holder.binding.state = state
        }
        holder.itemView.setOnClickListener {
            if (position == 0) {
                listener?.onItemClicked(null)
            } else {
                listener?.onItemClicked(resource)
            }
        }
    }

    private var listener: OnItemClickedListener<DavResource?>? = null

    fun setOnItemClickedListener(listener: (DavResource?) -> Unit) {
        this.listener = object : OnItemClickedListener<DavResource?> {
            override fun onItemClicked(item: DavResource?) {
                listener.invoke(item)
            }
        }
    }
}
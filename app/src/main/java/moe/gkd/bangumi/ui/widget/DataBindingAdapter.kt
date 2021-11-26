package moe.gkd.bangumi.ui.widget

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import moe.gkd.bangumi.R

@BindingAdapter("isRefreshing")
fun isRefreshing(view: SwipeRefreshLayout, isRefreshing: Boolean) {
    view.isRefreshing = isRefreshing
}

@BindingAdapter("setAvatarUrl")
fun setAvatarUrl(view: ImageView, url: String) {
    Glide.with(view)
        .load(url)
        .placeholder(R.mipmap.waifu)
        .error(R.mipmap.waifu)
        .into(view)
}

@BindingAdapter("setChecked")
fun setChecked(view: SettingsSwitchView, checked: Boolean) {
    view.setChecked(checked)
}
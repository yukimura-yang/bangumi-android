package moe.gkd.bangumi.ui.widget

import android.graphics.Typeface
import android.widget.ImageView
import android.widget.TextView
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
        .placeholder(R.mipmap.ic_launcher)
        .error(R.mipmap.ic_launcher)
        .into(view)
}

@BindingAdapter("setChecked")
fun setChecked(view: SettingsSwitchView, checked: Boolean) {
    view.setChecked(checked)
}

@BindingAdapter("isBold")
fun setBold(view: TextView, isBold: Boolean) {
    if (isBold) {
        view.setTypeface(null, Typeface.BOLD);
    } else {
        view.setTypeface(null, Typeface.NORMAL);
    }
}
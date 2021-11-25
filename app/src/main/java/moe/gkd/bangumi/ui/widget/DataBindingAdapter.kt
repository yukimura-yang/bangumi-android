package moe.gkd.bangumi.ui.widget

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide

@BindingAdapter("isRefreshing")
fun isRefreshing(view: SwipeRefreshLayout, isRefreshing: Boolean) {
    view.isRefreshing = isRefreshing
}

@BindingAdapter("setImageUrl")
fun setImageUrl(view: ImageView, url: String) {
    Glide.with(view).load(url).into(view)
}

@BindingAdapter("setTitle")
fun setTitle(view: SettingsTextView, str: String) {
    view.setTitle(str)
}

@BindingAdapter("setNum")
fun setNum(view: SettingsTextView, isNum: Boolean) {
    view.setNum(isNum)
}

@BindingAdapter("setTitle")
fun setTitle(view: SettingsSwitchView, str: String) {
    view.setTitle(str)
}

@BindingAdapter("setChecked")
fun setChecked(view: SettingsSwitchView, checked: Boolean) {
    view.setChecked(checked)
}


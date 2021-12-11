package moe.gkd.bangumi.ui

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewbinding.ViewBinding
import moe.gkd.bangumi.TAG

abstract class BaseActivity<B : ViewBinding>(@LayoutRes val layoutId: Int) : AppCompatActivity() {
    protected val TAG = this.TAG()

    protected lateinit var binding: B
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding(layoutId)
        initViews()
        initViewModel()
    }

    protected abstract fun initViews()

    protected abstract fun initViewModel()

    private fun setBinding(@LayoutRes id: Int) {
        binding = DataBindingUtil.setContentView(this, id)
    }
}
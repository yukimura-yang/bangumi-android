package moe.gkd.bangumi.ui.main.about

import android.view.LayoutInflater
import android.view.ViewGroup
import moe.gkd.bangumi.databinding.FragmentAboutBinding
import moe.gkd.bangumi.ui.BaseFragment

class AboutFragment : BaseFragment<FragmentAboutBinding>() {
    override fun initViews() {

    }

    override fun initViewModel() {
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAboutBinding {
        return FragmentAboutBinding.inflate(inflater, container, false)
    }
}
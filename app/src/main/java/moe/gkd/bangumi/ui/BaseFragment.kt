package moe.gkd.bangumi.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<B : ViewBinding> : Fragment() {
    private var _binding: B? = null

    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (_binding != null) {
            return binding.root
        }
        _binding = inflateViewBinding(inflater, container)
        initViews()
        initViewModel()
        return binding.root
    }

    protected abstract fun initViews()

    protected abstract fun initViewModel()
    protected abstract fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): B

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
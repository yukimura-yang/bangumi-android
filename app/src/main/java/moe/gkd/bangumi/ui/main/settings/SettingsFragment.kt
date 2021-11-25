package moe.gkd.bangumi.ui.main.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import moe.gkd.bangumi.*
import moe.gkd.bangumi.data.SharedPreferencesHelper
import moe.gkd.bangumi.databinding.FragmentSettingsBinding
import moe.gkd.bangumi.ui.BaseFragment

class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {
    private val viewModel: SettingsViewModel by viewModels()
    private val checking = ObservableBoolean(false)
    override fun initViews() {
        binding.checking = checking
        //初始化数据
        binding.bangumiCdn.setChecked(MainApplication.INSTANCE.hashMap[USE_BANGUMI_MOE_CDN] as Boolean)
        binding.transmissionHost.setContent(MainApplication.INSTANCE.hashMap[TRANSMISSION_HOST].toString())
        binding.transmissionPort.setContent(MainApplication.INSTANCE.hashMap[TRANSMISSION_PORT].toString())
        binding.transmissionSsl.setChecked(MainApplication.INSTANCE.hashMap[TRANSMISSION_SSL] as Boolean)
        binding.transmissionUsername.setContent(MainApplication.INSTANCE.hashMap[TRANSMISSION_USERNAME].toString())
        binding.transmissionPassword.setContent(MainApplication.INSTANCE.hashMap[TRANSMISSION_PASSWORD].toString())
        binding.transmissionRpc.setContent(MainApplication.INSTANCE.hashMap[TRANSMISSION_RPC].toString())
        binding.transmissionSaveDir.setContent(MainApplication.INSTANCE.hashMap[TRANSMISSION_SAVE_DIR].toString())


        binding.bangumiCdn.setOnCheckedChangeListener { buttonView, isChecked ->
            MainApplication.INSTANCE.hashMap[USE_BANGUMI_MOE_CDN] = isChecked
            SharedPreferencesHelper.SP.bangumiCDN = isChecked
        }
        binding.transmissionHost.setInputListener { text ->
            MainApplication.INSTANCE.hashMap[TRANSMISSION_HOST] = text
            SharedPreferencesHelper.SP.transmissionHost = text
        }
        binding.transmissionPort.setInputListener { text ->
            MainApplication.INSTANCE.hashMap[TRANSMISSION_PORT] = text
            SharedPreferencesHelper.SP.transmissionPort = text
        }
        binding.transmissionSsl.setOnCheckedChangeListener { buttonView, isChecked ->
            MainApplication.INSTANCE.hashMap[TRANSMISSION_SSL] = isChecked
            SharedPreferencesHelper.SP.transmissionSSL = isChecked
        }
        binding.transmissionUsername.setInputListener { text ->
            MainApplication.INSTANCE.hashMap[TRANSMISSION_USERNAME] = text
            SharedPreferencesHelper.SP.transmissionUsername = text
        }
        binding.transmissionPassword.setInputListener { text ->
            MainApplication.INSTANCE.hashMap[TRANSMISSION_PASSWORD] = text
            SharedPreferencesHelper.SP.transmissionPassword = text
        }
        binding.transmissionRpc.setInputListener { text ->
            MainApplication.INSTANCE.hashMap[TRANSMISSION_RPC] = text
            SharedPreferencesHelper.SP.transmissionRpc = text
        }
        binding.transmissionSaveDir.setInputListener { text ->
            MainApplication.INSTANCE.hashMap[TRANSMISSION_SAVE_DIR] = text
            SharedPreferencesHelper.SP.transmissionSaveDir = text
        }
        binding.checkRequest.setOnClickListener(this::onCheckTransmission)
    }

    private fun onCheckTransmission(view: View) {
        viewModel.checkTransmission()
    }

    override fun initViewModel() {
        viewModel.checking.observe(this) {
            checking.set(it)
        }
        viewModel.toast.observe(this) {
            Snackbar.make(binding.root, it, LENGTH_SHORT).show()
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }
}
package moe.gkd.bangumi.ui.main.transmission

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.HttpAuthHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import moe.gkd.bangumi.*
import moe.gkd.bangumi.databinding.FragmentTransmissionBinding
import moe.gkd.bangumi.ui.BaseFragment
import okhttp3.HttpUrl

class TransmissionFragment : BaseFragment<FragmentTransmissionBinding>() {
    override fun initViews() {
        binding.webView.also { webView ->
            webView.loadUrl(
                getTransmissionUrl()
            )
            webView.webViewClient = webViewClient
            webView.settings.also { webSettings ->
                webSettings.javaScriptEnabled = true
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            binding.webView.goBack()
        }
    }


    private val webViewClient = object : WebViewClient() {
        override fun onReceivedHttpAuthRequest(
            view: WebView?,
            handler: HttpAuthHandler?,
            host: String?,
            realm: String?
        ) {
            val userName = MainApplication.INSTANCE.hashMap[TRANSMISSION_USERNAME].toString()
            val password = MainApplication.INSTANCE.hashMap[TRANSMISSION_PASSWORD].toString()
            handler?.proceed(userName, password)
        }
    }

    private fun getTransmissionUrl(): String {
        val scheme =
            if (MainApplication.INSTANCE.hashMap[TRANSMISSION_SSL] == true) "https" else "http"
        val host = MainApplication.INSTANCE.hashMap[TRANSMISSION_HOST].toString()
        val port = MainApplication.INSTANCE.hashMap[TRANSMISSION_PORT].toString().toIntOrNull() ?: 0
        try {
            val httpUrl = HttpUrl.Builder()
                .scheme(scheme)
                .host(host)
                .port(port)
                .build()
            return httpUrl.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    override fun initViewModel() {
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTransmissionBinding {
        return FragmentTransmissionBinding.inflate(inflater, container, false)
    }
}
package moe.gkd.bangumi.ui.video

import android.util.Log
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.thegrizzlylabs.sardineandroid.util.SardineUtil
import moe.gkd.bangumi.R
import moe.gkd.bangumi.databinding.ActivityVideoBinding
import moe.gkd.bangumi.ui.BaseActivity
import moe.gkd.bangumi.ui.utils.WebDavUtils
import okhttp3.Credentials.basic

class VideoActivity : BaseActivity<ActivityVideoBinding>(R.layout.activity_video), Player.Listener {
    private val isOnline by lazy {
        intent.getBooleanExtra("isOnline", false)
    }

    private val data by lazy {
        if (isOnline) {
            WebDavUtils.getAddress() + intent.getStringExtra("data").toString()
        } else {
            intent.getStringExtra("data").toString()
        }
    }


    override fun initViews() {
        hideSystemBars()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        initVideo()
    }

    private fun headers(): DataSource.Factory {
        val headersMap = hashMapOf<String, String>()
        headersMap["Authorization"] =
            basic(WebDavUtils.getUserName(), WebDavUtils.getPassword(), SardineUtil.standardUTF8())
        return DefaultHttpDataSource.Factory().setDefaultRequestProperties(headersMap)
    }

    private lateinit var player: Player

    private fun initVideo() {
        Log.e(TAG, "initVideo: $data")
        player = SimpleExoPlayer.Builder(this)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(headers()),
            )
            .build()
        player.addListener(this)
        binding.player.player = player
        val mediaItem = MediaItem.fromUri(data)
        player.addMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    override fun initViewModel() {
    }

    private fun hideSystemBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        // Configure the behavior of the hidden system bars
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        supportActionBar?.hide()
    }

    override fun onPlayerError(error: PlaybackException) {
        Log.e(TAG, "onPlayerError", error)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::player.isInitialized) {
            player.release()
            player.stop()
        }
    }
}
package moe.gkd.bangumi.ui.video

import android.util.Log
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.thegrizzlylabs.sardineandroid.util.SardineUtil
import moe.gkd.bangumi.R
import moe.gkd.bangumi.databinding.ActivityVideoBinding
import moe.gkd.bangumi.http.DnsUtils
import moe.gkd.bangumi.ui.BaseActivity
import moe.gkd.bangumi.ui.utils.WebDavUtils
import okhttp3.Credentials.basic
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

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

    private lateinit var player: Player

    private fun initVideo() {
        Log.e(TAG, "initVideo: $data")
        val dataSource = DefaultDataSourceFactory(
            this, OkHttpDataSource.Factory(
                OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .dns(DnsUtils.getDns())
                    .addInterceptor {
                        val request = it.request().newBuilder()
                        request.removeHeader("Authorization")
                        request.addHeader(
                            "Authorization",
                            basic(
                                WebDavUtils.getUserName(),
                                WebDavUtils.getPassword(),
                                SardineUtil.standardUTF8()
                            )
                        )
                        it.proceed(request.build())
                    }
                    .build())
        )
        player = SimpleExoPlayer.Builder(this)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(dataSource),
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
            player.stop()
            player.release()
        }
    }
}
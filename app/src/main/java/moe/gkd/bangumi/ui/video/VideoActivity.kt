package moe.gkd.bangumi.ui.video

import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.STATE_ENDED
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.ui.PlayerControlView
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

class VideoActivity : BaseActivity<ActivityVideoBinding>(R.layout.activity_video), Player.Listener,
    PlayerControlView.VisibilityListener {
    private val isOnline by lazy {
        intent.getBooleanExtra("isOnline", false)
    }
    private val viewModel: VideoViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @SuppressWarnings("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return VideoViewModel(intent.getStringExtra("data").toString()) as T
            }
        }
    }

    private val isShow = ObservableBoolean(true)
    private val title = ObservableField<String>()

    private val data by lazy {
        if (isOnline) {
            WebDavUtils.getAddress() + intent.getStringExtra("data").toString()
        } else {
            intent.getStringExtra("data").toString()
        }
    }


    override fun initViews() {
        title.set(intent.getStringExtra("title"))
        binding.isShow = isShow
        binding.title = title
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        initVideo()
        binding.player.setControllerVisibilityListener(this)
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
        viewModel.playHistory.observe(this) {
            if (it == null) return@observe
            player.seekTo(it.duration)
        }
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

    private fun showSystemBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
    }

    override fun onPlayerError(error: PlaybackException) {
        Log.e(TAG, "onPlayerError", error)
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

    override fun onPause() {
        super.onPause()
        binding.root.post {
            showSystemBars()
        }
        if (this::player.isInitialized) {
            player.pause()
        }
    }

    override fun onDestroy() {
        if (this::player.isInitialized) {
            player.release()
            if (viewModel.isEnd) {
                viewModel.setPlayHistory(
                    true,
                    0
                )
            } else {
                viewModel.setPlayHistory(
                    false,
                    player.currentPosition
                )
            }
        }
        super.onDestroy()
    }

    override fun onVisibilityChange(visibility: Int) {
        isShow.set(visibility == View.VISIBLE)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        viewModel.isEnd = playbackState == STATE_ENDED
    }
}
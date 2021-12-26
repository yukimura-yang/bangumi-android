package moe.gkd.bangumi.ui.video

import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import moe.gkd.bangumi.R
import moe.gkd.bangumi.databinding.ActivityVideoBinding
import moe.gkd.bangumi.ui.BaseActivity
import moe.gkd.bangumi.ui.widget.ijkplayer.AndroidMediaController
import tv.danmaku.ijk.media.player.IjkMediaPlayer

class VideoActivity : BaseActivity<ActivityVideoBinding>(R.layout.activity_video) {
    private lateinit var mediaController: AndroidMediaController
    private val data by lazy {
        intent.getStringExtra("data")
    }

    override fun initViews() {
        hideSystemBars()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // init player
        mediaController = AndroidMediaController(this, false)
        IjkMediaPlayer.loadLibrariesOnce(null)
        IjkMediaPlayer.native_profileBegin("libijkplayer.so")
        binding.videoView.setMediaController(mediaController)
        binding.videoView.setHudView(binding.hudView)
        binding.videoView.setVideoPath(data)
        binding.videoView.start()
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

    override fun onDestroy() {
        super.onDestroy()
        binding.videoView.stopPlayback()
        binding.videoView.release(true)
        IjkMediaPlayer.native_profileEnd()
    }
}
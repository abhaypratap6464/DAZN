package com.example.dzan.ui

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.dzan.R
import com.example.dzan.databinding.ActivityMainBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var exoPlayer: ExoPlayer? = null
    private var playbackPosition = 0L
    private var playWhenReady = true
    private lateinit var pauseBtn : ImageButton
    private lateinit var playBtn : ImageButton
    private lateinit var ffwdBtn : ImageButton
    private lateinit var rewBtn : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        setView()
        initView()
        preparePlayer()
        initClickListeners()
    }

    private fun initView() {
        pauseBtn=findViewById(com.google.android.exoplayer2.R.id.exo_pause)
        playBtn=findViewById(com.google.android.exoplayer2.R.id.exo_play)
        ffwdBtn=findViewById(com.google.android.exoplayer2.R.id.exo_ffwd)
        rewBtn=findViewById(com.google.android.exoplayer2.R.id.exo_rew)
        binding.tvPauseCount.text = getString(R.string.pause_count,PAUSE_COUNT)
        binding.tvBackwardCount.text = getString(R.string.rewbtn_count, REWIND_COUNT)
        binding.tvForwardCount.text = getString(R.string.pause_count, FORWARD_COUNT)
    }

    private fun initClickListeners() {
        pauseBtn.setOnClickListener {
            firebaseAnalytics.logEvent(AnalyticsEvent.PAUSE_BTN_CLICKED,null)
            PAUSE_COUNT +=1
            pauseBtn.isEnabled = false
            playBtn.isEnabled = true
            binding.tvPauseCount.text = getString(R.string.pause_count,PAUSE_COUNT)
            exoPlayer?.pause()
        }

        playBtn.setOnClickListener {
            pauseBtn.isEnabled = true
            playBtn.isEnabled = false
            firebaseAnalytics.logEvent(AnalyticsEvent.PLAY_BTN_CLICKED,null)
            exoPlayer?.play()
        }

        ffwdBtn.setOnClickListener {
            FORWARD_COUNT +=1
            binding.tvForwardCount.text = getString(R.string.ffwdbtn_count,FORWARD_COUNT)
            firebaseAnalytics.logEvent(AnalyticsEvent.FORWARD_BTN_CLICKED,null)
            exoPlayer?.seekForward()
        }

        rewBtn.setOnClickListener {
            REWIND_COUNT +=1
            binding.tvBackwardCount.text = getString(R.string.rewbtn_count, REWIND_COUNT)
            firebaseAnalytics.logEvent(AnalyticsEvent.BACKWARD_BTN_CLICKED,null)
            exoPlayer?.seekBack()
        }
    }

    private fun setView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun preparePlayer() {
        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer?.playWhenReady = true
        binding.playerView.player = exoPlayer
        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
        val mediaItem = MediaItem.fromUri(URL)
        val mediaSource =
            DashMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mediaItem)
        exoPlayer?.apply {
            setMediaSource(mediaSource)
            seekTo(playbackPosition)
            playWhenReady = playWhenReady
            prepare()
        }

    }

    private fun releasePlayer() {
        exoPlayer?.let { player ->
            playbackPosition = player.currentPosition
            playWhenReady = player.playWhenReady
            player.release()
            exoPlayer = null
        }
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }


    companion object {
        const val URL = "https://storage.googleapis.com/wvmedia/clear/vp9/tears/tears_uhd.mpd"
        var PAUSE_COUNT:Int = 0
        var REWIND_COUNT = 0
        var FORWARD_COUNT =0
    }
}


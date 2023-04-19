package com.example.dzan.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dzan.databinding.ActivityMainBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource


class MainActivity : AppCompatActivity(), Player.Listener {

    private lateinit var binding: ActivityMainBinding
    private var exoPlayer: ExoPlayer? = null
    private var playbackPosition = 0L
    private var playWhenReady = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setView()
        preparePlayer()
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
        exoPlayer?.addListener(this)

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

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        Toast.makeText(this, if (isPlaying) "Playing" else "Paused", Toast.LENGTH_LONG).show()

    }

    override fun onPlaybackStateChanged(playbackState: Int) {


    }

    override fun onSeekBackIncrementChanged(seekBackIncrementMs: Long) {
        Toast.makeText(this, "backward", Toast.LENGTH_LONG).show()
    }

    override fun onSeekForwardIncrementChanged(seekForwardIncrementMs: Long) {
        Toast.makeText(this, "forward", Toast.LENGTH_LONG).show()
    }

    companion object {
        const val URL = "https://storage.googleapis.com/wvmedia/clear/vp9/tears/tears_uhd.mpd"
    }

}
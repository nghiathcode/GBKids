package vn.android.thn.gbfilm.views.dialogs

import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.PlaybackPreparer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoListener
import vn.android.thn.commons.App
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.views.activity.MainActivity
import vn.android.thn.library.utils.GBLog
import vn.android.thn.library.views.dialogs.GBDialogFragment


//
// Created by NghiaTH on 3/20/19.
// Copyright (c) 2019

class FullScreenDialog : GBDialogFragment(), PlaybackPreparer {
    override fun preparePlayback() {

    }
    var TAG = "FullScreenDialog"
    var playerView: PlayerView? =null

    lateinit var player: SimpleExoPlayer

    lateinit var listener: FullScreenListener
    private lateinit var fullScreen: ImageView
    lateinit var mVideoListener:VideoListener
    lateinit var mEventListener: Player.EventListener
    lateinit var stream_loading:View
    lateinit var img_thumbnail_video:ImageView
    lateinit var video_error: TextView
    lateinit var main_player_view: FrameLayout
    override fun initView() {
        isCancelable = true
        playerView =  findViewById(R.id.player_view_full_screen)!!
        fullScreen = findViewById<ImageView>(R.id.exo_fullscreen_button)!!
        fullScreen.setImageResource(R.drawable.unfullscreen)
        fullScreen.setOnClickListener {
            dismiss()
        }
        stream_loading = findViewById(R.id.stream_loading)!!
        img_thumbnail_video = findViewById(R.id.img_thumbnail_video)!!
        video_error = findViewById(R.id.video_error)!!
        main_player_view = findViewById(R.id.main_player_view)!!
        playerView!!.useController = true
        playerView!!.player = player
        playerView!!.setPlaybackPreparer(this)
    }
    fun initializePlayer() {
        player.setPlayWhenReady(true)
    }
    override fun dialogName(): String {
        return "FullScreenDialog"
    }
    override fun styleDialog(): Int {
        return R.style.AppTheme_Flash
    }

    override fun dismiss() {
        super.dismiss()
        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        playerView = null
        listener.onCloseFullScreen(player.currentPosition)

    }
    override fun layoutFileCommon(): Int {
        return R.layout.dialog_full_creen
    }
    fun onLoading(isComplete:Boolean){
        if (isComplete){
            stream_loading.visibility = View.GONE
        } else {
            stream_loading.visibility = View.VISIBLE
        }
    }
    fun onError(){
        stream_loading.visibility = View.GONE
        video_error.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        initializePlayer()
    }

    interface FullScreenListener{
        fun onCloseFullScreen(currentStop: Long)
    }

    override fun onCancel(dialog: DialogInterface?) {
        dismiss()
    }
}

package vn.android.thn.gbkids.views.dialogs

import android.content.pm.ActivityInfo
import android.view.View
import android.widget.ImageView
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.PlaybackPreparer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoListener
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.utils.LogUtils
import vn.android.thn.gbkids.views.activity.MainActivity
import vn.android.thn.library.views.dialogs.GBDialogFragment


//
// Created by NghiaTH on 3/20/19.
// Copyright (c) 2019

class FullScreenDialog : GBDialogFragment(), PlaybackPreparer {
    override fun preparePlayback() {

    }
    var TAG = "FullScreenDialog"
    var currentStop: Long = 0
    lateinit var playerView: PlayerView
    lateinit var trackSelector: DefaultTrackSelector
    var player: SimpleExoPlayer? = null
    lateinit var  videoSource: ExtractorMediaSource
    lateinit var listener: FullScreenListener
    private lateinit var fullScreen: ImageView
    override fun initView() {
        playerView =  findViewById(R.id.player_view)!!
        fullScreen = findViewById<ImageView>(R.id.exo_fullscreen_button)!!
        fullScreen.setImageResource(R.drawable.unfullscreen)
        fullScreen.setOnClickListener {
            dismiss()
        }
//        initializePlayer()
    }
    fun initializePlayer() {

        player = ExoPlayerFactory.newSimpleInstance(activity, trackSelector);
        playerView.useController = true
        playerView.player = player
        playerView.setPlaybackPreparer(this)
        player!!.addVideoListener(object : VideoListener {
            override fun onVideoSizeChanged(
                width: Int,
                height: Int,
                unappliedRotationDegrees: Int,
                pixelWidthHeightRatio: Float
            ) {
                LogUtils.info(TAG, "onVideoSizeChanged:h" + height.toString() + " w:" + width.toString())
                if (currentStop>0){
                    player!!.seekTo(currentStop)
                }
            }

            override fun onRenderedFirstFrame() {
                LogUtils.info(TAG, "onRenderedFirstFrame")

            }

        })
        var loopingSource = LoopingMediaSource(videoSource);
        player!!.prepare(videoSource)
//        if (currentStop>0){
//            player!!.seekTo(currentStop)
//        }
        player!!.setPlayWhenReady(true)
    }
    override fun dialogName(): String {
        return "FullScreenDialog"
    }
    fun releasePlayer() {
        if (player != null) {
            player!!.release()
            player = null
        }
    }
    override fun styleDialog(): Int {
        return android.R.style.Theme_Black_NoTitleBar_Fullscreen
    }

    override fun dismiss() {
        super.dismiss()
        playerView.player = null
        listener.onCloseFullScreen(player!!.currentPosition)
        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
    override fun layoutFileCommon(): Int {
        return R.layout.dialog_full_creen
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }
    override fun onResume() {
        super.onResume()

        if (Util.SDK_INT <= 23 || player == null) initializePlayer()
    }

    override fun onPause() {
        super.onPause()

        if (Util.SDK_INT <= 23) releasePlayer()
    }

    override fun onStop() {
        super.onStop()

        if (Util.SDK_INT > 23) releasePlayer()
    }
    interface FullScreenListener{
        fun onCloseFullScreen(currentStop: Long)
    }
}

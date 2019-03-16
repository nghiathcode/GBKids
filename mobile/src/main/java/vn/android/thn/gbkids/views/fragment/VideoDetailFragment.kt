package vn.android.thn.gbkids.views.fragment

import android.net.Uri
import android.os.Handler
import android.view.View
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.decoder.DecoderCounters
import com.google.android.exoplayer2.drm.FrameworkMediaDrm
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector

import com.google.android.exoplayer2.trackselection.TrackSelection

import com.google.android.exoplayer2.source.MediaSource
import vn.android.thn.gbfilm.views.listener.YoutubeStreamListener
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.ui.DebugTextViewHelper
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource

import vn.android.thn.gbfilm.views.dialogs.YoutubeDialog
import vn.android.thn.gbkids.R

import vn.android.thn.gbkids.utils.LogUtils
import java.io.IOException
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import vn.android.thn.library.utils.GBUtils


//
// Created by NghiaTH on 2/27/19.
// Copyright (c) 2019

class VideoDetailFragment : BaseFragment(), YoutubeStreamListener, PlaybackPreparer,
    PlayerControlView.VisibilityListener {
    override fun preparePlayback() {

    }

    override fun onVisibilityChange(visibility: Int) {

    }

    override fun onStartStream() {
        viewManager.showDialog(YoutubeDialog.newInstance())
    }

    override fun onStreamError() {
        viewManager.hideDialog()
    }

    override fun onStream(list_stream: ArrayList<String>) {
        viewManager.hideDialog()
//        if (list_stream.size>0)
        myStream = list_stream.get(0)
        play(list_stream.get(0))
    }

    var videoId = ""
    lateinit var playerView: PlayerView
    lateinit var dataSourceFactory: DataSource.Factory
    var player: SimpleExoPlayer? = null
    lateinit var mediaDrm: FrameworkMediaDrm
    lateinit var mediaSource: MediaSource
    var trackSelector: DefaultTrackSelector? = null
    lateinit var trackSelectorParameters: DefaultTrackSelector.Parameters
    lateinit var debugViewHelper: DebugTextViewHelper
    lateinit var lastSeenTrackGroupArray: TrackGroupArray
    var myStream = ""
    override fun getTitle(): String {
        return "detail"
    }

    override fun fragmentName(): String {
        return "VideoDetailFragment"
    }

    override fun firstInit() {
        app.mYoutubeStreamListener = this
        val bandwidthMeter = DefaultBandwidthMeter()
        var videoTrackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
    }
    override fun initView() {
        playerView =  findViewById(R.id.player_view)!!
        val h: Int = playerView.getResources().getConfiguration().screenHeightDp
        val w = playerView.getResources().getConfiguration().screenWidthDp
        LogUtils.info(fragmentName() + "VideoPaler:", "height : " + h + " weight: " + w)
    }

    fun initializePlayer(streamVideo: String) {
        val mp4VideoUri = Uri.parse(streamVideo)
        val dataSourceFactory = DefaultDataSourceFactory(activity, Util.getUserAgent(activity, "gbkids"))
        val videoSource = ExtractorMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mp4VideoUri)

//            var videoSource = HlsMediaSource(mp4VideoUri, dataSourceFactory, 1, null, null);
        if (!GBUtils.isEmpty(streamVideo))
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(activity, trackSelector);

            playerView.useController = true
            playerView.player = player
            var loopingSource = LoopingMediaSource(videoSource);
            player!!.prepare(videoSource)
            player!!.setPlayWhenReady(true)
        } else {
            player!!.prepare(videoSource)
            player!!.setPlayWhenReady(true)
        }
    }

    override fun loadData() {
        app.loadStream(videoId)
    }



    override fun layoutFileResourceContent(): Int {
        return R.layout.fragment_video_detail
    }

    fun play(streamVideo: String) {
        initializePlayer(streamVideo)
    }

    private fun releasePlayer() {
        if (player != null) {
//            updateStartPosition()
//            shouldAutoPlay = player!!.playWhenReady
            player!!.release()
            player = null
            trackSelector = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }
    public override fun onResume() {
        super.onResume()

        if (Util.SDK_INT <= 23 || player == null) initializePlayer(myStream)
    }

    public override fun onPause() {
        super.onPause()

        if (Util.SDK_INT <= 23) releasePlayer()
    }

    public override fun onStop() {
        super.onStop()

        if (Util.SDK_INT > 23) releasePlayer()
    }
}

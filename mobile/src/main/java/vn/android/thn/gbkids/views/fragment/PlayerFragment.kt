package vn.android.thn.gbkids.views.fragment


import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.*

import com.facebook.ads.*
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource

import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoListener
import vn.android.thn.gbfilm.views.listener.YoutubeStreamListener
import vn.android.thn.gbkids.App
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.constants.Constants
import vn.android.thn.gbkids.model.db.VideoDownLoad
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbkids.model.entity.StreamEntity
import vn.android.thn.gbkids.utils.LogUtils
import vn.android.thn.gbkids.utils.Utils
import vn.android.thn.gbkids.views.activity.MainActivity
import vn.android.thn.gbkids.views.dialogs.FullScreenDialog
import vn.android.thn.gbkids.views.services.VideoDeleteService
import vn.android.thn.gbkids.views.services.YoutubeStreamService
import vn.android.thn.gbkids.views.view.ImageLoader
import vn.android.thn.library.utils.GBUtils


//
// Created by NghiaTH on 3/19/19.
// Copyright (c) 2019

class PlayerFragment : Fragment(), PlaybackPreparer,
    PlayerControlView.VisibilityListener, ViewTreeObserver.OnGlobalLayoutListener,
    FullScreenDialog.FullScreenListener, YoutubeStreamListener {
    var density = -1
    var playLocal = false
    override fun onStartStream() {
        video_error.visibility = View.GONE
        playerView.visibility = View.VISIBLE
        video_loading.visibility = View.VISIBLE
    }

    override fun onStream(list_stream: ArrayList<StreamEntity>) {
        video_error.visibility = View.GONE
        playerView.visibility = View.VISIBLE
        video_loading.visibility = View.GONE

        if (list_stream.size>0){
            for (stream in list_stream){
                if (stream.quality == density){
                    LogUtils.info("Play_URL_density:",stream.quality.toString())
                    play(stream.url)
                    return
                }
            }
            var obj_steam = StreamEntity("",-1)
            for (stream in list_stream){
                if (stream.quality!= -1 && stream.quality < density){
                    if(obj_steam.quality < stream.quality) {
                        obj_steam = stream
                    }
                }
            }
            LogUtils.info("Play_URL:",obj_steam.quality.toString())
            if (videoPlay!= null){
                if (videoPlay is VideoTable){
                    (videoPlay as VideoTable).save()
                }

            }
            play(obj_steam.url)

        }

    }

    override fun onStreamError() {
        playerView.visibility = View.INVISIBLE
        video_loading.visibility = View.GONE
        video_error.visibility = View.VISIBLE
        var intentServer = Intent(activity, VideoDeleteService::class.java)
        intentServer.putExtra("videoId",videoIdCurrent)
        activity!!.startService(intentServer)

    }

    private var fb_NativeAd: InstreamVideoAdView? = null
    var currentStop: Long = 0
    private var mFullScreenDialog = FullScreenDialog()
    var myStream = ""
    val TAG = "PlayerFragment"
    lateinit var playerView: PlayerView
    var trackSelector: DefaultTrackSelector? = null
    var player: SimpleExoPlayer? = null
    lateinit var video_loading: View
    lateinit var exo_fullscreen_button: View
    lateinit var fragmentView:View
    lateinit var img_thumbnail: ImageView
    var isNewVideo = false
    var shouldAutoPlay = true
    lateinit var video_error:TextView
    var app = App.getInstance()
    var videoIdCurrent = ""
    lateinit var listener:PlayerListener
    var videoPlay:Any? = null
    lateinit var main_player_view:FrameLayout
    var initVideo = false
    var isLoadADComplete = false
    lateinit var adContainer:LinearLayout
    override fun preparePlayback() {
        LogUtils.info(TAG, "preparePlayback")
    }

    override fun onVisibilityChange(visibility: Int) {
        playerView.visibility = View.VISIBLE
        video_loading.visibility = View.GONE
        LogUtils.info(TAG, "onVisibilityChange")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bandwidthMeter = DefaultBandwidthMeter()
        var videoTrackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
    }

    //fb ad
    private fun pxToDP(px: Int): Int {
        return (px / this.resources.displayMetrics.density).toInt()
    }
    fun loadFBNativeAd() {
        fb_NativeAd =  InstreamVideoAdView(activity, getString(R.string.fb_ad_video),
            AdSize(
                pxToDP(adContainer.getMeasuredWidth()),
                pxToDP(adContainer.getMeasuredHeight())
            ))
        fb_NativeAd!!.setAdListener(object : InstreamVideoAdListener {

            override fun onAdVideoComplete(p0: Ad?) {
                LogUtils.error(TAG,"Instream video completed!")

                adContainer.removeAllViews();
                adContainer.setVisibility(View.GONE)
                playerView.visibility = View.VISIBLE
                initializePlayer(myStream)
                if (fb_NativeAd != null) {

                    fb_NativeAd!!.destroy();
                    fb_NativeAd = null
                }
            }
            override fun onAdClicked(p0: Ad?) {
                LogUtils.error(TAG, "Native ad clicked!")
                adContainer.removeAllViews();
                adContainer.setVisibility(View.GONE)
                playerView.visibility = View.VISIBLE
                initializePlayer(myStream)
                if (fb_NativeAd != null) {
                    fb_NativeAd!!.destroy();
                    fb_NativeAd = null
                }
            }



            override fun onError(ad: Ad?, adError: AdError?) {
                LogUtils.error(TAG, "Native ad failed to load: " + adError!!.getErrorMessage())
                playerView.visibility = View.VISIBLE

//                initializePlayer(myStream)
            }

            override fun onAdLoaded(ad: Ad?) {
                LogUtils.error(TAG, "Native ad is loaded and ready to be displayed!")
                if (fb_NativeAd == null || !fb_NativeAd!!.isAdLoaded) {
                    return
                }
                if (fb_NativeAd!!.isAdInvalidated()) {
                    return
                }
                releasePlayer()

                adContainer = fragmentView.findViewById(R.id.adContainer);
                adContainer.removeAllViews()
                adContainer.addView(fb_NativeAd)

                fb_NativeAd!!.show()
                playerView.visibility = View.GONE
                adContainer.visibility = View.VISIBLE
                // Inflate Native Ad into Container

            }

            override fun onLoggingImpression(p0: Ad?) {
                LogUtils.error(TAG, "Native ad impression logged!")


            }

        })
        // Request an ad
        fb_NativeAd!!.loadAd()
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_player, container, false)
        main_player_view = view.findViewById(R.id.main_player_view)
        mFullScreenDialog.listener = this
        playerView = view.findViewById(R.id.player_view)!!
        video_loading = view.findViewById(R.id.video_loading)
        video_error = view.findViewById(R.id.video_error)
        video_error.visibility = View.GONE
        val h: Int = playerView.getResources().getConfiguration().screenHeightDp
        val w = playerView.getResources().getConfiguration().screenWidthDp
//        LogUtils.info(fragmentName() + "VideoPaler:", "height : " + h + " weight: " + w)
        img_thumbnail = view.findViewById(R.id.img_thumbnail)
        exo_fullscreen_button = view.findViewById(R.id.exo_fullscreen_button)
        exo_fullscreen_button.setOnClickListener {
            activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            player!!.stop()
            currentStop = player!!.currentPosition
            mFullScreenDialog.listener = this
            mFullScreenDialog.trackSelector = trackSelector!!
            mFullScreenDialog.videoSource = videoSourceFullScreen
            mFullScreenDialog.currentStop = currentStop
            (activity as MainActivity).viewManager.showDialog(mFullScreenDialog)
        }
        adContainer = view.findViewById(R.id.adContainer)
        fragmentView = view!!

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        density = Utils.getScreen(activity!!)
        app.mYoutubeStreamListener = this

    }

    fun play(streamVideo: String) {
        myStream = streamVideo
        initializePlayer(streamVideo)
    }

    lateinit var videoSource: ExtractorMediaSource
    lateinit var videoSourceFullScreen: ExtractorMediaSource

    fun initializePlayer(streamVideo: String) {
        val mp4VideoUri = Uri.parse(streamVideo)
        val dataSourceFactory = DefaultDataSourceFactory(activity, Util.getUserAgent(activity, "gbkids"))
        videoSource = ExtractorMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mp4VideoUri)

        videoSourceFullScreen = ExtractorMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mp4VideoUri)
//            var videoSource = HlsMediaSource(mp4VideoUri, dataSourceFactory, 1, null, null);
        if (!GBUtils.isEmpty(streamVideo)) {
            if (player == null) {
                player = ExoPlayerFactory.newSimpleInstance(activity, trackSelector)
            }
            player!!.addVideoListener(object : VideoListener {
                override fun onVideoSizeChanged(
                    width: Int,
                    height: Int,
                    unappliedRotationDegrees: Int,
                    pixelWidthHeightRatio: Float
                ) {
                    LogUtils.info(TAG, "onVideoSizeChanged:h" + height.toString() + " w:" + width.toString())
//                    (activity as MainActivity).updateHeightVideoPlay(height)
                    if (currentStop>0){
                        player!!.seekTo(currentStop)
                    }
                }

                override fun onRenderedFirstFrame() {
                    LogUtils.info(TAG, "onRenderedFirstFrame")
                    playerView.visibility = View.VISIBLE
                    video_loading.visibility = View.GONE
                    initVideo = true
                    if (!isLoadADComplete) {
                        isLoadADComplete =true
                        loadFBNativeAd()
                    }
                }

            })
            player!!.addListener(object :Player.EventListener{
                override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
                    LogUtils.info(TAG, "onPlaybackParametersChanged")
                }

                override fun onSeekProcessed() {
                    LogUtils.info(TAG, "onSeekProcessed")
                }

                override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
                    LogUtils.info(TAG, "onTracksChanged")
                }

                override fun onPlayerError(error: ExoPlaybackException?) {
                    LogUtils.info(TAG, "onPlayerError")
                }

                override fun onLoadingChanged(isLoading: Boolean) {
                    LogUtils.info(TAG, "onLoadingChanged")
                    if (!isLoading && initVideo){
                        LogUtils.info(TAG, "onLoadingChanged:"+main_player_view.height)
                        Handler().postDelayed(object :Runnable{
                            override fun run() {
                                playerView.visibility = View.VISIBLE
                                video_loading.visibility = View.GONE
                                var params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,main_player_view.height)
                                main_player_view.setLayoutParams(params)
                                (activity as MainActivity).updateHeightVideoPlay(main_player_view.height)

                            }
                        },200)

                    }
                }

                override fun onPositionDiscontinuity(reason: Int) {
                    LogUtils.info(TAG, "onPositionDiscontinuity")
                }

                override fun onRepeatModeChanged(repeatMode: Int) {
                    LogUtils.info(TAG, "onRepeatModeChanged")
                }

                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                    LogUtils.info(TAG, "onShuffleModeEnabledChanged")
                }

                override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
                    LogUtils.info(TAG, "onTimelineChanged")
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    LogUtils.info(TAG, "onPlayerStateChanged")
                    if (playbackState ==Player.STATE_ENDED){
                        LogUtils.info(TAG, "onPlayerStateChanged:END VIDEO")
                        val obj = listener.nextVideo()
                        if (obj!= null){
                            playNewVideo(obj)
                        }

                    }
                    if (playbackState == Player.STATE_READY){
                        initVideo = true
                    }
                }

            })
            playerView.useController = true
            playerView.player = player
            playerView.setPlaybackPreparer(this)
            player!!.prepare(videoSource)
            player!!.setPlayWhenReady(shouldAutoPlay)
            playerView.visibility = View.VISIBLE
            video_loading.visibility = View.VISIBLE
        }
    }



    /**
     * playVideoWhenExitFullScreen
     */
    fun playVideoWhenExitFullScreen(){
        player!!.prepare(this.videoSource)
        player!!.setPlayWhenReady(true)
    }
    /**
     * playNewVideo
     */
    fun playNewVideo(obj:VideoTable){
        initVideo = false
        isLoadADComplete = false
        app.mYoutubeStreamListener = this
        this.playLocal = false
        closeVideo()
        videoPlay = obj
        myStream = ""
        isNewVideo = true
        shouldAutoPlay = true
        currentStop = 0
        videoIdCurrent = obj.videoID
        video_error.visibility = View.GONE
        playerView.visibility = View.GONE
        video_loading.visibility = View.VISIBLE
        if (App.getInstance().appStatus == 1){
            if (GBUtils.isEmpty(obj.urlImage)){
                var thumbnails =obj.toImage()
                if (thumbnails!= null) {
                    if (thumbnails.maxres != null) {
                        obj.urlImage = thumbnails!!.maxres!!.url
                    } else if (thumbnails.high != null) {
                        obj.urlImage = thumbnails!!.high!!.url
                    } else if (thumbnails.medium != null) {
                        obj.urlImage =  thumbnails!!.medium!!.url
                    } else if (thumbnails.standard != null) {
                        obj.urlImage = thumbnails!!.standard!!.url
                    } else if (thumbnails.default != null) {
                        obj.urlImage = thumbnails!!.default!!.url
                    }
                }
            }
            ImageLoader.loadImagePlay(img_thumbnail, obj.urlImage,obj.videoID)
        } else {
            ImageLoader.loadImagePlay(img_thumbnail, Constants.DOMAIN + "/thumbnail_high/" + obj.videoID,obj.videoID)
        }

        activity!!.stopService(Intent(activity, YoutubeStreamService::class.java))
        var intentServer = Intent(activity, YoutubeStreamService::class.java)
        intentServer.putExtra("videoId",obj.videoID)
        activity!!.startService(intentServer)
    }
    fun playVideoLocal(obj:VideoDownLoad){
        app.mYoutubeStreamListener = this
        this.playLocal = true
        closeVideo()
        videoPlay = obj
        myStream = ""
        isNewVideo = true
        shouldAutoPlay = true
        currentStop = 0
        videoIdCurrent = obj.videoID
        video_error.visibility = View.GONE
        playerView.visibility = View.VISIBLE
        video_loading.visibility = View.VISIBLE
        ImageLoader.loadImagePlay(img_thumbnail, obj.thumbnails,obj.videoID)

        play(obj.videoPath)
    }
    fun closeVideo(){
        releasePlayer()
        adContainer.removeAllViews();
        adContainer.setVisibility(View.GONE)
        playerView.visibility = View.VISIBLE
        myStream = ""
        isNewVideo = true
        shouldAutoPlay = true
        currentStop = 0
        playerView.visibility = View.VISIBLE
        video_loading.visibility = View.VISIBLE
    }
    fun releasePlayer() {
        if (player != null) {
            currentStop = player!!.currentPosition
//            updateStartPosition()
            shouldAutoPlay = player!!.playWhenReady
            player!!.release()
            player = null
//            trackSelector = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (fb_NativeAd != null) {
            fb_NativeAd!!.destroy();
            fb_NativeAd = null
        }
        releasePlayer()
    }

    override fun onResume() {
        super.onResume()

        if (Util.SDK_INT <= 23 || player == null) initializePlayer(myStream)
    }

    override fun onPause() {
        super.onPause()

        if (Util.SDK_INT <= 23) releasePlayer()
    }

    override fun onStop() {
        super.onStop()

        if (Util.SDK_INT > 23) releasePlayer()
    }
    override fun onCloseFullScreen(currentStop: Long) {
        this.currentStop = currentStop

        playVideoWhenExitFullScreen()
    }

    override fun onGlobalLayout() {
        if (img_thumbnail.measuredHeight > 0) {
            img_thumbnail.getViewTreeObserver().removeGlobalOnLayoutListener(this)
            LogUtils.info(TAG, "onGlobalLayout:" + img_thumbnail.measuredHeight.toString())
        }
    }
    interface PlayerListener{
        fun nextVideo():VideoTable?
    }
}

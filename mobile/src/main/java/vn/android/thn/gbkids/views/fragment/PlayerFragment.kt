package vn.android.thn.gbkids.views.fragment

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.DisplayMetrics
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.PlaybackPreparer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoListener
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.constants.Constants
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbkids.utils.LogUtils
import vn.android.thn.gbkids.utils.Utils
import vn.android.thn.gbkids.views.activity.MainActivity
import vn.android.thn.gbkids.views.dialogs.FullScreenDialog
import vn.android.thn.gbkids.views.view.ImageLoader
import vn.android.thn.library.utils.GBUtils


//
// Created by NghiaTH on 3/19/19.
// Copyright (c) 2019

class PlayerFragment : Fragment(), PlaybackPreparer,
    PlayerControlView.VisibilityListener, ViewTreeObserver.OnGlobalLayoutListener,
    FullScreenDialog.FullScreenListener {


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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_player, container, false)
        mFullScreenDialog.listener = this
        playerView = view.findViewById(R.id.player_view)!!
        video_loading = view.findViewById(R.id.video_loading)
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
        fragmentView = view!!
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        app.mYoutubeStreamListener = this

    }

    private fun getYoutubeDownloadUrl(youtubeLink: String) {
        object : YouTubeExtractor(activity!!) {

            public override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta) {
//                mainProgressBar.setVisibility(View.GONE)

                if (ytFiles == null) {
                    // Something went wrong we got no urls. Always check this.
//                    finish()
                    return
                }
                // Iterate over itags
                var i = 0
                var itag: Int
                var lstUrl: MutableList<YtFile> = ArrayList<YtFile>()
                var screenUrl = HashMap<Int,String>()
                var firstLoadURL = false
                var density = Utils.getScreen(activity!!)
                while (i < ytFiles.size()) {
                    itag = ytFiles.keyAt(i)
                    // ytFile represents one file with its url and meta data
                    val ytFile = ytFiles.get(itag)

                    // Just add videos in a decent format => height -1 = audio
                    if (ytFile.format.height == -1 || ytFile.format.height >= 360) {
//                        addButtonToMainLayout(vMeta.title, ytFile)
                        LogUtils.info("URL_STREAM_" + ytFile.format.height + ":", ytFile.url)
                        LogUtils.info("Screen:",density.toString())
                        lstUrl.add(ytFile)
                        if (!screenUrl.containsKey(ytFile.format.height)) {
                            screenUrl.put(ytFile.format.height, ytFile.url)
                        }
                        if (!firstLoadURL) {

                            firstLoadURL = true
                            play(ytFile.url)
                        }

                    }
                    i++
                }
//                if (screenUrl.containsKey(density)){
//                    play(screenUrl.get(density)!!)
//                } else {
//                    for ((key,url) in screenUrl ){
//                        if (key < density){
//                            play(url)
//                            return
//                        }
//                    }
//                }
            }
        }.extract(youtubeLink, true, false)
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

    fun loadVideo(videoId: String) {
        playerView.visibility = View.VISIBLE
        video_loading.visibility = View.VISIBLE
        ImageLoader.loadImage(img_thumbnail, Constants.DOMAIN + "/thumbnail_high/" + videoId,videoId)
        getYoutubeDownloadUrl("https://www.youtube.com/watch?v=" + videoId)
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
    fun playNewVideo(video:VideoTable){
        myStream = ""
        isNewVideo = true
        shouldAutoPlay = true
        currentStop = 0
        playerView.visibility = View.VISIBLE
        video_loading.visibility = View.VISIBLE
        ImageLoader.loadImage(img_thumbnail, Constants.DOMAIN + "/thumbnail_high/" + video.videoID,video.videoID)
        closeVideo()
        getYoutubeDownloadUrl("https://www.youtube.com/watch?v=" + video.videoID)
    }
    fun closeVideo(){

        myStream = ""
        isNewVideo = true
        shouldAutoPlay = true
        currentStop = 0
        playerView.visibility = View.VISIBLE
        video_loading.visibility = View.VISIBLE
        releasePlayer()
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

}

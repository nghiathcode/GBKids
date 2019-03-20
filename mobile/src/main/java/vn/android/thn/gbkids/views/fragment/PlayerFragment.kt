package vn.android.thn.gbkids.views.fragment

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
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
import vn.android.thn.gbkids.utils.LogUtils
import vn.android.thn.gbkids.views.activity.MainActivity
import vn.android.thn.gbkids.views.view.ImageLoader
import vn.android.thn.library.utils.GBUtils


//
// Created by NghiaTH on 3/19/19.
// Copyright (c) 2019

class PlayerFragment:Fragment(), PlaybackPreparer,
    PlayerControlView.VisibilityListener ,ViewTreeObserver.OnGlobalLayoutListener {
    override fun onGlobalLayout() {
        if(img_thumbnail.measuredHeight>0) {
            img_thumbnail.getViewTreeObserver().removeGlobalOnLayoutListener(this)
//            (activity as MainActivity).updateHeightVideoPlay(img_thumbnail.measuredHeight)
            LogUtils.info(TAG,"onGlobalLayout:"+img_thumbnail.measuredHeight.toString())
        }
    }

    var myStream = ""
    val TAG = "PlayerFragment"
    lateinit var playerView: PlayerView
    var trackSelector: DefaultTrackSelector? = null
    var player: SimpleExoPlayer? = null
    lateinit var video_loading:View
    lateinit var img_thumbnail:ImageView
    override fun preparePlayback() {
        LogUtils.info(TAG,"preparePlayback")
    }

    override fun onVisibilityChange(visibility: Int) {
        playerView.visibility = View.VISIBLE
        video_loading.visibility = View.GONE
        LogUtils.info(TAG,"onVisibilityChange")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bandwidthMeter = DefaultBandwidthMeter()
        var videoTrackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =inflater.inflate(R.layout.fragment_player,container,false)
        playerView =  view.findViewById(R.id.player_view)!!
        video_loading = view.findViewById(R.id.video_loading)
        val h: Int = playerView.getResources().getConfiguration().screenHeightDp
        val w = playerView.getResources().getConfiguration().screenWidthDp
//        LogUtils.info(fragmentName() + "VideoPaler:", "height : " + h + " weight: " + w)
        img_thumbnail = view.findViewById(R.id.img_thumbnail)

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
                var lstUrl : MutableList<String> = ArrayList<String>()
                var firstLoadURL = false
                while (i < ytFiles.size()) {
                    itag = ytFiles.keyAt(i)
                    // ytFile represents one file with its url and meta data
                    val ytFile = ytFiles.get(itag)

                    // Just add videos in a decent format => height -1 = audio
                    if (ytFile.format.height == -1 || ytFile.format.height >= 360) {
//                        addButtonToMainLayout(vMeta.title, ytFile)
                        LogUtils.info("URL_STREAM_"+ytFile.format.height+":",ytFile.url)
                        lstUrl.add(ytFile.url)

                        if(!firstLoadURL){
                            firstLoadURL = true
                            play(ytFile.url)
                        }

                    }
                    i++
                }

            }
        }.extract(youtubeLink, true, false)
    }
    fun play(streamVideo: String) {
         myStream = streamVideo

        initializePlayer(streamVideo)
    }
    fun initializePlayer(streamVideo: String) {
        val mp4VideoUri = Uri.parse(streamVideo)
        val dataSourceFactory = DefaultDataSourceFactory(activity, Util.getUserAgent(activity, "gbkids"))
        val videoSource = ExtractorMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mp4VideoUri)

//            var videoSource = HlsMediaSource(mp4VideoUri, dataSourceFactory, 1, null, null);
        if (!GBUtils.isEmpty(streamVideo)) {
            if (player == null) {
                player = ExoPlayerFactory.newSimpleInstance(activity, trackSelector);
                player!!.addVideoListener(object :VideoListener{
                    override fun onVideoSizeChanged(
                        width: Int,
                        height: Int,
                        unappliedRotationDegrees: Int,
                        pixelWidthHeightRatio: Float
                    ) {
                        LogUtils.info(TAG,"onVideoSizeChanged:h"+height.toString()+" w:"+width.toString())
                        (activity as MainActivity).updateHeightVideoPlay(height)
                    }

                    override fun onRenderedFirstFrame() {
                        LogUtils.info(TAG,"onRenderedFirstFrame")
                        playerView.visibility = View.VISIBLE
                        video_loading.visibility = View.GONE
                    }

                })
                playerView.useController = true
                playerView.player = player
                playerView.setPlaybackPreparer(this)
                var loopingSource = LoopingMediaSource(videoSource);
                player!!.prepare(videoSource)
                player!!.setPlayWhenReady(true)
            } else {
                player!!.prepare(videoSource)
                player!!.setPlayWhenReady(true)
            }
            playerView.visibility = View.VISIBLE
            video_loading.visibility = View.VISIBLE
        }
    }
    fun loadVideo(videoId:String){
        playerView.visibility = View.VISIBLE
        video_loading.visibility = View.VISIBLE


//        img_thumbnail.viewTreeObserver.addOnGlobalLayoutListener( this)
        ImageLoader.loadImage(img_thumbnail, Constants.DOMAIN+"/thumbnail_high/"+videoId)

        getYoutubeDownloadUrl("https://www.youtube.com/watch?v="+videoId)
    }

    fun releasePlayer() {
        if (player != null) {
//            updateStartPosition()
//            shouldAutoPlay = player!!.playWhenReady
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
}

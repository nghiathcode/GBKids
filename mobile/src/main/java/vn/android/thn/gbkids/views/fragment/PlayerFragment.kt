package vn.android.thn.gbkids.views.fragment

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoListener
import vn.android.thn.commons.App
import vn.android.thn.commons.listener.YoutubeStreamListener
import vn.android.thn.commons.realm.RealmVideo
import vn.android.thn.commons.service.YoutubeStreamService
import vn.android.thn.commons.view.ImageLoader
import vn.android.thn.gbfilm.views.dialogs.FullScreenDialog
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.views.activity.MainActivity
import vn.android.thn.library.utils.GBLog
import vn.android.thn.library.utils.GBUtils
import java.util.*
import kotlin.collections.ArrayList

class PlayerFragment:Fragment(),PlaybackPreparer , YoutubeStreamListener,FullScreenDialog.FullScreenListener ,VideoListener,Player.EventListener{
    companion object{
        var dataSourceMap = Hashtable<String,ExtractorMediaSource>()//<videoId,ExtractorMediaSource>
        var linkSourceMap = Hashtable<String,String>()
    }
    //Player.EventListener
    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
        GBLog.info("PlayerFragment","onPlaybackParametersChanged:",mMainActivity!!.isDebugMode())
    }

    override fun onSeekProcessed() {
        GBLog.info("PlayerFragment","onSeekProcessed:",mMainActivity!!.isDebugMode())
        currentStop = app.player.currentPosition
    }

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
        GBLog.info("PlayerFragment","onTracksChanged:",mMainActivity!!.isDebugMode())
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        GBLog.info("PlayerFragment","onPlayerError:",mMainActivity!!.isDebugMode())
        if (fullScreen == 1){
            mFullScreenDialog.onError()
        }
        RealmVideo.resetLink(video!!.videoID,GBUtils.dateNow("yyyyMMdd"))
        linkSourceMap.remove(GBUtils.dateNow("yyyyMMdd")+this.video!!.videoID)
        dataSourceMap.remove(GBUtils.dateNow("yyyyMMdd")+this.video!!.videoID)
        activity!!.stopService(Intent(activity, YoutubeStreamService::class.java))
        var intentServer = Intent(activity, YoutubeStreamService::class.java)
        intentServer.putExtra("videoId",video!!.videoID)
        activity!!.startService(intentServer)
    }

    override fun onLoadingChanged(isLoading: Boolean) {
        GBLog.info("PlayerFragment","onLoadingChanged:"+isLoading.toString(),mMainActivity!!.isDebugMode())
        if (!isLoading && fullScreen ==0){
            img_thumbnail_video.visibility = View.GONE
            stream_loading.visibility = View.GONE
        }
    }

    override fun onPositionDiscontinuity(reason: Int) {
        GBLog.info("PlayerFragment","onPositionDiscontinuity:"+reason,mMainActivity!!.isDebugMode())
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        GBLog.info("PlayerFragment","onRepeatModeChanged:",mMainActivity!!.isDebugMode())
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        GBLog.info("PlayerFragment","onShuffleModeEnabledChanged:",mMainActivity!!.isDebugMode())
    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
        GBLog.info("PlayerFragment","onTimelineChanged:",mMainActivity!!.isDebugMode())
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        GBLog.info("PlayerFragment","onPlayerStateChanged:",mMainActivity!!.isDebugMode())
        when(playbackState){
            Player.STATE_ENDED->{
                GBLog.info("PlayerFragment","onPlayerStateChanged:STATE_ENDED",mMainActivity!!.isDebugMode())
                if (currentItem>=listVideo.size){
                    currentItem = listVideo.size-1
                }
                app.player.setPlayWhenReady(false)
                if (currentItem<0){
                    currentItem = 0
                }
                if (listVideo.size>currentItem) {
                    playVideo(listVideo.get(currentItem))
                    currentItem=currentItem+1
                }

            }
            Player.STATE_READY->{
                GBLog.info("PlayerFragment","onPlayerStateChanged:STATE_READY:"+playerView.height,mMainActivity!!.isDebugMode())

                if (fullScreen == 1){
                    mFullScreenDialog.onLoading(true)
                } else{
                    endLoading()
                    playerView.visibility = View.VISIBLE
                    if (isFirstFrame)
                    stream_loading.visibility = View.GONE
                }
            }
            Player.STATE_BUFFERING->{
                GBLog.info("PlayerFragment","onPlayerStateChanged:STATE_BUFFERING",mMainActivity!!.isDebugMode())

                if (fullScreen == 1){
                    mFullScreenDialog.onLoading(false)
                } else {
                    stream_loading.visibility = View.VISIBLE

                }
            }
            Player.STATE_IDLE->{
                stream_loading.visibility = View.GONE
                GBLog.info("PlayerFragment","onPlayerStateChanged:STATE_IDLE",mMainActivity!!.isDebugMode())
            }

        }

    }


    //VideoListener
    override fun onVideoSizeChanged(
        width: Int,
        height: Int,
        unappliedRotationDegrees: Int,
        pixelWidthHeightRatio: Float
    ) {
        GBLog.info("PlayerFragment","onVideoSizeChanged:height"+height+",width:"+width,mMainActivity!!.isDebugMode())
        endLoading()
    }

    override fun onRenderedFirstFrame() {
        GBLog.info("PlayerFragment","onRenderedFirstFrame:height"+playerView.height,mMainActivity!!.isDebugMode())
        isFirstFrame = true
        if (mMainActivity!= null){
            mMainActivity!!.updateHeightVideoPlay(playerView.height)
        }
        playerView.visibility = View.VISIBLE
        video_error.visibility = View.GONE
        img_thumbnail_video.visibility = View.GONE
        stream_loading.visibility = View.GONE
    }


    //FullScreenListener
    override fun onCloseFullScreen(currentStop: Long) {
        playVideoWhenExitFullScreen()
    }
    //YoutubeStreamListener
    override fun onStartStream() {
        stream_loading.visibility = View.VISIBLE
        initLoading(video!!)
    }

    override fun onNoInternet() {
        endLoading()
        if (activity is MainActivity) {
             (activity as MainActivity).onNoInternet()
        }
    }
    override fun onStream(videoId:String,stream_video: String) {
        if (videoId.equals(video!!.videoID,true))
        myStream = stream_video
        if (mMainActivity!= null) {
            app.player.prepare(app.videoSource)

        }
        initializePlayer(myStream)
    }

    override fun onStreamError() {
        endLoading()
        img_thumbnail_video.visibility = View.GONE
        stream_loading.visibility = View.GONE
        video_error.visibility = View.VISIBLE
        playerView.visibility = View.GONE

    }
    var isFirstFrame = false
    var app = App.getInstance()
    var video: RealmVideo? = null
    var mMainActivity :MainActivity?=null
    lateinit var playerView: PlayerView
    var shouldAutoPlay = true
    var currentStop: Long = 0
    var myStream = ""
    lateinit var exo_fullscreen_button: View
    lateinit var stream_loading:View
    lateinit var img_thumbnail_video:ImageView
    lateinit var video_error:TextView
    lateinit var main_player_view: FrameLayout
    private var mFullScreenDialog = FullScreenDialog()
    var fullScreen = 0
    lateinit var exo_progress: DefaultTimeBar
    var listVideo = ArrayList<RealmVideo>()
    lateinit var next_button:View
    lateinit var prev_button:View
    var mListVideoPlayFragment:ListVideoPlayFragment? = null
    var currentItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app.player!!.addVideoListener(this)
        app.player!!.addListener(this)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_player, container, false)
        playerView = view.findViewById(R.id.player_view)!!

        stream_loading = view.findViewById(R.id.stream_loading)
        img_thumbnail_video = view.findViewById(R.id.img_thumbnail_video)
        video_error = view.findViewById(R.id.video_error)
        main_player_view = view.findViewById(R.id.main_player_view)
        exo_fullscreen_button = view.findViewById(R.id.exo_fullscreen_button)
        exo_progress =  view.findViewById(R.id.exo_progress)

        next_button = view.findViewById(R.id.play_next)
        prev_button = view.findViewById(R.id.play_prev)
        prev_button.setOnClickListener {
            currentItem = currentItem -1
            if (currentItem<=0){
                currentItem = 0
            }
            app.player.setPlayWhenReady(false)
            playVideo(listVideo.get(currentItem))
        }
        next_button.setOnClickListener {

            if (currentItem>=listVideo.size){
                currentItem = listVideo.size-1
            }
            app.player.setPlayWhenReady(false)
            if (currentItem<0){
                currentItem = 0
            }
            playVideo(listVideo.get(currentItem))
            currentItem=currentItem+1
        }
        App.getInstance().mYoutubeStreamListener = this
        if (activity is MainActivity) {
            mMainActivity = activity as MainActivity
            exo_fullscreen_button.setOnClickListener {
                activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                mFullScreenDialog.listener = this
                mFullScreenDialog.player = app.player
                mFullScreenDialog.mEventListener = this
                mFullScreenDialog.mVideoListener = this
                playerView.player = null
                app.player.setPlayWhenReady(false)
                fullScreen = 1
                (activity as MainActivity).viewManager.showDialog(mFullScreenDialog)
            }
        }
        return view
    }
    fun showController(isShow:Boolean = true){
        if (playerView!= null) {
            playerView.useController = isShow
        }
    }
    fun playVideo(video:RealmVideo){
        isFirstFrame = false
        if (this.video!= null){
            linkSourceMap.remove(GBUtils.dateNow("yyyyMMdd")+this.video!!.videoID)
            dataSourceMap.remove(GBUtils.dateNow("yyyyMMdd")+this.video!!.videoID)
            app.player.playWhenReady = false
            RealmVideo.updateTime(this.video!!.videoID,app.player.duration,app.player.currentPosition)
        }
        if (mListVideoPlayFragment!=null){
            mListVideoPlayFragment!!.mPlayerFragment = this
            mListVideoPlayFragment!!.presenter!!.videoCurrent = video
            mListVideoPlayFragment!!.loadNewList()
        }

        this.video = video
        if (this.video!!.videoTimeCurrent>0 && GBUtils.dateNow("yyyyMMdd").equals(this.video!!.expiredVideo,true)){
            currentStop = this.video!!.videoTimeCurrent
            var time_haft = this.video!!.videoTime/2
            if (currentStop>time_haft)currentStop=0
            if (currentStop<0) currentStop=0
            if (time_haft<0) currentStop=0
        } else{
            currentStop = 0
        }

        shouldAutoPlay = true
        try {
            var intentServer = Intent(activity, YoutubeStreamService::class.java)
            intentServer.putExtra("videoId", video.videoID)
            activity!!.startService(intentServer)
        }catch (e:Exception){

        }
    }
    fun initLoading(video:RealmVideo){
        video_error.visibility = View.GONE
        playerView.visibility = View.INVISIBLE
        img_thumbnail_video.visibility = View.VISIBLE
        ImageLoader.loadImagePlay(img_thumbnail_video,video.imageLarger,video.videoID)

    }
    fun endLoading(){
        var params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,img_thumbnail_video.height)
        main_player_view.setLayoutParams(params)
        playerView.visibility = View.VISIBLE
        video_error.visibility = View.GONE
    }
    fun initializePlayer(streamVideo: String) {

        if (listVideo.size==0){
            next_button.visibility = View.GONE
            prev_button.visibility = View.GONE
        }else {
            next_button.visibility = View.VISIBLE
            prev_button.visibility = View.VISIBLE
        }

        if (GBUtils.isEmpty(streamVideo)) return
        if (fullScreen == 1){
            app.player.setPlayWhenReady(shouldAutoPlay)
            return
        }
        if (mMainActivity!= null){
            playerView.useController = true

            if (app.videoSource!= null){
                if (playerView.player==null){
                    playerView.player = app.player
                }
            }
            playerView.setPlaybackPreparer(this)
            if (currentStop > 0) {
                app.player.seekTo(currentStop)
            }else{
                app.player.seekTo(currentStop)
            }

            app.player.setPlayWhenReady(shouldAutoPlay)
        }
    }
    fun loadList(){
        if (listVideo.size==0){
            next_button.visibility = View.GONE
            prev_button.visibility = View.GONE
        }else {
            next_button.visibility = View.VISIBLE
            prev_button.visibility = View.VISIBLE
        }

    }
    fun releasePlayer() {
        if (app.player != null) {
            if (app.player.playWhenReady) {
                currentStop = app.player.currentPosition
                GBLog.info("PlayerFragment_State","releasePlayer:"+app.player.currentPosition,mMainActivity!!.isDebugMode())
                shouldAutoPlay = app.player.playWhenReady
            } else {
                shouldAutoPlay = false
            }
            app.player.setPlayWhenReady(false)
        }
        if (this.video!= null){
            RealmVideo.updateTime(this.video!!.videoID,app.player.duration,app.player.currentPosition)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        GBLog.info("PlayerFragment_State","onDestroy:",mMainActivity!!.isDebugMode())
        releasePlayer()
    }

    override fun onPause() {
        super.onPause()
        GBLog.info("PlayerFragment_State","onPause:",mMainActivity!!.isDebugMode())
        if (Util.SDK_INT <= 23) releasePlayer()
    }

    override fun onResume() {
        super.onResume()
        GBLog.info("PlayerFragment_State","onResume:",mMainActivity!!.isDebugMode())
//        if (Util.SDK_INT <= 23 ){
//            initializePlayer(myStream)
//        } else{
//            app.player.setPlayWhenReady(shouldAutoPlay)
//        }
        if (!GBUtils.isEmpty(myStream))
        initializePlayer(myStream)
    }

    override fun onStop() {
        super.onStop()
        GBLog.info("PlayerFragment_State","onStop:",mMainActivity!!.isDebugMode())
        if (Util.SDK_INT > 23) releasePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        GBLog.info("PlayerFragment_State","onDestroyView:",mMainActivity!!.isDebugMode())
    }
    /**
     * playVideoWhenExitFullScreen
     */
    fun playVideoWhenExitFullScreen(){
        fullScreen = 0
        if (mMainActivity!= null) {
            playerView.player = app.player
            app.player.setPlayWhenReady(shouldAutoPlay)
        }
    }
    //PlaybackPreparer
    override fun preparePlayback() {
        GBLog.info("PlayerFragment","preparePlayback:",mMainActivity!!.isDebugMode())
    }
}
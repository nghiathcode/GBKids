package vn.android.thn.commons

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.webkit.WebView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.gson.GsonBuilder
import io.realm.Realm
import io.realm.RealmConfiguration
import vn.android.thn.commons.listener.DownloadListener
import vn.android.thn.commons.listener.FileDownloadListener
import vn.android.thn.commons.listener.YoutubeStreamListener
import vn.android.thn.commons.realm.RealmTableDateUpdate
import vn.android.thn.commons.realm.RealmVideo
import vn.android.thn.commons.response.GBTubeResponse
import vn.android.thn.commons.response.VideoResponse
import vn.android.thn.commons.service.*
import vn.android.thn.gbkids.BuildConfig
import vn.android.thn.gbkids.constants.Constants
import vn.android.thn.gbkids.model.SettingEntity
import vn.android.thn.gbkids.model.api.GBVideoRequestCallBack
import vn.android.thn.gbkids.model.realm.RealmSetting
import vn.android.thn.gbkids.views.fragment.PlayerFragment
import vn.android.thn.library.net.GBRequestError
import vn.android.thn.library.utils.GBLog
import vn.android.thn.library.utils.GBUtils
import java.lang.reflect.Modifier
import java.net.InetAddress

class App : Application() {
    val builder = GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
    val gson = builder.create()
    var mYoutubeStreamListener: YoutubeStreamListener? = null
    var videoCurrent: RealmVideo? = null
    var videoSource: ExtractorMediaSource? = null
    var videoSourceHls: HlsMediaSource? = null
    lateinit var player: SimpleExoPlayer
    var mDownloadListener: DownloadListener? = null
    var mFileDownloadListener: FileDownloadListener? = null
    var screenHeight = -1
    var screenWidth = -1
    var statusBarHeight = -1
    var bottomHeight = -1
    var isConnectInternet = true
    var downLoadAllow = 0
    fun heightRowLarger(): Int {
        var height = (screenHeight - statusBarHeight - bottomHeight) / 2
        if (height > 720){
            return 720
        } else {
            return height
        }
    }

    fun heightRowSmall(): Int {
        return (screenHeight - statusBarHeight - bottomHeight) / 5
    }

    companion object {
        lateinit var INSTANCE_: App
        fun getInstance() = INSTANCE_
        var sBrowserUserAgent: String? = null
        fun getBrowserUserAgent(): String {
            // get browser user agent
            if (sBrowserUserAgent == null) {
                var web = WebView(INSTANCE_);
//                web.setWebChromeClient(WebChromeClient())
                sBrowserUserAgent = web.getSettings().getUserAgentString();
            }

            return sBrowserUserAgent!!
        }
    }

    init {
        INSTANCE_ = this
    }

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config =
            RealmConfiguration.Builder().name("top_kids.realm").schemaVersion(1L).deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config)
        val bandwidthMeter = DefaultBandwidthMeter()
        var videoTrackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        var trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        scanFileDowLoad()
    }

    fun scanFileDowLoad() {
        var intentServer = Intent(this, ScanFileDownLoadService::class.java)
        startService(intentServer)
    }

    fun initApp() {

        registerReceiver(youtubeStreamReceiver, IntentFilter(Constants.YOUTUBE_STREAM))
        registerReceiver(downloadReceiver, IntentFilter(Constants.DOWNLOAD_DATA))
        registerReceiver(fileDownLoadReceiver, IntentFilter(Constants.DOWNLOAD_VIDEO))
//        val filter = IntentFilter()
//
//        filter.addAction(ConnectivityManager.EXTRA_NO_CONNECTIVITY)
//        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
//        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
//        filter.addAction(BluetoothDevice.ACTION_FOUND)
//        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
//        registerReceiver(networkStateReceiver, filter)
        firstDownLoadData()
    }

    fun downloadVideo(videoId: String) {
        val intentService = Intent(this, DownLoadLocalVideoService::class.java)
        intentService.putExtra("videoId", videoId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this,intentService)
        } else {
            startService(intentService)
        }
    }

    fun loadFirstStream(videoID: String) {
        val item = RealmVideo.getObject(videoID)
        if (item != null) {
            if (item.isDownLoaded == 2) {
                val dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "gbkids"))
                var videoSource: ExtractorMediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(item!!.linkPlay))
                PlayerFragment.dataSourceMap.put(GBUtils.dateNow("yyyyMMdd") + item!!.videoID, videoSource)
                PlayerFragment.linkSourceMap.put(GBUtils.dateNow("yyyyMMdd") + item!!.videoID, item!!.linkPlay)
                return
            }
        }
        var intentServer = Intent(this, PlayStreamWorkerService::class.java)
        intentServer.putExtra("videoId", videoID)
        startService(intentServer)
    }

    fun getSettingApp(): SettingEntity {
        val realm = Realm.getDefaultInstance()
        var setting = realm.where(RealmSetting::class.java).equalTo("id", 1.toInt()).findFirst()

        if (setting != null) {
            return gson.fromJson<SettingEntity>(realm.copyFromRealm(setting).settingEntity, SettingEntity::class.java)
        } else {
            return SettingEntity()
        }
    }

    fun isDebugMode(): Boolean {

        if (BuildConfig.DEBUG) {
            return true
        }
        return false
    }

    private fun firstDownLoadData() {
        val api = GBVideoRequest(
            String.format(
                "execute_native_to_result?downloadKey=%s&isDebug=%s",
                getSettingApp().downloadKey,
                isDebugMode().toString()
            ), null
        )
        val queryobject = QueryNativeEntity()
        queryobject.queryNativeString =
            "select videos.* ,channels.imageUrl as channelImage from videos,channels where channels.channelId = videos.channelId and videos.appID = 'vn.thn.app.gbkids' order by dateUpdate DESC "
        queryobject.firstResult = 0
        queryobject.maxResults = 200
        api.queryobject = queryobject
        api.addHeader("appId", getSettingApp().appId)
        api.get().execute(object : GBVideoRequestCallBack {
            override fun onRequestError(errorRequest: GBRequestError, request: GBVideoRequest) {
                val sender = Intent()
                sender.action = Constants.DOWNLOAD_DATA
                sendBroadcast(sender)
            }

            override fun onResponse(httpCode: Int, response: GBTubeResponse, request: GBVideoRequest) {
                var videoResponse = response.toResponse(VideoResponse::class)
                if (videoResponse != null) {
                    GBRealm.insertList(videoResponse.listRealmVideo)
                    var dateDownLoad =
                        RealmTableDateUpdate.getObject("videos")
                    if (dateDownLoad == null) {
                        val maxDate = RealmVideo.maxDate()
                        dateDownLoad = RealmTableDateUpdate()
                        dateDownLoad!!.tableName = "videos"
                        dateDownLoad!!.dateUp = maxDate
                        GBRealm.save(dateDownLoad)
                    }
                    val sender = Intent()
                    sender.action = Constants.DOWNLOAD_DATA
                    sendBroadcast(sender)
                    val intentService = Intent(this@App, DownLoadVideoService::class.java)
                    startService(intentService)
                }
            }
        })
    }

    /**
     * getOsVersion
     */
    fun getOsVersion(): String {
        val release = Build.VERSION.RELEASE
        val sdkVersion = Build.VERSION.SDK_INT
        return "Android SDK: $sdkVersion ($release)"
    }

    /**
     * getVersionApp
     */
    fun getVersionApp(): String {
        try {
            return packageManager
                .getPackageInfo(packageName, 0).versionName
        } catch (e: Exception) {
            return ""
        }

    }

    fun getDeviceName(): String {
        var deviceName = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL
        return deviceName
    }

    fun getDeviceType(): String {
        return "android"
    }

    fun getAppId(): String {
        return "vn.thn.app.gbkids"
    }

    /**
     *getDeviceId
     */
    fun getDeviceId(): String {
        return Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    fun download() {
        val intentService = Intent(this@App, DownLoadVideoService::class.java)
        startService(intentService)
    }
    fun report(videoId:String,comment:String="",flagReport:Int = 1) {
        val intentService = Intent(this@App, ReportService::class.java)
        intentService.putExtra("videoId",videoId)
        intentService.putExtra("comment",comment)
        intentService.putExtra("flagReport",flagReport)
        startService(intentService)
    }

    fun isNetworkAvailable():Boolean{
        try {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.getActiveNetworkInfo()
            return  (netInfo != null && netInfo.isConnected())
        }catch (e:Exception){
            return false
        }
    }

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, initiatingIntent: Intent) {
            if (mDownloadListener != null) {
                mDownloadListener!!.onComplete()
            }
        }
    }
    private val youtubeStreamReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, initiatingIntent: Intent) {
            var action = initiatingIntent.getIntExtra("action", 0)
            if (action == 2) {
                if (mYoutubeStreamListener != null) {
                    GBLog.info("App:", "onNoInternet:", isDebugMode())
                    mYoutubeStreamListener!!.onNoInternet()
                }
                return
            }
            if (action == 1) {
                val stream_video = initiatingIntent.getStringExtra("stream_video")
                var videoId = initiatingIntent.getStringExtra("videoId")
                RealmVideo.updateWatched(videoId)
                if (mYoutubeStreamListener != null) {
                    if (!GBUtils.isEmpty(stream_video)) {
                        GBLog.info("App:", "mYoutubeStreamListener:" + stream_video, isDebugMode())
                        mYoutubeStreamListener!!.onStream(videoId, stream_video)
                    } else {
                        GBLog.info("App:", "onStreamError:", isDebugMode())
                        RealmVideo.updateDelete(videoId)
                        report(videoId)
                        mYoutubeStreamListener!!.onStreamError()
                    }
                }
            } else {
                if (mYoutubeStreamListener != null) {
                    GBLog.info("App:", "onStartStream:", isDebugMode())
                    mYoutubeStreamListener!!.onStartStream()
                }
            }

        }
    }
    private val fileDownLoadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent!=null) {
                var videoId = intent.getStringExtra("videoId")
                if (mFileDownloadListener!=null){
                    mFileDownloadListener!!.onComplete(videoId)
                }
            }
            val realm = Realm.getDefaultInstance()
            val listDownLoad = realm.copyFromRealm(realm.where(RealmVideo::class.java).greaterThan("isDownLoaded",0.toInt()).findAll())
            var pathDirectory  = App.getInstance().filesDir.listFiles()//context!!.getExternalFilesDir(null).listFiles()
            for (video in listDownLoad){
                for (directory in pathDirectory){
                    if (directory.isDirectory){
                        if (directory.name.equals(video.videoID,true)){
                            var isFull = 2
                            val listFile = directory.listFiles()
                            if (listFile.size >=2 ){
                                for (file in listFile){
                                    if (file.isFile){
                                        if (file.name.contains("img_"+video.videoID,true)){
                                            isFull = isFull-1
                                            video.imageLarger = file.path
                                        }
                                        if (file.name.contains("video_"+video.videoID,true)){
                                            isFull = isFull-1
                                            video.linkPlay = file.path
                                        }
                                        GBLog.info("download complete",file.path, App.getInstance().isDebugMode())
                                    }
                                }
                                if (isFull <= 0) {
                                    video.isDownLoaded = 2

                                } else {
                                    video.isDownLoaded = 0
                                }
                                GBRealm.save(video)
                            }
                        }
                    }
                }
            }
        }
    }
}
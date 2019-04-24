package vn.android.thn.gbkids

import android.app.Application
import android.content.*
import android.os.Build
import android.provider.Settings
import android.support.multidex.MultiDex
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.activeandroid.ActiveAndroid
import com.facebook.ads.AudienceNetworkAds

import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.util.Util
import com.google.gson.GsonBuilder
import jp.co.tss21.monistor.models.GBDataBase
import org.json.JSONArray
import org.json.JSONObject
import vn.android.thn.gbfilm.views.listener.YoutubeStreamListener
import vn.android.thn.gbkids.constants.Constants
import vn.android.thn.gbkids.model.db.AppSetting
import vn.android.thn.gbkids.model.db.VideoDownLoad
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbkids.model.entity.StreamEntity
import vn.android.thn.gbkids.utils.LogUtils
import vn.android.thn.gbkids.views.services.DownLoadVideoService
import vn.android.thn.gbkids.views.services.YoutubeStreamService
import vn.android.thn.library.utils.GBUtils
import java.io.File
import java.lang.reflect.Modifier
import java.util.ArrayList


//
// Created by NghiaTH on 3/12/19.
// Copyright (c) 2019

class App : Application() {
    val builder = GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
    val gson = builder.create()
    var appStatus:Int = 0
    var playCount:Long = 0
    var mYoutubeStreamListener: YoutubeStreamListener? = null
    private var downloadDirectory: File? = null
    private var downloadCache: Cache? = null
//    private var downloadManager: DownloadManager? = null
//    private var downloadTracker: DownloadTracker? = null
    protected var userAgent: String = ""
    companion object {
        private val DOWNLOAD_CONTENT_DIRECTORY = "downloads"
        lateinit var INSTANCE_: App
        fun getInstance() = INSTANCE_
        var sBrowserUserAgent:String? = null
        fun getBrowserUserAgent():String {
            // get browser user agent
            if( sBrowserUserAgent == null) {
                var  web = WebView(INSTANCE_);
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
        AudienceNetworkAds.initialize(this)
        MultiDex.install(this)
        ActiveAndroid.initialize(this)
        registerReceiver(youtybeStreamReceiver, IntentFilter(Constants.YOUTUBE_STREAM));
//        registerReceiver(youtybeStreamReceiver, IntentFilter(Constants.YOUTUBE_STREAM));
        val listDownLoad = GBDataBase.getList(VideoDownLoad::class.java,"isComplete=0")
        var pathDirectory = getExternalFilesDir(null).listFiles()
        for (video in listDownLoad){
            for (directory in pathDirectory){
                if (directory.name.equals(video.videoID,true)){
                    var isFull = 2
                    val listFile = directory.listFiles()
                    if (listFile.size >=2 ){
                        for (file in listFile){
                            if (file.name.equals(video.imageName)){
                                isFull = isFull-1
                                video.thumbnails = file.path
                            }
                            if (file.name.equals(video.videoName)){
                                isFull = isFull-1
                                video.videoPath = file.path
                            }
                            LogUtils.info("download complete",file.path)
                        }
                        if (isFull == 0) {
                            video.isComplete = 1
                            video.save()
                        } else {
                            video.delete()
                        }
                    }
                }

            }
        }

    }


    fun isDebugMode(): Boolean {
        if (BuildConfig.DEBUG) {
            return true
        }
        return false
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
     * getVersionName
     */
    fun getVersionName(): String {
        try {
            return packageManager
                .getPackageInfo(packageName, 0).versionName
        } catch (e: Exception) {
            return ""
        }

    }
    fun getDeviceName():String{
        var deviceName = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL
        return deviceName
    }
    fun getDeviceType():String{
        return  "android"
    }
    fun getAppId():String{
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
    fun appSetting():AppSetting?{
        val data = GBDataBase.getObject(AppSetting::class.java,null,null)
        return data
    }
    /**
     *convertJsonToObject
     * @param jsonObject
     * @param clazz
     */
    fun <T : Any> convertJsonToObject(jsonObject: JSONObject, clazz: Class<T>): T? {
        return gson.fromJson(jsonObject.toString(), clazz)
    }

    /**
     * convertJsonToListObject
     * @param jsonArray
     * @param clazz
     */
    fun <T : Any> convertJsonToListObject(jsonArray: JSONArray, clazz: Class<T>): MutableList<T> {
        var list: MutableList<T> = ArrayList<T>()
        for (i in 0 until jsonArray.length()) {
            list.add(convertJsonToObject<T>(jsonArray.getJSONObject(i), clazz)!!)
        }
        return list
    }
    fun loadStream(videoId:String){
        if (mYoutubeStreamListener!= null){
            mYoutubeStreamListener!!.onStartStream()
        }
        val intentService = Intent(this, YoutubeStreamService::class.java)
        intentService.putExtra("videoId",videoId)
        startService(intentService)
    }
    fun downloadVideo(videoId:String){
        val intentService = Intent(this, DownLoadVideoService::class.java)
        intentService.putExtra("videoId",videoId)
        startService(intentService)
    }
    /** Returns whether extension renderers should be used.  */
    fun useExtensionRenderers(): Boolean {
        return "withExtensions" == BuildConfig.FLAVOR
    }

    /** Returns a [DataSource.Factory].  */
    fun buildDataSourceFactory(): DataSource.Factory {
        val upstreamFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "ExoPlayerDemo"))
        return buildReadOnlyCacheDataSource(upstreamFactory, getDownloadCache())
    }

    /** Returns a [HttpDataSource.Factory].  */
    fun buildHttpDataSourceFactory(): HttpDataSource.Factory {
        return DefaultHttpDataSourceFactory(userAgent)
    }

    @Synchronized
    private fun getDownloadCache(): Cache {
        if (downloadCache == null) {
            val downloadContentDirectory = File(getDownloadDirectory(), DOWNLOAD_CONTENT_DIRECTORY)
            downloadCache = SimpleCache(downloadContentDirectory, NoOpCacheEvictor())
        }
        return downloadCache!!
    }

    private fun getDownloadDirectory(): File {
        if (downloadDirectory == null) {
            downloadDirectory = getExternalFilesDir(null)
            if (downloadDirectory == null) {
                downloadDirectory = filesDir
            }
        }
        return downloadDirectory!!
    }
    private val youtybeStreamReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, initiatingIntent: Intent) {
//            val action = initiatingIntent.action
            val stream_list = initiatingIntent.getSerializableExtra("stream_list") as ArrayList<StreamEntity>
            if (mYoutubeStreamListener!= null){
                if (stream_list.size>0) {
                    mYoutubeStreamListener!!.onStream(stream_list)
                } else{
                    mYoutubeStreamListener!!.onStreamError()
                }
            }
        }
    }
    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, initiatingIntent: Intent) {

        }
    }
    private fun buildReadOnlyCacheDataSource(
        upstreamFactory: DefaultDataSourceFactory, cache: Cache
    ): CacheDataSourceFactory {
        return CacheDataSourceFactory(
            cache,
            upstreamFactory,
            FileDataSourceFactory(),
            /* eventListener= */ null,
            CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null
        )/* cacheWriteDataSinkFactory= */
    }
}

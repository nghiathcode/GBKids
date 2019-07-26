package vn.android.thn.commons.service

import android.app.IntentService
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.SparseArray
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.gson.Gson
import vn.android.thn.commons.App
import vn.android.thn.commons.GBRealm
import vn.android.thn.commons.ServerInfor.DOMAIN_CHECK_CONNECT
import vn.android.thn.commons.realm.RealmVideo
import vn.android.thn.gbkids.constants.Constants
import vn.android.thn.gbkids.views.activity.ActivityBase
import vn.android.thn.gbkids.views.fragment.PlayerFragment
import vn.android.thn.library.utils.GBLog
import vn.android.thn.library.utils.GBUtils
import java.net.InetAddress
import java.sql.Timestamp
import java.util.*
import kotlin.collections.HashMap

class YoutubeStreamService() : IntentService("YoutubeStreamService") {
    var app = App.getInstance()
    var videoId = ""
    var stream_video = ""
    var linksPlay = HashMap<Int,String>()
    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val sender = Intent()
            val bundle = Bundle()
            sender.action = Constants.YOUTUBE_STREAM
            bundle.putInt("action", 0)
            sender.putExtras(bundle)
            sendBroadcast(sender)
            videoId = intent!!.getStringExtra("videoId")
            if (PlayerFragment.dataSourceMap.containsKey(GBUtils.dateNow("yyyyMMdd")+videoId) && PlayerFragment.dataSourceMap.get(GBUtils.dateNow("yyyyMMdd")+videoId)!=null){
                stream_video = PlayerFragment.linkSourceMap.get(GBUtils.dateNow("yyyyMMdd")+videoId)!!
                app.videoSource = PlayerFragment.dataSourceMap.get(GBUtils.dateNow("yyyyMMdd")+videoId)
                GBLog.info("YoutubeStreamService", stream_video, app!!.isDebugMode())
                val sender = Intent()
                val bundle = Bundle()
                sender.action = Constants.YOUTUBE_STREAM
                bundle.putString("stream_video", stream_video)
                bundle.putString("videoId", videoId)
                bundle.putInt("action", 1)
                sender.putExtras(bundle)
                sendBroadcast(sender)
            } else {
                stream_video = ""
                val itemDownLoad = RealmVideo.getObject(videoId)
                if (itemDownLoad!= null){
                    if (itemDownLoad.isDownLoaded == 2){
                        val dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "gbkids"))
                        app.videoSource = ExtractorMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(Uri.parse(itemDownLoad!!.linkPlay))
                        val sender = Intent()
                        val bundle = Bundle()
                        sender.action = Constants.YOUTUBE_STREAM
                        bundle.putString("stream_video", itemDownLoad!!.linkPlay)
                        bundle.putString("videoId", videoId)
                        bundle.putInt("action", 1)
                        sender.putExtras(bundle)
                        sendBroadcast(sender)
                        return
                    }
                }

                var item = RealmVideo.videoDetail(videoId,GBUtils.dateNow("yyyyMMdd"))
                if (item == null){
                    getYoutubeDownloadUrl("https://www.youtube.com/watch?v=" + videoId)
                }else{
                    linksPlay = item.toLink()
                    if (linksPlay.size>0) {
                        sendStream()
                    } else{
                        getYoutubeDownloadUrl("https://www.youtube.com/watch?v=" + videoId)
                    }
                }
            }

        }

    }

    private fun getYoutubeDownloadUrl(youtubeLink: String) {
        object : YouTubeExtractor(App.getInstance()) {

            public override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta?) {
                if (ytFiles == null || vMeta == null) {
                    var isConnectInternet = false
                    if (app.isNetworkAvailable()){
                        try {
                            val ipAddr = InetAddress.getByName(DOMAIN_CHECK_CONNECT)
                            isConnectInternet = !ipAddr.equals("")
                        } catch (e:Exception){
                            isConnectInternet = false
                        }
                    }
                    app.isConnectInternet = isConnectInternet
                    if (isConnectInternet) {
                        sendStream()
                    } else{
                        val sender = Intent()
                        val bundle = Bundle()
                        sender.action = Constants.YOUTUBE_STREAM
                        bundle.putInt("action", 2)
                        sender.putExtras(bundle)
                        sendBroadcast(sender)
                    }
                    return
                }
                var i = 0
                var itag: Int
                var links = HashMap<Int, String>()
                while (i < ytFiles.size()) {
                    itag = ytFiles.keyAt(i)
                    // ytFile represents one file with its url and meta data
                    val ytFile = ytFiles.get(itag)
                    GBLog.info("YoutubeStreamService", "itag:" + itag + ":" + ytFile.url, app!!.isDebugMode())
                    links.put(itag, parseLinkMP4(ytFile.url)!!)
                    linksPlay.put(itag, ytFile.url)
                    i++
                }
                var item = RealmVideo.getObject(vMeta.videoId)
                if (item != null) {
                    item.linkPlay = Gson().toJson(links)
                    item.expiredVideo = GBUtils.dateNow("yyyyMMdd")
                    GBRealm.save(item)
                }
                sendStream()
            }
        }.extract(youtubeLink, true, true)
    }

    private fun sendStream() {
        var widthScreen = ActivityBase.getScreenWidth()
        if (widthScreen>360 ){
            if (linksPlay.containsKey(22)) {
                stream_video = linksPlay.get(22)!!
                initVideoSource()
            }else{
                if (linksPlay.containsKey(18)) {
                    stream_video = linksPlay.get(18)!!
                    initVideoSource()
                } else if (linksPlay.containsKey(43)) {
                    stream_video = linksPlay.get(43)!!
                    initVideoSource()
                } else if (linksPlay.containsKey(36)) {
                    stream_video = linksPlay.get(36)!!
                    initVideoSource()
                } else if (linksPlay.containsKey(17)) {
                    stream_video = linksPlay.get(17)!!
                    initVideoSource()
                } else if (linksPlay.containsKey(5)) {
                    stream_video = linksPlay.get(5)!!
                    initVideoSource()
                } else if (linksPlay.containsKey(22)) {
                    stream_video = linksPlay.get(22)!!
                    initVideoSource()
                }
            }
        } else{
            if (linksPlay.containsKey(18)) {
                stream_video = linksPlay.get(18)!!
                initVideoSource()
            } else if (linksPlay.containsKey(43)) {
                stream_video = linksPlay.get(43)!!
                initVideoSource()
            } else if (linksPlay.containsKey(36)) {
                stream_video = linksPlay.get(36)!!
                initVideoSource()
            } else if (linksPlay.containsKey(17)) {
                stream_video = linksPlay.get(17)!!
                initVideoSource()
            } else if (linksPlay.containsKey(5)) {
                stream_video = linksPlay.get(5)!!
                initVideoSource()
            } else if (linksPlay.containsKey(22)) {
                stream_video = linksPlay.get(22)!!
                initVideoSource()
            }
        }

        if (!GBUtils.isEmpty(stream_video)) {
            var url_link = Uri.parse(stream_video)
            var timestamp = url_link.getQueryParameter("expire").toLong()
            var ti = Timestamp(timestamp)
            var expire=Date(ti.time)
            GBLog.info("YoutubeStreamService", GBUtils.formatData(expire), app!!.isDebugMode())
            RealmVideo.updateWatched(videoId)
        } else{
            RealmVideo.updateDelete(videoId)
        }
        GBLog.info("YoutubeStreamService", stream_video, app!!.isDebugMode())
        val sender = Intent()
        val bundle = Bundle()
        sender.action = Constants.YOUTUBE_STREAM
        bundle.putString("stream_video", stream_video)
        bundle.putString("videoId", videoId)
        bundle.putInt("action", 1)
        sender.putExtras(bundle)
        sendBroadcast(sender)

    }
    private fun initVideoSource(){
        try {
            val mp4VideoUri = Uri.parse(stream_video)
            val dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "gbkids"))
            //DefaultHttpDataSourceFactory( Util.getUserAgent(this, "gbkids"))
//            if (isLive){
//                app.videoSourceHls = HlsMediaSource(mp4VideoUri, dataSourceFactory, 1, null, null)
//            } else {
//                app.videoSource = ExtractorMediaSource.Factory(dataSourceFactory)
//                    .createMediaSource(mp4VideoUri)
//            }

            app.videoSource = ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mp4VideoUri)
        } catch (e: Exception) {
            app.videoSourceHls = null
            app.videoSource = null
        }
    }
    fun parseLinkMP4(link: String): String? {
        var url_link = Uri.parse(link)
        var mime = url_link.getQueryParameter("mime")
        var host = url_link.host
        return link.replace(host, "redirector.googlevideo.com")

    }
}
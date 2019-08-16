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
import vn.android.thn.commons.ServerInfor
import vn.android.thn.commons.realm.RealmVideo
import vn.android.thn.gbkids.views.activity.ActivityBase
import vn.android.thn.gbkids.views.fragment.PlayerFragment
import vn.android.thn.library.utils.GBLog
import vn.android.thn.library.utils.GBUtils
import java.net.InetAddress
import java.sql.Timestamp
import java.util.*
import kotlin.collections.HashMap

class PlayStreamWorkerService() : IntentService("PlayStreamWorkerService") {
    var app = App.getInstance()
    var stream_video = ""

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            stream_video = ""
            if (PlayerFragment.dataSourceMap.size > 1000){
                PlayerFragment.dataSourceMap.clear()
                PlayerFragment.linkSourceMap.clear()
            }
            var videoId = intent!!.getStringExtra("videoId")
            if (PlayerFragment.dataSourceMap.containsKey(GBUtils.dateNow("yyyyMMdd")+videoId)){
                if (PlayerFragment.dataSourceMap.get(GBUtils.dateNow("yyyyMMdd")+videoId)!=null){
                    return
                }
            }
            var item = RealmVideo.videoDetail(videoId,GBUtils.dateNow("yyyyMMdd"))
            if (item == null){
                getYoutubeDownloadUrl("https://www.youtube.com/watch?v=" + videoId)
            }else{
                var linksPlay = item.toLink()
                if (linksPlay.size>0) {
                    sendStream(linksPlay,videoId)
                } else{
                    getYoutubeDownloadUrl("https://www.youtube.com/watch?v=" + videoId)
                }

            }
        }

    }

    private fun getYoutubeDownloadUrl(youtubeLink: String) {
        object : YouTubeExtractor(App.getInstance()) {

            public override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta?) {

                if (ytFiles == null || vMeta == null) {
//                    sendStream()
                    return
                }
                var linksPlay = HashMap<Int,String>()
                var i = 0
                var itag: Int
                var links = HashMap<Int, String>()
                while (i < ytFiles.size()) {
                    itag = ytFiles.keyAt(i)
                    // ytFile represents one file with its url and meta data
                    val ytFile = ytFiles.get(itag)
                    GBLog.info("PlayStreamWorkerService", "itag:" + itag + ":" + ytFile.url, app!!.isDebugMode())
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
                sendStream(linksPlay,vMeta.videoId)
            }
        }.extract(youtubeLink, true, true)
    }

    private fun sendStream(linksPlay:HashMap<Int,String>,videoId:String) {
        var widthScreen = ActivityBase.getScreenWidth()
        if (widthScreen>360 ){
            if (linksPlay.containsKey(22)) {
                initVideoSource(linksPlay.get(22)!!,videoId)
                return
            }
        }
        if (linksPlay.containsKey(18)) {
            initVideoSource(linksPlay.get(18)!!,videoId)
        } else if (linksPlay.containsKey(43)) {
            initVideoSource(linksPlay.get(43)!!,videoId)
        } else if (linksPlay.containsKey(36)) {
            initVideoSource(linksPlay.get(36)!!,videoId)
        } else if (linksPlay.containsKey(17)) {
            initVideoSource(linksPlay.get(17)!!,videoId)
        } else if (linksPlay.containsKey(5)) {
            initVideoSource(linksPlay.get(5)!!,videoId)
        } else if (linksPlay.containsKey(22)) {
            initVideoSource(linksPlay.get(22)!!,videoId)
        }

        if (!GBUtils.isEmpty(stream_video)) {
            var url_link = Uri.parse(stream_video)
            var timestamp = url_link.getQueryParameter("expire").toLong()
            var ti = Timestamp(timestamp)
            var expire=Date(ti.time)
            GBLog.info("PlayStreamWorkerService", GBUtils.formatData(expire), app!!.isDebugMode())

//            VideoTable.updateWatched(videoId)
        } else{
//            VideoTable.updateDelete(videoId)
        }

    }
    private fun initVideoSource(stream_video:String,videoId:String){
        GBLog.info("PlayStreamWorkerService", stream_video, app!!.isDebugMode())
        try {
            val mp4VideoUri = Uri.parse(stream_video)
            val dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "gbkids"))
            var videoSource: ExtractorMediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mp4VideoUri)
            PlayerFragment.dataSourceMap.put(GBUtils.dateNow("yyyyMMdd")+videoId,videoSource)
            PlayerFragment.linkSourceMap.put(GBUtils.dateNow("yyyyMMdd")+videoId,stream_video)

        } catch (e: Exception) {
            PlayerFragment.dataSourceMap.put(GBUtils.dateNow("yyyyMMdd")+videoId,null)
            PlayerFragment.linkSourceMap.put(GBUtils.dateNow("yyyyMMdd")+videoId,stream_video)
            return
        }
    }
    fun parseLinkMP4(link: String): String? {
        var url_link = Uri.parse(link)
        var mime = url_link.getQueryParameter("mime")
        var host = url_link.host
        return link.replace(host, "redirector.googlevideo.com")

    }
}
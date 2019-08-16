package vn.android.thn.commons.service

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import vn.android.thn.commons.App
import vn.android.thn.commons.realm.RealmVideo
import vn.android.thn.gbkids.constants.Constants
import vn.android.thn.gbkids.views.activity.ActivityBase
import vn.android.thn.library.utils.GBLog
import vn.android.thn.library.utils.GBUtils
import java.io.File
import java.net.URL

class DownLoadLocalVideoService : IntentService("DownLoadLocalVideoService") {
    var listUrl = HashMap<Int,YtFile>()
    var videoId=""
    override fun onHandleIntent(intent: Intent?) {
        listUrl.clear()
        videoId = intent!!.getStringExtra("videoId")
        getYoutubeDownloadUrl(videoId)
    }
    private fun getYoutubeDownloadUrl(youtubeLink: String) {
        object : YouTubeExtractor(App.getInstance()) {

            public override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta) {
                if (ytFiles == null) {
                    return
                }
                var i = 0
                var itag: Int
                while (i < ytFiles.size()) {
                    itag = ytFiles.keyAt(i)
                    // ytFile represents one file with its url and meta data
                    val ytFile = ytFiles.get(itag)
                    GBLog.info("DownLoadLocalVideoService", "itag:" + itag + ":" + ytFile.url, App.getInstance()!!.isDebugMode())
                    listUrl.put(itag, ytFile)
                    i++
                }
                if (listUrl.size>0){
                    var obj =RealmVideo.getObject(vMeta.videoId)
                    var widthScreen = ActivityBase.getScreenWidth()
                    var urlVideo = ""
                    var videoFileName=""
                    if (widthScreen>360 ){
                        if (listUrl.containsKey(22)) {
                            urlVideo = listUrl.get(22)!!.url
                            videoFileName = vMeta.videoId+"."+listUrl.get(22)!!.format.ext
                            if (!GBUtils.isEmpty(urlVideo) && !GBUtils.isEmpty(videoFileName)) {
                                if (obj!= null) {
                                    downloadThumbnail(obj!!.imageLarger, obj.title, vMeta.videoId, vMeta.videoId,urlVideo,videoFileName)
                                }
                            }
                            return
                        }
                    }
                    if (listUrl.containsKey(18)) {
                        urlVideo = listUrl.get(18)!!.url
                        videoFileName = vMeta.videoId+"."+listUrl.get(18)!!.format.ext
                    } else if (listUrl.containsKey(43)) {
                        urlVideo = listUrl.get(43)!!.url
                        videoFileName = vMeta.videoId+"."+listUrl.get(43)!!.format.ext
                    } else if (listUrl.containsKey(36)) {
                        urlVideo = listUrl.get(36)!!.url
                        videoFileName = vMeta.videoId+"."+listUrl.get(36)!!.format.ext
                    } else if (listUrl.containsKey(17)) {
                        urlVideo = listUrl.get(17)!!.url
                        videoFileName = vMeta.videoId+"."+listUrl.get(17)!!.format.ext
                    } else if (listUrl.containsKey(5)) {
                        urlVideo = listUrl.get(5)!!.url
                        videoFileName = vMeta.videoId+"."+listUrl.get(5)!!.format.ext
                    } else if (listUrl.containsKey(22)) {
                        urlVideo = listUrl.get(22)!!.url
                        videoFileName = vMeta.videoId+"."+listUrl.get(22)!!.format.ext
                    }
                    if (!GBUtils.isEmpty(urlVideo) && !GBUtils.isEmpty(videoFileName)) {
                        if (obj!= null) {
                            downloadThumbnail(obj!!.imageLarger, obj.title, vMeta.videoId, vMeta.videoId,urlVideo,videoFileName)
                        }
                    }
                }
            }
        }.extract(youtubeLink, true, false)
    }

    private fun downloadFromUrl(youtubeDlUrl: String, downloadTitle: String,folder:String,fileName: String) {
        Thread({
            kotlin.run {
                val directory = App.getInstance().filesDir
                val directoryDownLoad =File(directory.path+"/"+folder)
                if (!directoryDownLoad.exists()){
                    directoryDownLoad.mkdir()
                }
                //
                try {
                    val urlRequest = URL(youtubeDlUrl)
                    val urlConnection = urlRequest.openConnection()
                    var input  = urlConnection.getInputStream()
                    File(directory.path+"/"+folder+"/"+"video_"+fileName).outputStream().use { input.copyTo(it) }
                    val sender = Intent()
                    val bundle = Bundle()
                    sender.action = Constants.DOWNLOAD_VIDEO
                    bundle.putString("videoId", videoId)
                    sender.putExtras(bundle)
                    sendBroadcast(sender)


                }catch (e:Exception){
                    directoryDownLoad.delete()
                    GBLog.info("DownLoadLocalVideoService", "error download Video", App.getInstance()!!.isDebugMode())
                }
            }
        }).start()
        //
//        val uri = Uri.parse(youtubeDlUrl)
//        val request = DownloadManager.Request(uri)
//        request.setTitle(downloadTitle)
//        request.allowScanningByMediaScanner()
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//        request.setDestinationInExternalFilesDir(this,folder,"video_"+fileName)
//        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        manager.enqueue(request)
    }
    private fun downloadThumbnail(url:String, downloadTitle: String,folder:String,fileName:String,videoLink:String,videoFileName:String){
        var ext = url.substring(url.lastIndexOf("."))
//        val uri = Uri.parse(url)
//        val request = DownloadManager.Request(uri)
//        request.setTitle(downloadTitle)
//        request.allowScanningByMediaScanner()
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//        request.setDestinationInExternalFilesDir(this,folder,"img_"+fileName+ext)
//        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        manager.enqueue(request)
        //

        try {

            val urlRequest = URL(url)
            val urlConnection = urlRequest.openConnection()
            var input  = urlConnection.getInputStream()
            val directory = App.getInstance().filesDir
            val directoryDownLoad =File(directory.path+"/"+folder)
            if (!directoryDownLoad.exists()){
                directoryDownLoad.mkdir()
            }
            File(directory.path+"/"+folder+"/"+"img_"+fileName+ext).outputStream().use { input.copyTo(it) }
//            File(pathRoot.path+"/"+folder+"/"+"img_"+fileName+ext).outputStream().use { input.copyTo(it) }
            downloadFromUrl(videoLink,downloadTitle,folder,videoFileName)
        }catch (e:Exception){
            GBLog.info("DownLoadLocalVideoService", "error download Image", App.getInstance()!!.isDebugMode())
        }

        //
    }

}
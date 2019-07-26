package vn.android.thn.gbkids.views.services

import android.app.DownloadManager
import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.support.v4.content.FileProvider
import android.util.SparseArray
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import jp.co.tss21.monistor.models.GBDataBase
import vn.android.thn.gbkids.App
import vn.android.thn.gbkids.model.db.VideoDownLoad
import vn.android.thn.gbkids.utils.LogUtils
import vn.android.thn.library.utils.GBLog
import vn.android.thn.library.utils.GBUtils
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class DownLoadVideoService: IntentService("DownLoadVideoService") {
    var listUrl:MutableList<YtFile> = ArrayList<YtFile>()
    var videoId=""
    override fun onHandleIntent(intent: Intent?) {
        listUrl.clear()
        videoId = intent!!.getStringExtra("videoId")
        getYoutubeDownloadUrl("https://www.youtube.com/watch?v=" + videoId)
    }
    private fun getYoutubeDownloadUrl(youtubeLink: String) {
        object : YouTubeExtractor(App.getInstance()) {

            public override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta) {
//                mainProgressBar.setVisibility(View.GONE)

                if (ytFiles == null) {
                    return
                }
                var i = 0
                var itag: Int
                while (i < ytFiles.size()) {
                    itag = ytFiles.keyAt(i)
                    // ytFile represents one file with its url and meta data
                    val ytFile = ytFiles.get(itag)
                    // Just add videos in a decent format => height -1 = audio
                    if (ytFile.getFormat().getHeight() >= 360){
                        LogUtils.info("URL_STREAM_VideoID",ytFile.url)
                        if (ytFile.url.contains("ratebypass",true)) {
//                            stream_list.add(StreamEntity(ytFile.url,ytFile.format.height))
                            listUrl.add(ytFile)
                            LogUtils.info("URL_STREAM_VideoID",ytFile.url)
                        }

                    }
                    i++
                }
                if (listUrl.size>0){
                    if (!GBUtils.isEmpty(vMeta.maxResImageUrl)){
                        downloadThumbnail(vMeta.maxResImageUrl,vMeta.title,vMeta.videoId,vMeta.videoId)
                    } else if (!GBUtils.isEmpty(vMeta.sdImageUrl)){
                        downloadThumbnail(vMeta.sdImageUrl,vMeta.title,vMeta.videoId,vMeta.videoId)
                    }else if (!GBUtils.isEmpty(vMeta.hqImageUrl)){
                        downloadThumbnail(vMeta.hqImageUrl,vMeta.title,vMeta.videoId,vMeta.videoId)
                    } else if (!GBUtils.isEmpty(vMeta.mqImageUrl)){
                        downloadThumbnail(vMeta.mqImageUrl,vMeta.title,vMeta.videoId,vMeta.videoId)
                    } else if (!GBUtils.isEmpty(vMeta.thumbUrl)){
                        downloadThumbnail(vMeta.thumbUrl,vMeta.title,vMeta.videoId,vMeta.videoId)
                    }
                    downloadFromUrl(listUrl.get(0).url,vMeta.title,vMeta.videoId,vMeta.videoId+"."+listUrl.get(0).format.ext)

                }
            }
        }.extract(youtubeLink, true, false)
    }

    private fun downloadFromUrl(youtubeDlUrl: String, downloadTitle: String,folder:String,fileName: String) {
        var data = GBDataBase.getObject(VideoDownLoad::class.java, "videoID=?", *arrayOf(folder))
        if (data!= null){
            data.videoName =fileName
            data.save()
        }
        val uri = Uri.parse(youtubeDlUrl)
        val request = DownloadManager.Request(uri)
        request.setTitle(downloadTitle)
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalFilesDir(this,folder,fileName)
        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)


    }
    private fun downloadThumbnail(url:String, downloadTitle: String,folder:String,fileName:String){
        var ext = url.substring(url.lastIndexOf("."))
        var data = GBDataBase.getObject(VideoDownLoad::class.java, "videoID=?", *arrayOf(folder))
        if (data!= null){
            data.imageName =fileName+ext
            data.save()
        }
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)

        request.setTitle(downloadTitle)
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalFilesDir(this,folder,fileName+ext)
        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)

    }
}
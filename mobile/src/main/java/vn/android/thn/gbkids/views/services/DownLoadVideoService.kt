package vn.android.thn.gbkids.views.services

import android.app.DownloadManager
import android.app.IntentService
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.support.v4.content.FileProvider
import android.util.SparseArray
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import vn.android.thn.gbkids.App
import vn.android.thn.gbkids.model.entity.StreamEntity
import vn.android.thn.gbkids.utils.LogUtils

class DownLoadVideoService: IntentService("DownLoadVideoService") {
    var listUrl:MutableList<YtFile> = ArrayList<YtFile>()
    var title=""
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
                title= vMeta.title
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
                    downloadFromUrl(listUrl.get(0).url,title,videoId+"."+listUrl.get(0).format.ext)
                }
            }
        }.extract(youtubeLink, true, false)
    }

    private fun downloadFromUrl(youtubeDlUrl: String, downloadTitle: String, fileName: String) {
//        val folder = FileProvider.getUriForFile(this, "jp.co.tss21.monistor.fileprovider", fileName)
        val uri = Uri.parse(youtubeDlUrl)
        val request = DownloadManager.Request(uri)
        request.setTitle(downloadTitle)
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        request.setDestinationInExternalFilesDir(this,fileName,fileName)
        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
    }

}
package vn.android.thn.gbkids.views.services

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import vn.android.thn.gbkids.App
import vn.android.thn.gbkids.constants.Constants
import vn.android.thn.gbkids.model.api.GBRequestName
import vn.android.thn.gbkids.model.api.GBTubeRequestCallBack
import vn.android.thn.gbkids.model.api.request.GBTubeRequest
import vn.android.thn.gbkids.model.api.response.GBTubeResponse
import vn.android.thn.gbkids.model.entity.StreamEntity
import vn.android.thn.gbkids.utils.LogUtils
import vn.android.thn.library.net.GBRequestError
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


//
// Created by NghiaTH on 2/27/19.
// Copyright (c) 2019

class YoutubeStreamService() : IntentService("YoutubeStreamService") {

    var videoId = ""

    var stream_list:ArrayList<StreamEntity> = ArrayList<StreamEntity>()
    override fun onHandleIntent(intent: Intent?) {
        videoId = intent!!.getStringExtra("videoId")

        getYoutubeDownloadUrl("https://www.youtube.com/watch?v=" + videoId)


    }
    private fun getYoutubeDownloadUrl(youtubeLink: String) {
        object : YouTubeExtractor(App.getInstance()) {

            public override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta) {
//                mainProgressBar.setVisibility(View.GONE)

                if (ytFiles == null) {
                    sendStream()
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
                        if (ytFile.url.contains("ratebypass",true)) {

                            stream_list.add(StreamEntity(ytFile.url,ytFile.format.height))
                            LogUtils.info("URL_STREAM_VideoID",ytFile.url)
                        }

                    }
                    i++
                }
                sendStream()
            }
        }.extract(youtubeLink, true, false)
    }

    private fun sendStream(){
        val sender = Intent()
        val bundle = Bundle()
        sender.action = Constants.YOUTUBE_STREAM
        bundle.putSerializable("stream_list", stream_list)
        sender.putExtras(bundle)
        sendBroadcast(sender)
        if (stream_list.size>0){
            val api = GBTubeRequest(GBRequestName.COUNT_PLAY,null)
            api.get().execute(object : GBTubeRequestCallBack {
                override fun onResponse(httpCode: Int, response: GBTubeResponse, request: GBTubeRequest) {

                }

                override fun onRequestError(errorRequest: GBRequestError, request: GBTubeRequest) {

                }

            })
        }
    }
}

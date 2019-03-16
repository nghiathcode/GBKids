package vn.android.thn.gbkids.views.services

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import vn.android.thn.gbkids.App
import vn.android.thn.gbkids.constants.Constants
import vn.android.thn.gbkids.model.api.GBTubeRequestCallBack
import vn.android.thn.gbkids.model.api.request.GBTubeRequest
import vn.android.thn.gbkids.model.api.request.StreamRequest
import vn.android.thn.gbkids.model.api.response.GBTubeResponse
import vn.android.thn.gbkids.model.api.response.StreamResponse
import vn.android.thn.gbyoutubelibrary.entity.VideoStreamParam
import vn.android.thn.library.net.GBRequestError
import vn.android.thn.library.net.GBResponseType
import vn.android.thn.library.utils.GBLog
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


//
// Created by NghiaTH on 2/27/19.
// Copyright (c) 2019

class YoutubeStreamService() : IntentService("YoutubeStreamService") {
    var countDown = 10

    var videoId = ""
    override fun onHandleIntent(intent: Intent?) {
        countDown = 10

        videoId = intent!!.getStringExtra("videoId")
        GBLog.info("URL_STREAM_VideoID",videoId,App.getInstance().isDebugMode())

        loadStream(videoId)
    }
    fun loadStream(videoId:String){
//        val url = "https://www.youtube.com/get_video_info?el=embedded&ps=default&eurl=&gl=US&hl=en&video_id=$videoId"

        val api = StreamRequest()
        var param = VideoStreamParam(videoId)
        api.addHeader("User-Agent", App.getBrowserUserAgent())
        api.mParameters = param.toParamRequest()
        api.responseType = GBResponseType.TEXT
        api.get().execute(object : GBTubeRequestCallBack {
            override fun onRequestError(errorRequest: GBRequestError, request: GBTubeRequest) {
                countDown --
                if (countDown>0){
                    countDown --
                    loadStream(videoId)
                }
            }

            override fun onResponse(httpCode: Int, response: GBTubeResponse, request: GBTubeRequest) {
                GBLog.info("URL_STREAM_check:",countDown.toString(),App.getInstance().isDebugMode())
                var listUrl = response.toResponse(StreamResponse::class)!!.listUrl
                for (url in listUrl){
                    if (isConnect(url)){
                        countDown = 0
                        sendComplete(listUrl)
                        return
                    }
                }
                if (countDown>0){
                    countDown --
                    loadStream(videoId)
                } else {
                    sendComplete(ArrayList<String>())
                }

            }
        })
    }
    private fun isConnect(url_check: String): Boolean {
        var con: HttpURLConnection? = null
        try {
            val url = URL(url_check)
            con = url.openConnection() as HttpURLConnection
            con!!.setConnectTimeout(100)
            val response_code = con!!.getResponseCode()
            con!!.disconnect()
            if (response_code == 200) {
                GBLog.info("YoutubeStreamService_connect_"+countDown, url_check,App.getInstance().isDebugMode())
                return true
            } else {
//                GBLog.error("YoutubeStreamService_no_connect_"+countDown, url_check,App.getInstance().isDebugMode())
                return false
            }
        } catch (e: IOException) {
//            GBLog.error("YoutubeStreamService", e.message + "",App.getInstance().isDebugMode())
            return false
        }

    }
    fun sendComplete(ArrayUrl:ArrayList<String>){
            val sender = Intent()
            sender.action = Constants.YOUTUBE_STREAM
            sender.putStringArrayListExtra("stream_list",ArrayUrl)
            if (ArrayUrl.size>0) {
                GBLog.info("URL_STREAM", "send_OK", App.getInstance().isDebugMode())
            } else {
                GBLog.info("URL_STREAM", "send_NOT_OK", App.getInstance().isDebugMode())
            }
            sendBroadcast(sender)
    }
}

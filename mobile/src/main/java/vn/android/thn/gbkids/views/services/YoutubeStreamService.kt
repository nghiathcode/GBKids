package vn.android.thn.gbkids.views.services

import android.app.IntentService
import android.content.Intent
import vn.android.thn.gbkids.App
import vn.android.thn.library.utils.GBLog


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

    }
}

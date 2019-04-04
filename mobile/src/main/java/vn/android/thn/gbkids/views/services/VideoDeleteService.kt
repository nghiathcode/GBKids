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
import vn.android.thn.gbkids.model.api.request.GBTubeRequest
import vn.android.thn.gbkids.model.entity.StreamEntity
import vn.android.thn.gbkids.utils.LogUtils
import vn.android.thn.gbkids.utils.Utils
import vn.android.thn.library.utils.GBLog
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


//
// Created by NghiaTH on 2/27/19.
// Copyright (c) 2019

class VideoDeleteService() : IntentService("VideoDeleteService") {

    var videoId = ""

    override fun onHandleIntent(intent: Intent?) {
        videoId = intent!!.getStringExtra("videoId")
        val api = GBTubeRequest(String.format(GBRequestName.DELETE_VIDEO,videoId),null)
        api.delete().execute()
    }

}

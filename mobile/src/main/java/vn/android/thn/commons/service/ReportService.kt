package vn.android.thn.commons.service

import android.app.IntentService
import android.content.Intent
import vn.android.thn.commons.App
import vn.android.thn.commons.GBVideoRequest
import vn.android.thn.commons.ServerInfor
import vn.android.thn.commons.response.GBTubeResponse
import vn.android.thn.gbkids.model.VideoReportEntity
import vn.android.thn.gbkids.model.api.GBVideoRequestCallBack
import vn.android.thn.library.net.GBRequestError
import vn.android.thn.library.utils.GBUtils
import java.net.InetAddress

class ReportService :IntentService("ReportService"){
    override fun onHandleIntent(intent: Intent?) {
        if (intent!= null){
            var videoId = intent!!.getStringExtra("videoId")
            var flagReport = intent!!.getIntExtra("flagReport",1)
            var comment = intent!!.getStringExtra("comment")
            if (GBUtils.isEmpty(comment)){
                comment = ""
            }
            val report = VideoReportEntity(videoId,comment,if (flagReport==1) true else false)
            report.isDebug = if (App.getInstance().isDebugMode()) 1 else 0
            val api =  GBVideoRequest(String.format("report?isDebug=%s", App.getInstance().isDebugMode().toString()),null)
            api.dataBody = report
            api.post().execute(object : GBVideoRequestCallBack {
                override fun onRequestError(errorRequest: GBRequestError, request: GBVideoRequest) {

                }
                override fun onResponse(httpCode: Int, response: GBTubeResponse, request: GBVideoRequest) {

                }
            })
        }
    }
}
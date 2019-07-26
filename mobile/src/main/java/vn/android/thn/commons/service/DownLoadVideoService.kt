package vn.android.thn.commons.service

import android.app.IntentService
import android.content.Intent
import vn.android.thn.commons.*
import vn.android.thn.commons.realm.RealmTableDateUpdate
import vn.android.thn.commons.realm.RealmVideo
import vn.android.thn.commons.response.DateUpdateResponse
import vn.android.thn.commons.response.GBTubeResponse
import vn.android.thn.commons.response.VideoResponse
import vn.android.thn.gbkids.constants.Constants
import vn.android.thn.gbkids.model.api.GBVideoRequestCallBack
import vn.android.thn.library.net.GBRequestError
import vn.android.thn.library.utils.GBLog
import vn.android.thn.library.utils.GBUtils

class DownLoadVideoService : IntentService("DownLoadVideoService") {
    var dateDownLoad: RealmTableDateUpdate?=null
    override fun onHandleIntent(intent: Intent?) {
        minDate()
        dateDownLoad = RealmTableDateUpdate.getObject("videos")
        if (dateDownLoad==null){
            dateDownLoad = RealmTableDateUpdate()
            dateDownLoad!!.tableName="videos"
        }
        if (!GBUtils.isEmpty(dateDownLoad!!.dateUp)){
            downLoadUp(dateDownLoad!!.dateUp)
        }
        if (!GBUtils.isEmpty(dateDownLoad!!.dateDown)){
            if (dateDownLoad!!.dateDownStop.equals(dateDownLoad!!.dateDown)){
                sendComplete()
                return
            }
            downLoadDown(dateDownLoad!!.dateDown)
        } else {
            downLoadDown(dateDownLoad!!.dateUp)
        }
    }
    fun downLoadUp(date:String){
        val api = GBVideoDownLoadRequest(String.format("execute_native_to_result?downloadKey=%s&isDebug=%s",App.getInstance().getSettingApp().downloadKey,App.getInstance().isDebugMode().toString()))
        val queryobject = QueryNativeEntity()
        queryobject.queryNativeString ="select videos.* ,channels.imageUrl as channelImage from videos,channels where channels.channelId = videos.channelId and videos.dateUpdate >=?0 and videos.appID = 'vn.thn.app.gbkids' order by dateUpdate ASC "
        queryobject.firstResult = 0
        queryobject.maxResults = 500
        queryobject.params.add(date)
        api.queryobject = queryobject
        api.get().execute(object : GBVideoRequestCallBack {
            override fun onRequestError(errorRequest: GBRequestError, request: GBVideoRequest) {
                GBLog.info("DownLoadVideoService","downLoadUp_onRequestError:", App.getInstance().isDebugMode())
            }
            override fun onResponse(httpCode: Int, response: GBTubeResponse, request: GBVideoRequest) {
                var videoResponse = response.toResponse(VideoResponse::class)
                GBLog.info("DownLoadVideoService","downLoadUp_onResponse:", App.getInstance().isDebugMode())
                if (videoResponse!= null) {
                    GBRealm.insertList(videoResponse.listRealmVideo)
                    var dateDownLoad= RealmTableDateUpdate.getObject("videos")
                    if (dateDownLoad!= null){
                        dateDownLoad.dateUp = videoResponse!!.listRealmVideo.get(videoResponse!!.listRealmVideo.size-1).dateUpdate
                        GBRealm.save(dateDownLoad)
                        App.getInstance().scanFileDowLoad()
                    }
                }
            }
        })
    }
    fun downLoadDown(date:String){
        val api = GBVideoDownLoadRequest(String.format("execute_native_to_result?downloadKey=%s&isDebug=%s",App.getInstance().getSettingApp().downloadKey,App.getInstance().isDebugMode().toString()))
        val queryobject = QueryNativeEntity()
        queryobject.queryNativeString ="select videos.* ,channels.imageUrl as channelImage from videos,channels where channels.channelId = videos.channelId and videos.dateUpdate<=?0 and videos.appID = 'vn.thn.app.gbkids' order by dateUpdate DESC "
        queryobject.firstResult = 0
        queryobject.maxResults = 500
        queryobject.params.add(date)
        api.queryobject = queryobject
        api.get().execute(object : GBVideoRequestCallBack {
            override fun onRequestError(errorRequest: GBRequestError, request: GBVideoRequest) {
                GBLog.error("DownLoadVideoService","downLoadDown_onRequestError:", App.getInstance().isDebugMode())
                sendComplete()
            }
            override fun onResponse(httpCode: Int, response: GBTubeResponse, request: GBVideoRequest) {
                GBLog.info("DownLoadVideoService","downLoadDown_onResponse:", App.getInstance().isDebugMode())
                var videoResponse = response.toResponse(VideoResponse::class)
                if (videoResponse!= null) {
                    GBRealm.insertList(videoResponse.listRealmVideo)
                    var dateDownLoad= RealmTableDateUpdate.getObject("videos")
                    if (dateDownLoad!= null){
                        dateDownLoad.dateDown = videoResponse!!.listRealmVideo.get(videoResponse!!.listRealmVideo.size-1).dateUpdate
                        GBRealm.save(dateDownLoad)
                        App.getInstance().scanFileDowLoad()
                    }
                }
                sendComplete()
            }
        })
    }
    fun sendComplete(){
        val sender = Intent()
        sender.action = Constants.DOWNLOAD_DATA
        sendBroadcast(sender)
    }
    fun minDate(){
        val api = GBVideoDownLoadRequest(String.format("execute_native_to_result?downloadKey=%s&isDebug=%s",App.getInstance().getSettingApp().downloadKey,App.getInstance().isDebugMode().toString()))
        val queryobject = QueryNativeEntity()
        queryobject.queryNativeString ="select min(dateUpdate) as dateUpdate from videos where videos.appID = 'vn.thn.app.gbkids'"
        queryobject.firstResult = 0
        queryobject.isListResult = false
        queryobject.maxResults = 0
        api.queryobject = queryobject
        api.get().execute(object : GBVideoRequestCallBack {
            override fun onRequestError(errorRequest: GBRequestError, request: GBVideoRequest) {
                GBLog.info("DownLoadVideoService","minDate_onRequestError:", App.getInstance().isDebugMode())
                sendComplete()
            }

            override fun onResponse(httpCode: Int, response: GBTubeResponse, request: GBVideoRequest) {
                var videoResponse = response.toResponse(DateUpdateResponse::class)
                if (videoResponse!= null) {
                    var dateDownLoad= RealmTableDateUpdate.getObject("videos")
                    if (dateDownLoad==null){
                        val maxDate = RealmVideo.maxDate()
                        dateDownLoad = RealmTableDateUpdate()
                        dateDownLoad!!.tableName="videos"
                        dateDownLoad!!.dateUp = maxDate
                    }
                    dateDownLoad.dateDownStop = videoResponse.dateUpdate
                    GBRealm.save(dateDownLoad)
                }
            }
        })
    }
}
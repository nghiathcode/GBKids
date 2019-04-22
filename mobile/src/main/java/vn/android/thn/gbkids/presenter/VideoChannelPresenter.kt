package vn.android.thn.gbkids.presenter

import android.support.v4.app.FragmentActivity
import vn.android.thn.gbkids.constants.ResponseCode
import vn.android.thn.gbkids.model.api.GBRequestName
import vn.android.thn.gbkids.model.api.GBTubeRequestCallBack
import vn.android.thn.gbkids.model.api.request.GBTubeRequest
import vn.android.thn.gbkids.model.api.response.GBTubeResponse
import vn.android.thn.gbkids.model.api.response.LogoChannelResponse
import vn.android.thn.gbkids.model.api.response.NewResponse
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbkids.model.entity.ChannelLogoEntity
import vn.android.thn.library.net.GBRequestError


//
// Created by NghiaTH on 3/20/19.
// Copyright (c) 2019

class VideoChannelPresenter(mvp: VideoChannelMvp, mActivity: FragmentActivity?) :
    PresenterBase<VideoChannelPresenter.VideoChannelMvp>(mvp, mActivity) {
    var channelId:String = ""
    fun loadData(channelId:String,offset:Int = 0,isShowPopup: Boolean = true){
        this.channelId = channelId
        if (isShowPopup) {
            mMvp!!.onStartLoad()

        }
        val api = GBTubeRequest(String.format(GBRequestName.VIDEO_CHANNEL,channelId),mActivity!!)
        api.addHeader("offset",offset.toString())
        api.get().execute(object : GBTubeRequestCallBack {
            override fun onRequestError(errorRequest: GBRequestError, request: GBTubeRequest) {
                if (isShowPopup) {
                    mMvp!!.onComplete()
                }
            }

            override fun onResponse(httpCode: Int, response: GBTubeResponse, request: GBTubeRequest) {


                val result = response.toResponse(NewResponse::class)!!
                if (result!!.error.errorCode == ResponseCode.NO_ERROR){
                    mMvp!!.onListVideo(result.data,result.offset)
                    if (isShowPopup) {
                        mMvp!!.onComplete()
                    }
                } else if (result!!.error.errorCode == ResponseCode.TOKEN_EXPIRED){
                    refreshToken(request.mRequestName)
                } else{
                    if (isShowPopup) {
                        mMvp!!.onComplete()
                    }
                }

            }
        })

    }

    override fun onRefreshComplete(requestName: String) {
        if (requestName.equals(String.format(GBRequestName.VIDEO_CHANNEL,channelId),true)){
            loadData(channelId,0,true)
        } else {
            mMvp!!.onComplete()
        }

    }
    interface VideoChannelMvp : MVPBase {
        fun onListVideo(listVideo: MutableList<VideoTable>,offset:Int = -1)
        fun onStartLoad()
        fun onComplete()
    }
}

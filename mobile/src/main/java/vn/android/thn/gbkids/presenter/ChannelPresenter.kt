package vn.android.thn.gbkids.presenter

import android.support.v4.app.FragmentActivity
import vn.android.thn.gbkids.constants.ResponseCode
import vn.android.thn.gbkids.model.api.GBRequestName
import vn.android.thn.gbkids.model.api.GBTubeRequestCallBack
import vn.android.thn.gbkids.model.api.request.GBTubeRequest
import vn.android.thn.gbkids.model.api.response.ChannelListResponse
import vn.android.thn.gbkids.model.api.response.GBTubeResponse
import vn.android.thn.gbkids.model.api.response.LogoChannelResponse
import vn.android.thn.gbkids.model.api.response.NewResponse
import vn.android.thn.gbkids.model.db.FollowTable
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbkids.model.entity.ChannelLogoEntity
import vn.android.thn.library.net.GBRequestError


//
// Created by NghiaTH on 3/20/19.
// Copyright (c) 2019

class ChannelPresenter(mvp: ChannelMvp, mActivity: FragmentActivity?) :
    PresenterBase<ChannelPresenter.ChannelMvp>(mvp, mActivity) {

    fun loadData(offset:Int = 0,isShowPopup: Boolean = true){

        if (isShowPopup) {
            showLoading()
        }
        val api = GBTubeRequest(GBRequestName.CHANNEL_LIST,mActivity!!)
        api.addHeader("offset",offset.toString())
        api.get().execute(object : GBTubeRequestCallBack {
            override fun onRequestError(errorRequest: GBRequestError, request: GBTubeRequest) {
                if (isShowPopup) {
                    hideLoading()
                }
            }

            override fun onResponse(httpCode: Int, response: GBTubeResponse, request: GBTubeRequest) {


                val result = response.toResponse(ChannelListResponse::class)!!
                if (result!!.error.errorCode == ResponseCode.NO_ERROR){
                    mMvp!!.onChannelList(result.data,result.offset)
                    if (isShowPopup) {
                        hideLoading()
                    }
                } else if (result!!.error.errorCode == ResponseCode.TOKEN_EXPIRED){
                    refreshToken(request.mRequestName)
                } else{
                    if (isShowPopup) {
                        hideLoading()
                    }
                }

            }
        })

    }

    override fun onRefreshComplete(requestName: String) {
        if (requestName.equals(GBRequestName.CHANNEL_LIST,true)){
            loadData(0,true)
        } else {
            hideLoading()
        }

    }
    interface ChannelMvp : MVPBase {
        fun onChannelList(listVideo: MutableList<FollowTable>,offset:Int = -1)
    }
}

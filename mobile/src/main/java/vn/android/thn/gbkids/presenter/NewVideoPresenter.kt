package vn.android.thn.gbkids.presenter

import android.support.v4.app.FragmentActivity
import vn.android.thn.gbkids.constants.ResponseCode
import vn.android.thn.gbkids.model.api.GBRequestName
import vn.android.thn.gbkids.model.api.GBTubeRequestCallBack
import vn.android.thn.gbkids.model.api.request.GBTubeRequest
import vn.android.thn.gbkids.model.api.response.GBTubeResponse
import vn.android.thn.gbkids.model.api.response.NewResponse
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.library.net.GBRequestError


//
// Created by NghiaTH on 2/27/19.
// Copyright (c) 2019

class NewVideoPresenter(mvp: SearchMvp, mActivity: FragmentActivity?) :
    PresenterBase<NewVideoPresenter.SearchMvp>(mvp, mActivity) {
    var nextPageToken = ""
    fun loadNew(offset:Int = 0,isShowPopup: Boolean = true){
        if (isShowPopup) {
            showLoading()
        }
        val api = GBTubeRequest(GBRequestName.NEW,mActivity!!)
        api.addHeader("offset",offset.toString())
        api.get().execute(object : GBTubeRequestCallBack {
            override fun onRequestError(errorRequest: GBRequestError, request: GBTubeRequest) {
                if (isShowPopup) {
                    hideLoading()
                }
            }

            override fun onResponse(httpCode: Int, response: GBTubeResponse, request: GBTubeRequest) {

                if (httpCode == 200){
                    val result = response.toResponse(NewResponse::class)!!
                    if (result!!.error.errorCode == ResponseCode.NO_ERROR){
                        mMvp!!.onSearch(result.data,result.offset)
                        if (isShowPopup) {
                            hideLoading()
                        }
                    } else if (result!!.error.errorCode == ResponseCode.TOKEN_EXPIRED){
                        refreshToken(request.mRequestName)
                    } else if (result!!.error.errorCode == ResponseCode.TOKEN_REGISTER){
                        registerToken(request.mRequestName)
                    } else{
                        if (isShowPopup) {
                            hideLoading()
                        }
                    }
                } else {
                    mMvp!!.onNetworkFail()
                }
            }
        })

    }
    fun suggestionList(offset:Int = 0,isShowPopup: Boolean = true){
        if (isShowPopup) {
            showLoading()
        }
        val api = GBTubeRequest(GBRequestName.SUGGESTIONS_LIST,mActivity!!)
        api.addHeader("offset",offset.toString())
        api.get().execute(object : GBTubeRequestCallBack {
            override fun onRequestError(errorRequest: GBRequestError, request: GBTubeRequest) {
                if (isShowPopup) {
                    hideLoading()
                }
            }

            override fun onResponse(httpCode: Int, response: GBTubeResponse, request: GBTubeRequest) {

                if (httpCode == 200){
                    val result = response.toResponse(NewResponse::class)!!
                    if (result!!.error.errorCode == ResponseCode.NO_ERROR){
                        mMvp!!.onSearch(result.data,result.offset)
                        if (isShowPopup) {
                            hideLoading()
                        }
                    } else if (result!!.error.errorCode == ResponseCode.TOKEN_EXPIRED){
                        refreshToken(request.mRequestName)
                    } else if (result!!.error.errorCode == ResponseCode.TOKEN_REGISTER){
                        registerToken(request.mRequestName)
                    } else{
                        if (isShowPopup) {
                            hideLoading()
                        }
                    }
                } else {
                    mMvp!!.onNetworkFail()
                }
            }
        })

    }
    override fun onRegisterTokenComplete(requestName: String) {
        if (requestName.equals(GBRequestName.NEW,true)){
            loadNew(0,true)
        } else if (requestName.equals(GBRequestName.SUGGESTIONS_LIST,true)){
            suggestionList(0,true)
        } else{
            hideLoading()
        }
    }
    override fun onRefreshComplete(requestName: String) {
        if (requestName.equals(GBRequestName.NEW,true)){
            loadNew(0,true)
        } else if (requestName.equals(GBRequestName.SUGGESTIONS_LIST,true)){
            suggestionList(0,true)
        } else {
            hideLoading()
        }

    }
    interface SearchMvp : MVPBase {
        fun onSearch(result: MutableList<VideoTable>,offset:Int = -1)
    }
}

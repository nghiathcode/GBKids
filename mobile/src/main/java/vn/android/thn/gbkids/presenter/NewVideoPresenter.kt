package vn.android.thn.gbkids.presenter

import android.support.v4.app.FragmentActivity
import vn.android.thn.gbkids.model.api.GBTubeRequestCallBack
import vn.android.thn.gbkids.model.api.request.GBTubeRequest
import vn.android.thn.gbkids.model.api.response.GBTubeResponse
import vn.android.thn.gbkids.model.api.response.NewResponse
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbyoutubelibrary.entity.SearchParam
import vn.android.thn.gbyoutubelibrary.entity.YoutubeApiName
import vn.android.thn.library.net.GBRequestError


//
// Created by NghiaTH on 2/27/19.
// Copyright (c) 2019

class NewVideoPresenter(mvp: SearchMvp, mActivity: FragmentActivity?) :
    PresenterBase<NewVideoPresenter.SearchMvp>(mvp, mActivity) {
    var nextPageToken = ""
    fun loadNew(isShowPopup: Boolean = true){
        if (isShowPopup) {
            showLoading()
        }
        val api = GBTubeRequest("new",mActivity!!)
        val param = SearchParam("kids song")
//        param.type = "video"
//        param.order ="date"
        param.pageToken = nextPageToken
        api.mParameters = param.toParamRequest()
        api.get().execute(object : GBTubeRequestCallBack {
            override fun onRequestError(errorRequest: GBRequestError, request: GBTubeRequest) {
                if (isShowPopup) {
                    hideLoading()
                }
            }

            override fun onResponse(httpCode: Int, response: GBTubeResponse, request: GBTubeRequest) {

                mMvp!!.onSearch(response.toResponse(NewResponse::class)!!.data)
                if (isShowPopup) {
                    hideLoading()
                }
            }
        })

    }
    interface SearchMvp : MVPBase {
        fun onSearch(result: MutableList<VideoTable>)
    }
}

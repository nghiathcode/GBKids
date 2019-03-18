package vn.android.thn.gbkids.presenter

import android.support.v4.app.FragmentActivity
import vn.android.thn.gbfilm.views.dialogs.YoutubeDialog
import vn.android.thn.gbkids.App
import vn.android.thn.gbkids.model.api.GBRequestName
import vn.android.thn.gbkids.model.api.GBTubeRequestCallBack
import vn.android.thn.gbkids.model.api.request.GBTubeRequest
import vn.android.thn.gbkids.model.api.response.DeviceRegisterResponse
import vn.android.thn.gbkids.model.api.response.GBTubeResponse
import vn.android.thn.library.net.GBRequestError
import vn.android.thn.library.views.activity.GBActivity


//
// Created by NghiaTH on 2/26/19.
// Copyright (c) 2019

open class PresenterBase<T : MVPBase>(var mMvp: T?, var mActivity: FragmentActivity?){
     var app = App.getInstance()

   fun showLoading(){
       if (mActivity!= null)
       if (mActivity is GBActivity){
           (mActivity as GBActivity).viewManager.showDialog(YoutubeDialog.newInstance())
       }
   }
    fun hideLoading(){
        if (mActivity!= null)
            if (mActivity is GBActivity){
                (mActivity as GBActivity).viewManager.hideDialog()
            }
    }
    fun refreshToken(apiName:String){
        val api = GBTubeRequest(GBRequestName.REFRESH_TOKEN,null)
        api.get().execute(object : GBTubeRequestCallBack {
            override fun onResponse(httpCode: Int, response: GBTubeResponse, request: GBTubeRequest) {
                response.toResponse(DeviceRegisterResponse::class)
                onRefreshComplete(apiName)
            }

            override fun onRequestError(errorRequest: GBRequestError, request: GBTubeRequest) {
                hideLoading()
            }

        })
    }
    open fun onRefreshComplete(requestName:String){

    }
}

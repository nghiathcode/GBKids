package vn.android.thn.gbkids.presenter

import android.support.v4.app.FragmentActivity
import vn.android.thn.gbkids.model.api.GBTubeRequestCallBack
import vn.android.thn.gbkids.model.api.param.RegisterParam
import vn.android.thn.gbkids.model.api.request.DeviceRegisterRequest
import vn.android.thn.gbkids.model.api.request.GBTubeRequest
import vn.android.thn.gbkids.model.api.response.DeviceRegisterResponse
import vn.android.thn.gbkids.model.api.response.GBTubeResponse
import vn.android.thn.library.net.GBRequestError


//
// Created by NghiaTH on 3/12/19.
// Copyright (c) 2019

class MainPresenter(mvp: MainMvp, mActivity: FragmentActivity?) :
    PresenterBase<MainPresenter.MainMvp>(mvp, mActivity) {
    fun register(){
        showLoading()
        var param = RegisterParam()
        param.appID =app.getAppId()
        param.deviceID = app.getDeviceId()
        param.deviceName = app.getDeviceName()
        param.deviceType = app.getDeviceType()
        param.deviceVersion = app.getOsVersion()
        param.appVersion = app.getVersionName()
        val api = DeviceRegisterRequest(param,mActivity!!)

        api.post<DeviceRegisterRequest>().execute(object :GBTubeRequestCallBack{
            override fun onResponse(httpCode: Int, response: GBTubeResponse, request: GBTubeRequest) {
                hideLoading()
                response.toResponse(DeviceRegisterResponse::class)
                mMvp!!.onRegister()
            }

            override fun onRequestError(errorRequest: GBRequestError, request: GBTubeRequest) {
                hideLoading()
            }

        })
    }
    interface MainMvp : MVPBase {
        fun onRegister()
    }
}

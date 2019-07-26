package vn.android.thn.gbkids.presenter

import android.support.v4.app.FragmentActivity
import vn.android.thn.gbkids.constants.ResponseCode
import vn.android.thn.gbkids.model.api.GBRequestName
import vn.android.thn.gbkids.model.api.GBTubeRequestCallBack
import vn.android.thn.gbkids.model.api.param.RegisterParam
import vn.android.thn.gbkids.model.api.request.DeviceRegisterRequest
import vn.android.thn.gbkids.model.api.request.GBTubeRequest
import vn.android.thn.gbkids.model.api.response.DeviceRegisterResponse
import vn.android.thn.gbkids.model.api.response.GBTubeResponse
import vn.android.thn.gbkids.model.api.response.NewResponse
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.library.net.GBRequestError

class MainPresenter(mvp: MainMvp, mActivity: FragmentActivity?) :
    PresenterBase<MainPresenter.MainMvp>(mvp, mActivity) {
    fun register(){
//        showLoading()
        var param = RegisterParam()
        param.appID =app.getAppId()
        param.deviceID = app.getDeviceId()
        param.deviceName = app.getDeviceName()
        param.deviceType = app.getDeviceType()
        param.deviceVersion = app.getOsVersion()
        param.appVersion = app.getVersionName()
        val api = DeviceRegisterRequest(param,mActivity!!)

        api.post<DeviceRegisterRequest>().execute(object : GBTubeRequestCallBack {
            override fun onResponse(httpCode: Int, response: GBTubeResponse, request: GBTubeRequest) {
//                hideLoading()
                response.toResponse(DeviceRegisterResponse::class)
                mMvp!!.onRegister()
            }

            override fun onRequestError(errorRequest: GBRequestError, request: GBTubeRequest) {
//                hideLoading()
            }

        })
    }
    fun loadNew(offset:Int = 0){

        val api = GBTubeRequest(GBRequestName.NEW,mActivity!!)
        api.addHeader("offset",offset.toString())
        api.get().execute(object : GBTubeRequestCallBack {
            override fun onRequestError(errorRequest: GBRequestError, request: GBTubeRequest) {

            }

            override fun onResponse(httpCode: Int, response: GBTubeResponse, request: GBTubeRequest) {

                if (httpCode == 200){
                    val result = response.toResponse(NewResponse::class)!!
                    if (result!!.error.errorCode == ResponseCode.NO_ERROR){
                        mMvp!!.onNew(result.data,result.offset)
//                        if (isShowPopup) {
//                            hideLoading()
//                        }
                    } else if (result!!.error.errorCode == ResponseCode.TOKEN_EXPIRED){
                        refreshToken(request.mRequestName)
                    } else if (result!!.error.errorCode == ResponseCode.TOKEN_REGISTER){
                        registerToken(request.mRequestName)
                    } else{
//                        if (isShowPopup) {
//                            hideLoading()
//                        }
                    }
                } else {
                    mMvp!!.onNetworkFail()
                }
            }
        })

    }
    interface MainMvp : MVPBase {
        fun onRegister()
        fun onNew(result: MutableList<VideoTable>, offset:Int = -1)
    }
}
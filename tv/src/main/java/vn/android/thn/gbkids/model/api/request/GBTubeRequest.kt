package vn.android.thn.gbkids.model.api.request

import android.support.v4.app.FragmentActivity
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Response
import vn.android.thn.gbkids.App
import vn.android.thn.gbkids.constants.Constants
import vn.android.thn.gbkids.model.api.GBTubeRequestCallBack
import vn.android.thn.gbkids.model.api.response.GBTubeResponse
import vn.android.thn.library.net.GBRequest
import vn.android.thn.library.net.GBRequestError
import vn.android.thn.library.utils.GBLog
import vn.android.thn.library.utils.GBUtils
import java.io.IOException


//
// Created by NghiaTH on 3/12/19.
// Copyright (c) 2019

open class GBTubeRequest: GBRequest {

    var app = App.getInstance()
    var dataBody: Any? = null
    constructor(apiName:String,context: FragmentActivity?,addHeader:Boolean = true) : super(apiName,context) {
        if (app.appSetting()!= null && addHeader) {
            addHeader("token", app.appSetting()!!.token)

        }
        addHeader("appId", app.getAppId())
        addHeader("osType","0")
        addHeader("deviceType","1")
        addHeader("appVersion",app.getVersionName())
    }
    override fun getDomain(): String {
        return Constants.DOMAIN
    }

    override fun getVersion(): String {
        return "v1.0"
    }

    override fun getPath(): String? {
        return "api"
    }

    override fun getBodyPost(): String {
        if (dataBody == null) {
            return ""
        } else{
            return app.gson.toJson(dataBody)
        }
    }

    override fun execute() {
        GBLog.info("url_request",makeUrl(),app.isDebugMode())
        GBLog.info("request_param_Header", Gson().toJson(mHeader),app.isDebugMode())
        if (!GBUtils.isEmpty(dataBody)) {
            GBLog.info("request_param_body", Gson().toJson(getBodyPost()), app.isDebugMode())
        }
        super.execute()
    }

    override fun execute(requestListener: Any) {
        super.execute(requestListener)
    }
    override fun onFailure(call: Call, e: IOException) {
        if (mRequestListener != null) {
            val callBack = mRequestListener as GBTubeRequestCallBack
            if (mContext != null) {
                mContext!!.runOnUiThread {
                    callBack.onRequestError(
                        GBRequestError.NETWORK_LOST,
                        this
                    )
                }
            } else {
                callBack.onRequestError(
                    GBRequestError.NETWORK_LOST,
                    this
                )
            }
        }
    }

    override fun onResponse(call: Call, response: Response) {
        val body = response!!.body()?.string()
        GBLog.info("onResponse",body!!,app.isDebugMode())
        if (mRequestListener != null && response.code() == 200) {
            val callBack = mRequestListener as GBTubeRequestCallBack
            if (mContext != null) {
                mContext!!.runOnUiThread {
                    callBack!!.onResponse(response.code(),
                        GBTubeResponse(body!!,responseType),
                        this
                    )
                }
            } else {
                callBack!!.onResponse(response.code(),GBTubeResponse(body!!,responseType), this)
            }
        } else {
            if (mRequestListener != null) {
                val callBack = mRequestListener as GBTubeRequestCallBack
                if (mContext != null) {
                    mContext!!.runOnUiThread {
                        callBack.onRequestError(
                            GBRequestError.NETWORK_LOST,
                            this
                        )
                    }
                } else {
                    callBack.onRequestError(
                        GBRequestError.NETWORK_LOST,
                        this
                    )
                }
            }
        }
    }
}

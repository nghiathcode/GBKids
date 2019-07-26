package vn.android.thn.gbkids.model.api.response

import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import vn.android.thn.gbkids.App
import vn.android.thn.gbkids.model.entity.ErrorResponseEntity
import vn.android.thn.library.net.GBResponseType
import vn.android.thn.library.utils.GBLog
import vn.android.thn.library.utils.GBUtils
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance


//
// Created by NghiaTH on 2/26/19.
// Copyright (c) 2019

open class GBTubeResponse() {
    val TAG = "GBTubeResponse"
    lateinit var responseType: GBResponseType
    lateinit var dataResponse: String
    var app = App.getInstance()
    lateinit var error:ErrorResponseEntity
    init {

    }

    constructor(response: String, responseType: GBResponseType) : this() {
        this.dataResponse = response
        this.responseType = responseType
        error = ErrorResponseEntity()
    }

    open fun <T : GBTubeResponse> toResponse(clazz: KClass<out T>, callBackError: Any? = null): T? {
        val response: GBTubeResponse
        try {
            response = clazz.createInstance()
            response.dataResponse = dataResponse
            response.responseType = responseType
            response.error = error
            response.parser()
            return response
        } catch (e: InstantiationException) {
            if (e.message != null) {
                GBLog.error(TAG, e.message!!, app.isDebugMode())
            } else {
                GBLog.error(TAG, "toResponse InstantiationException")
            }

            e.printStackTrace()
            return null
        } catch (e: IllegalAccessException) {
            if (e.message != null) {
                GBLog.error(TAG, e.message!!)
            } else {
                GBLog.error(TAG, "toResponse IllegalAccessException")
            }
            return null
        }

    }

    fun has(jsonObject: JSONObject, key: String): Boolean {
        if (jsonObject.has(key)) {
            return !jsonObject.isNull(key)
        }
        return false;
    }

    fun parser() {
        if (responseType == GBResponseType.JSON) {
            if (!GBUtils.isEmpty(dataResponse)) {
                val dataJson = JSONTokener(dataResponse).nextValue()
                if (dataJson is JSONObject) {
                    if (has(dataJson,"appStatus")){
                        val appStatus = dataJson.getJSONObject("appStatus")
                        if (has(appStatus,"status")){
                            app.appStatus =appStatus.getInt("status")
                        }

                    }
                    if (has(dataJson,"myDevice")){
                        val jMyDevice = dataJson.getJSONObject("myDevice")
                        if (has(jMyDevice,"playCount")){
                            app.myDevice.playCount = jMyDevice.getLong("playCount")
                        }
                        if (has(jMyDevice,"showDownLoad")){
                            app.myDevice.showDownLoad = jMyDevice.getInt("showDownLoad")
                        }
                        if (has(jMyDevice,"showAD")){
                            app.myDevice.showAD = jMyDevice.getInt("showAD")
                        }

                    }
                    if (has(dataJson,"countPlay")){
                        app.playCount =dataJson.getLong("countPlay")
                    }
                    if (has(dataJson, "error")) {
                        error = Gson().fromJson<ErrorResponseEntity>(dataJson.get("error").toString(),ErrorResponseEntity::class.java)
                        if (error.errorCode == 100){
                            if (has(dataJson, "data")) {
                                var data = dataJson.get("data")
                                if (data is JSONObject) {
                                    onJsonData(data)
                                } else if (data is JSONArray) {
                                    onJsonArrayData(data)
                                }
                            }
                        } else {
                            onErrorData(error)
                        }
                    }

                } else if (dataJson is JSONArray) {
                    onJsonArrayData(dataJson)
                }
            }
        } else {
            onTextData(dataResponse)
        }
    }

    open fun onJsonData(data: JSONObject) {}
    open fun onErrorData(data: ErrorResponseEntity) {

    }
    open fun onJsonArrayData(data: JSONArray) {}
    open fun onTextData(data: String) {}
}

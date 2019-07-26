package vn.android.thn.commons

import com.google.gson.Gson
import okhttp3.*
import vn.android.thn.commons.response.GBTubeResponse
import vn.android.thn.gbkids.model.api.GBVideoRequestCallBack
import vn.android.thn.library.net.GBRequestError
import vn.android.thn.library.utils.GBLog
import vn.android.thn.library.utils.GBUtils
import java.io.IOException
import java.net.InetAddress

class GBVideoDownLoadRequest(apiName: String="execute_native_to_result") : GBVideoRequest(apiName, null) {
    val TAG_DOWN = "GBVideoDownLoadRequest_"
    init {
        addHeader("appId",app.getSettingApp().appId)
    }
    override fun onFailure(call: Call, e: IOException) {
        showInfoApi(app.isDebugMode())
        GBLog.error(TAG_DOWN+"onFailure","NETWORK_LOST",app.isDebugMode())
        var isConnectInternet = false
        if (app.isNetworkAvailable()){
            try {
                val ipAddr = InetAddress.getByName(ServerInfor.DOMAIN_CHECK_CONNECT);
                isConnectInternet = !ipAddr.equals("")
            }catch (e:Exception){
                isConnectInternet = false
            }
        }
        app.isConnectInternet = isConnectInternet
        if (mRequestListener != null) {
            val callBack = mRequestListener as GBVideoRequestCallBack
            if (isConnectInternet){
                callBack.onRequestError(
                    GBRequestError.DATA_ERROR,
                    this
                )
            }else{
                callBack.onRequestError(
                    GBRequestError.NETWORK_LOST,
                    this
                )
            }
        }


    }
    override
    fun showInfoApi(isShow:Boolean){
        GBLog.info(TAG_DOWN+"Url:",makeUrl(),isShow)
        GBLog.info(TAG_DOWN+"Header:",Gson().toJson(mHeader),isShow)
    }
    override fun onResponse(call: Call, response: Response) {
        showInfoApi(app.isDebugMode())
        val body = response!!.body()?.string()
        GBLog.info(TAG_DOWN+"onResponse",body!!,app.isDebugMode())
        val callBack = mRequestListener as GBVideoRequestCallBack
        callBack!!.onResponse(response.code(),GBTubeResponse(body!!,responseType), this)
    }

    override fun execute() {

        if (queryobject!= null){
            mHeader.put("queryobject",Gson().toJson(queryobject))
        }
        try {
            var builder = Request.Builder()

            builder = builder.url(makeUrl())
            if (mHeader.size > 0) {
                for (key in mHeader.keys) {
                    if (!GBUtils.isEmpty(mHeader[key])){
                        builder = builder.addHeader(key, mHeader[key]!!)

                    }

                }
            }
            if (mMethod.equals("POST", true)||mMethod.equals("DELETE", true)) {
                var body: RequestBody?
                if (!GBUtils.isEmpty(getBodyPost())) {
                    body = RequestBody.create(MediaType.parse(mediaType), getBodyPost())
                } else {
                    if (mParameters.size > 0) {
                        var formBody = FormBody.Builder()
                        for (key in this.mParameters.keys) {
                            if (!GBUtils.isEmpty(mParameters[key])) {
                                formBody.add(key, mParameters[key]!!)
                            }
                        }
                        formBody.build()
                        body = formBody.build()
                    } else {
                        body = RequestBody.create(MediaType.parse(mediaType), "")
                    }
                }
                builder.method(mMethod, body)
            } else {
                builder.method("GET", null)
            }
            var request: Request = builder.build()

            val response=mClient.newCall(request).execute()
            if (response.code() == 200){
                if (response.body() != null) {
                    if (mRequestListener != null) {
                        showInfoApi(app.isDebugMode())
                        val body = response!!.body()?.string()
                        GBLog.info(TAG_DOWN+"onResponse",body!!,app.isDebugMode())
                        val callBack = mRequestListener as GBVideoRequestCallBack
                        callBack!!.onResponse(response.code(),GBTubeResponse(body!!,responseType), this)
                    }
                }
            } else {
                var isConnectInternet = false
                if (app.isNetworkAvailable()){
                    try {
                        val ipAddr = InetAddress.getByName(ServerInfor.DOMAIN_CHECK_CONNECT);
                        isConnectInternet = !ipAddr.equals("")
                    }catch (e:Exception){
                        isConnectInternet = false
                    }
                }
                app.isConnectInternet = isConnectInternet
                if (mRequestListener != null) {
                    val callBack = mRequestListener as GBVideoRequestCallBack
                    if (isConnectInternet){
                        callBack.onRequestError(
                            GBRequestError.DATA_ERROR,
                            this
                        )
                    }else{
                        callBack.onRequestError(
                            GBRequestError.NETWORK_LOST,
                            this
                        )
                    }
                }
            }

        } catch (e:Exception){
            exceptionError()
//            var isConnectInternet = false
//            if (app.isNetworkAvailable()){
//                try {
//                    val ipAddr = InetAddress.getByName(ServerInfor.DOMAIN_CHECK_CONNECT);
//                    isConnectInternet = !ipAddr.equals("")
//                }catch (e:Exception){
//                    isConnectInternet = false
//                }
//            }
//            app.isConnectInternet = isConnectInternet
//            showInfoApi(app.isDebugMode())
//            GBLog.error(TAG_DOWN+"onFailure","NETWORK_LOST",app.isDebugMode())
//            val callBack = mRequestListener as GBVideoRequestCallBack
//            if (isConnectInternet){
//                callBack.onRequestError(
//                    GBRequestError.DATA_ERROR,
//                    this
//                )
//            }else{
//                callBack.onRequestError(
//                    GBRequestError.NETWORK_LOST,
//                    this
//                )
//            }
        }
    }

}
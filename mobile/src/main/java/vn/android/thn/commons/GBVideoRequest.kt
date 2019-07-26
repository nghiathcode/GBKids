package vn.android.thn.commons

import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Response
import vn.android.thn.commons.response.GBTubeResponse
import vn.android.thn.gbkids.model.api.GBVideoRequestCallBack
import vn.android.thn.gbkids.views.listener.ApiStateListener
import vn.android.thn.library.net.GBRequest
import vn.android.thn.library.net.GBRequestError
import vn.android.thn.library.utils.GBLog
import java.io.IOException
import java.net.InetAddress
import java.util.concurrent.TimeUnit

open class GBVideoRequest(apiName: String, context: FragmentActivity?) : GBRequest(apiName, context) {
    val TAG = "GBVideoRequest_"
    var app = App.getInstance()
    var mApiStateListener: ApiStateListener? = null
    var queryobject:Any? = null
    var dataBody: Any? = null
    constructor(apiName: String, context: FragmentActivity?, apiStateListener: ApiStateListener) : this(
        apiName,
        context
    ) {
        this.mApiStateListener = apiStateListener
    }
    init {
        mClient = OkHttpClient().newBuilder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).writeTimeout(30,
            TimeUnit.SECONDS).build()

        mParameters = HashMap()
        mHeader = HashMap()
    }
    override fun getDomain(): String {
        return ServerInfor.DOMAIN//"http://192.168.100.27:8080"
    }

    override fun getVersion(): String {
        return ""
    }

    override fun getPath(): String? {
        return "mobile"
    }

    override fun getBodyPost(): String {
        if (dataBody == null) {
            return ""
        } else{
            return app.gson.toJson(dataBody)
        }
    }

    override fun onFailure(call: Call, e: IOException) {
        showInfoApi(app.isDebugMode())
        GBLog.error(TAG+"onFailure","NETWORK_LOST",app.isDebugMode())
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
            if (mContext != null) {
                mContext!!.runOnUiThread {
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

                    endRequest()
                }
            } else {
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
                endRequest()
            }
        }

    }
    open fun showInfoApi(isShow:Boolean){
        GBLog.info(TAG+"Url:",makeUrl(),isShow)
        GBLog.info(TAG+"Header:",Gson().toJson(mHeader),isShow)
    }
    override fun onResponse(call: Call, response: Response) {
        if (response.code() == 200){
            try{
                showInfoApi(app.isDebugMode())
                val body = response!!.body()?.string()
                GBLog.info(TAG+"onResponse",body!!,app.isDebugMode())

                if (mRequestListener != null && response.code() == 200) {
                    val callBack = mRequestListener as GBVideoRequestCallBack
                    if (mContext != null) {
                        mContext!!.runOnUiThread {
                            callBack!!.onResponse(response.code(),
                                GBTubeResponse(body!!,responseType),
                                this
                            )
                            endRequest()
                        }
                    } else {
                        callBack!!.onResponse(response.code(),GBTubeResponse(body!!,responseType), this)
                        endRequest()
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
                        if (mContext != null) {
                            mContext!!.runOnUiThread {
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
                                endRequest()
                            }
                        } else {
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
                            endRequest()
                        }
                    }
                }
            }catch (e:Exception){
                GBLog.error(TAG+"onFailure","Exception",app.isDebugMode())
                endRequest()
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
                if (mContext != null) {
                    mContext!!.runOnUiThread {
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
                        endRequest()
                    }
                } else {
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
                    endRequest()
                }
            }
        }



    }

    override fun exceptionError() {
        super.exceptionError()
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

        endRequest()
    }

    override fun execute() {
        startRequest()
        if (queryobject!= null){
            mHeader.put("queryobject",Gson().toJson(queryobject))
        }
        super.execute()
    }

    open fun startRequest() {
        if (mApiStateListener != null) {
            mApiStateListener!!.onStartApi()
        }
    }

    open fun endRequest() {
        if (mApiStateListener != null) {
            mApiStateListener!!.onEndApi()
        }
    }
}
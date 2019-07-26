package vn.android.thn.gbkids.views.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import vn.android.thn.commons.App
import vn.android.thn.commons.GBRealm
import vn.android.thn.commons.GBVideoRequest
import vn.android.thn.commons.listener.DownloadListener
import vn.android.thn.commons.response.GBTubeResponse
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.model.SettingEntity
import vn.android.thn.gbkids.model.api.GBVideoRequestCallBack
import vn.android.thn.gbkids.model.realm.RealmSetting
import vn.android.thn.gbkids.model.response.SettingResponse

import vn.android.thn.library.net.GBRequestError
import vn.android.thn.library.utils.GBUtils

class SplashActivity:FragmentActivity() , DownloadListener {
    override fun onComplete() {
        App.getInstance().mDownloadListener = null
        val intent = Intent(applicationContext,MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_loading)
        App.getInstance().mDownloadListener = this

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener{ task ->
            if (!task.isSuccessful) {
                App.getInstance().initApp()
                return@OnCompleteListener
            }

            // Get new Instance ID token
            val token = task.result?.token
            var setting = SettingEntity()
            setting.appId = App.getInstance().getAppId()
            setting.appVersion = App.getInstance().getVersionApp()
            setting.deviceId = App.getInstance().getDeviceId()
            setting.deviceType = App.getInstance().getDeviceType()
            setting.deviceName = App.getInstance().getDeviceName()
            setting.deviceVersion = App.getInstance().getOsVersion()
            setting.dateRequest =GBUtils.dateNow()
            setting.token = token!!
            sendServer(setting)
            // Log and toast
        })
    }
    fun sendServer(setting:SettingEntity){
        val api =  GBVideoRequest("register",this)
        api.dataBody = setting
        api.post().execute(object : GBVideoRequestCallBack{
            override fun onRequestError(errorRequest: GBRequestError, request: GBVideoRequest) {
                App.getInstance().initApp()
            }

            override fun onResponse(httpCode: Int, response: GBTubeResponse, request: GBVideoRequest) {
                var setting = response.toResponse(SettingResponse::class,null)!!.setting
                var settingTable = RealmSetting()
                settingTable.settingEntity = Gson().toJson(setting)
                GBRealm.save(settingTable)
                App.getInstance().initApp()
            }
        })
    }
}
package vn.android.thn.gbkids.views.dialogs

import android.os.Handler
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import vn.android.thn.commons.App
import vn.android.thn.commons.GBRealm
import vn.android.thn.commons.GBVideoRequest
import vn.android.thn.commons.response.GBTubeResponse
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.model.SettingEntity
import vn.android.thn.gbkids.model.api.GBVideoRequestCallBack
import vn.android.thn.gbkids.model.realm.RealmSetting
import vn.android.thn.gbkids.model.response.SettingResponse
import vn.android.thn.library.net.GBRequestError
import vn.android.thn.library.utils.GBUtils
import vn.android.thn.library.views.dialogs.GBDialogFragment

class DownLoadDialog: GBDialogFragment() {
    var app = App.getInstance()
    override fun initView() {
        isCancelable = false
        Handler().post {
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener{ task ->
                if (!task.isSuccessful) {
                    App.getInstance().initApp()
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                var realmSetting = RealmSetting.getObject()
                var setting = SettingEntity()
                setting.appId = App.getInstance().getAppId()
                setting.appVersion = App.getInstance().getVersionApp()
                setting.deviceId = App.getInstance().getDeviceId()
                setting.deviceType = App.getInstance().getDeviceType()
                setting.deviceName = App.getInstance().getDeviceName()
                setting.deviceVersion = App.getInstance().getOsVersion()
                setting.dateRequest = GBUtils.dateNow()
                setting.dateInstall = GBUtils.dateNow()
                setting.token = token!!
                if (realmSetting!= null){
                    val settingLocal =app.gson.fromJson<SettingEntity>(realmSetting!!.settingEntity,SettingEntity::class.java)
                    setting.point = settingLocal.point
                    setting.isReceiverPush = settingLocal.isReceiverPush
                    setting.downLoadAllow = settingLocal.downLoadAllow
                    if (!GBUtils.isEmpty(settingLocal.dateInstall)){
                        setting.dateInstall = settingLocal.dateInstall
                    }
                }
                sendServer(setting)
                // Log and toast
            })
        }

    }

    override fun dialogName(): String {
        return "DownLoadDialog"
    }
    override fun styleDialog(): Int {
        return R.style.AppTheme_Flash
    }
    override fun layoutFileCommon(): Int {
        return R.layout.dialog_download_creen
    }

    fun sendServer(setting: SettingEntity){
        val api =  GBVideoRequest(String.format("register?isDebug=%s",App.getInstance().isDebugMode().toString()),activity)
        api.dataBody = setting
        api.post().execute(object : GBVideoRequestCallBack {
            override fun onRequestError(errorRequest: GBRequestError, request: GBVideoRequest) {
                app.initApp()
            }

            override fun onResponse(httpCode: Int, response: GBTubeResponse, request: GBVideoRequest) {
                var setting = response.toResponse(SettingResponse::class,null)!!.setting
                app.downLoadAllow = setting.downLoadAllow
                var settingTable = RealmSetting()
                settingTable.settingEntity = Gson().toJson(setting)
                GBRealm.save(settingTable)
                app.initApp()
            }
        })
    }
}
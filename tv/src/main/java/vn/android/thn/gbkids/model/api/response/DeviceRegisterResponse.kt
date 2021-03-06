package vn.android.thn.gbkids.model.api.response

import jp.co.tss21.monistor.models.GBDataBase
import org.json.JSONObject
import vn.android.thn.gbkids.model.db.AppSetting
import vn.android.thn.gbkids.model.db.VideoTable


//
// Created by NghiaTH on 3/12/19.
// Copyright (c) 2019

class DeviceRegisterResponse:GBTubeResponse() {
    var data: MutableList<VideoTable> = ArrayList<VideoTable>()
    override fun onJsonData(data: JSONObject) {
        var setting = AppSetting()
        if (has(data,"appID")){
            setting.appID =   data.getString("appID")
        }
        if (has(data,"token")){
            setting.token = data.getString("token")
        }
        GBDataBase.deleteTable(AppSetting::class.java)
        GBDataBase.resetSeq(AppSetting::class.java)
        GBDataBase.insert(setting)
    }
}

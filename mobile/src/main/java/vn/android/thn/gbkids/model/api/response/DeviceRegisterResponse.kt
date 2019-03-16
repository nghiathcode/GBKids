package vn.android.thn.gbkids.model.api.response

import jp.co.tss21.monistor.models.GBDataBase
import org.json.JSONObject
import vn.android.thn.gbkids.model.db.AppSetting
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbyoutubelibrary.entity.SearchEntity


//
// Created by NghiaTH on 3/12/19.
// Copyright (c) 2019

class DeviceRegisterResponse:GBTubeResponse() {
    var data: MutableList<VideoTable> = ArrayList<VideoTable>()
    override fun onJsonData(data: JSONObject) {
        if (has(data,"token")){
               var setting = AppSetting()
            setting.token = data.getString("token")
            GBDataBase.insert(setting)
        }
    }
}

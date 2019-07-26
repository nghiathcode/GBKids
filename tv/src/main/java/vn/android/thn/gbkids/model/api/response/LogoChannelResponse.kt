package vn.android.thn.gbkids.model.api.response

import com.google.gson.Gson
import jp.co.tss21.monistor.models.GBDataBase
import org.json.JSONArray
import org.json.JSONObject
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbkids.model.entity.ChannelLogoEntity
import vn.android.thn.gbkids.model.entity.ThumbnailEntity


//
// Created by NghiaTH on 3/12/19.
// Copyright (c) 2019

class LogoChannelResponse:GBTubeResponse() {
    var logo :ChannelLogoEntity? = null

    override fun onJsonData(data: JSONObject) {
        logo = Gson().fromJson<ChannelLogoEntity>(data.toString(),ChannelLogoEntity::class.java)
    }

}

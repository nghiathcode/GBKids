package vn.android.thn.gbkids.model.api.response

import com.google.gson.Gson
import jp.co.tss21.monistor.models.GBDataBase
import org.json.JSONArray
import org.json.JSONObject
import vn.android.thn.gbkids.model.db.FollowTable
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbkids.model.entity.ChannelLogoEntity
import vn.android.thn.gbkids.model.entity.ThumbnailEntity


//
// Created by NghiaTH on 3/12/19.
// Copyright (c) 2019

class ChannelListResponse:GBTubeResponse() {
    var data: MutableList<FollowTable> = ArrayList<FollowTable>()
    var offset = -1
    override fun onJsonData(data: JSONObject) {
        if (has(data,"offset")){
            offset = data.getInt("offset")
        }
        if (has(data,"channels")){
            onJsonArrayData(data.getJSONArray("channels"))
        }
    }
    override fun onJsonArrayData(data: JSONArray) {
        for (i in 0.. (data.length() -1)){
            var jObj =data.getJSONObject(i)
            var  obj = FollowTable()
            if(has(jObj,"channelId")){
                obj.channelID = jObj.getString("channelId")
            }
            if(has(jObj,"title")){
                obj.channelTitle = jObj.getString("title")
            }
            if(has(jObj,"thumbnails")){
                obj.thumbnails = jObj.get("thumbnails").toString()
            }

            this.data.add(obj)
        }
//        if (this.data.size>0){
//            GBDataBase.insertList(this.data)
//        }
    }
}

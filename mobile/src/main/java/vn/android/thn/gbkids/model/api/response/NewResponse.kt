package vn.android.thn.gbkids.model.api.response

import com.google.gson.Gson
import jp.co.tss21.monistor.models.GBDataBase
import org.json.JSONArray
import org.json.JSONObject
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbkids.model.entity.ThumbnailEntity


//
// Created by NghiaTH on 3/12/19.
// Copyright (c) 2019

class NewResponse:GBTubeResponse() {
    var data: MutableList<VideoTable> = ArrayList<VideoTable>()
    var offset = -1
    override fun onJsonData(data: JSONObject) {
        if (has(data,"offset")){
            offset = data.getInt("offset")
        }
        if (has(data,"videos")){
            onJsonArrayData(data.getJSONArray("videos"))
        }
    }
    override fun onJsonArrayData(data: JSONArray) {
        for (i in 0.. (data.length() -1)){
            var jObj =data.getJSONObject(i)
            var  obj = VideoTable()
            if(has(jObj,"videoID")){
                obj.videoID = jObj.getString("videoID")
            }
            if(has(jObj,"title")){
                obj.title = jObj.getString("title")
            }
            if(has(jObj,"description")){
                obj.description = jObj.getString("description")
            }
            if(has(jObj,"channelID")){
                obj.channelID = jObj.getString("channelID")
            }
            if(has(jObj,"thumbnails")){
                val  thumbnails =Gson().fromJson<ThumbnailEntity>(jObj.get("thumbnails").toString(),  ThumbnailEntity::class.java)
                obj.thumbnails = Gson().toJson(thumbnails)
            }
            if(has(jObj,"channelTitle")){
                obj.channelTitle = jObj.getString("channelTitle")
            }
            if(has(jObj,"publishedAt")){
                obj.publishedAt = jObj.getString("publishedAt")
            }
            if(has(jObj,"tags")){
                val tags = Gson().fromJson<ArrayList<String>>(jObj.get("tags").toString(),  ArrayList::class.java)
                obj.tags = Gson().toJson(tags)
            }
            if(has(jObj,"statistics")){
                val statistics = Gson().fromJson<HashMap<String,String>>(jObj.get("statistics").toString(),  HashMap::class.java)
                obj.statistics = Gson().toJson(statistics)
            }
            if(has(jObj,"isDelete")){
                obj.isDelete = jObj.getInt("isDelete")
            }
            if(has(jObj,"dateUpdate")){
                obj.dateUpdate = jObj.getString("dateUpdate")
            }
            this.data.add(obj)
        }
        if (this.data.size>0){
            GBDataBase.insertList(this.data)
        }
    }
}

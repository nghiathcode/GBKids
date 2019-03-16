package vn.android.thn.gbkids.model.api.request


//
// Created by NghiaTH on 3/13/19.
// Copyright (c) 2019

class StreamRequest:GBTubeRequest("get_video_info",null,false) {
    var requestIndex = 0
    override fun getDomain(): String {
        return "https://www.youtube.com"
    }

    override fun getVersion(): String {
        return ""
    }

    override fun getPath(): String? {
        return ""
    }
}

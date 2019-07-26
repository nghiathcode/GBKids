package vn.android.thn.gbkids.model.api.response

import vn.android.thn.library.utils.GBLog
import java.io.UnsupportedEncodingException
import java.net.URLDecoder


//
// Created by NghiaTH on 3/13/19.
// Copyright (c) 2019

class StreamResponse:GBTubeResponse() {
    var listUrl = ArrayList<String>()
    override fun onTextData(data: String) {
        var data_decode: String
        try {
            data_decode = URLDecoder.decode(URLDecoder.decode(data, "UTF-8"), "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            data_decode = ""
        }
        val index_url_encoded_fmt_stream_map = data_decode.indexOf("url_encoded_fmt_stream_map")

        try{
            val data = data_decode.substring(index_url_encoded_fmt_stream_map, data_decode.length)
            val url_list = data.split("https://")
//        GBLog.info("Stream_url", App.getInstance().gson.toJson(data_decode),app.isDebugMode())
            var url_stream = ArrayList<String>()
            for (i in 0 until url_list.size) {
                if (url_list[i].contains("ratebypass=yes")) {
                    val fix_url = StringBuilder()
                    fix_url.append("https://")
                    fix_url.append(url_list[i].substring(0, url_list[i].indexOf(";")))
                    url_stream.add(fix_url.toString())
                }
            }
            if(url_list.size>0){
                listUrl.addAll(url_stream)
            }
        }catch (e:Exception){
            GBLog.info("Exception",e.message!!)
            listUrl = ArrayList<String>()
        }



    }
}

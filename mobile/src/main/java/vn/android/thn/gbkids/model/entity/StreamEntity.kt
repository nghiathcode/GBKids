package vn.android.thn.gbkids.model.entity

import java.io.Serializable


//
// Created by NghiaTH on 4/3/19.
// Copyright (c) 2019

class StreamEntity:Serializable {
    var url:String = ""
    var quality:Int = -1
    constructor(url:String,quality:Int){
        this.url = url
        this.quality = quality
    }
}

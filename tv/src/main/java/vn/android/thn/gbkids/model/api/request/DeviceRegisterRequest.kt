package vn.android.thn.gbkids.model.api.request

import android.support.v4.app.FragmentActivity
import vn.android.thn.gbkids.model.api.GBRequestName
import vn.android.thn.gbkids.model.api.param.RegisterParam


//
// Created by NghiaTH on 3/12/19.
// Copyright (c) 2019

class DeviceRegisterRequest(var param: RegisterParam, context: FragmentActivity):GBTubeRequest(GBRequestName.REGISTER,context,false) {
    init {
        this.dataBody = param
    }
}

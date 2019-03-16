package vn.android.thn.gbkids.views.activity

import android.content.Context
import android.os.Bundle
import vn.android.thn.gbkids.App
import vn.android.thn.gbkids.R
import vn.android.thn.library.views.GBViewManager
import vn.android.thn.library.views.activity.GBActivity


//
// Created by NghiaTH on 3/12/19.
// Copyright (c) 2019

abstract class ActivityBase: GBActivity() {
    var app = App.getInstance()
    override fun layoutFileCommon(): Int {
        return R.layout.activity_base
    }

    override fun isDebugMode(): Boolean {

        return app.isDebugMode()
    }

    override fun contentId(): Int {
        return R.id.fragment_view
    }

    override fun attachBaseContext(newBase: Context?) {
        viewManager = GBViewManager(supportFragmentManager, contentId(), this, isDebugMode())
        super.attachBaseContext(newBase)
    }


}


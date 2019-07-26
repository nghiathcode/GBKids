package vn.android.thn.gbkids.presenter

import androidx.fragment.app.FragmentActivity
import io.realm.Realm
import vn.android.thn.commons.App
import vn.android.thn.gbfilm.views.dialogs.YoutubeDialog
import vn.android.thn.gbkids.views.fragment.BaseFragment
import vn.android.thn.gbkids.views.listener.ApiStateListener
import vn.android.thn.library.views.activity.GBActivity


//
// Created by NghiaTH on 2/26/19.
// Copyright (c) 2019

open abstract class PresenterBase<T:BaseFragment>(var view: T?=null, var mActivity: FragmentActivity?= null) : ApiStateListener {
    var realm = Realm.getDefaultInstance()
    val MAX_ROW = 30
    override fun onStartApi() {
        showLoading()
    }

    override fun onEndApi() {
        hideLoading()
    }


    var app = App.getInstance()
    fun showLoading() {
        if (mActivity != null)
            if (mActivity is GBActivity) {
                (mActivity as GBActivity).viewManager.showDialog(YoutubeDialog.newInstance())
            }
    }

    fun hideLoading() {
        if (mActivity != null)
            if (mActivity is GBActivity) {
                (mActivity as GBActivity).viewManager.hideDialog()
            }
    }
    abstract fun initView()
}

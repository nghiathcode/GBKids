package vn.android.thn.gbkids.presenter

import android.support.v4.app.FragmentActivity
import vn.android.thn.gbfilm.views.dialogs.YoutubeDialog
import vn.android.thn.gbkids.App
import vn.android.thn.library.views.activity.GBActivity


//
// Created by NghiaTH on 2/26/19.
// Copyright (c) 2019

open class PresenterBase<T : MVPBase>(var mMvp: T?, var mActivity: FragmentActivity?){
     var app = App.getInstance()

   fun showLoading(){
       if (mActivity!= null)
       if (mActivity is GBActivity){
           (mActivity as GBActivity).viewManager.showDialog(YoutubeDialog.newInstance())
       }
   }
    fun hideLoading(){
        if (mActivity!= null)
            if (mActivity is GBActivity){
                (mActivity as GBActivity).viewManager.hideDialog()
            }
    }
}

package vn.android.thn.gbfilm.views.dialogs

import android.os.Bundle
import vn.android.thn.gbkids.R

import vn.android.thn.library.views.dialogs.GBDialogContentEntity
import vn.android.thn.library.views.dialogs.GBDialogFragment


//
// Created by NghiaTH on 2/26/19.
// Copyright (c) 2019

class YoutubeDialog:GBDialogFragment() {
    companion object {
        fun newInstance(dialogContent: GBDialogContentEntity): YoutubeDialog {
            val fragment = YoutubeDialog()
            fragment.isDismiss = dialogContent.isDismiss
            var data: Bundle = Bundle()
            fragment.layoutId = dialogContent.layoutId
            data.putInt("layoutId",fragment.layoutId );
            fragment.arguments=data
            fragment.dialogContent = dialogContent
            return fragment
        }
        fun newInstance(): YoutubeDialog {
            val fragment = YoutubeDialog()

            return fragment
        }
    }
    override fun initView() {

    }

    override fun dialogName(): String {
        return "YoutubeDialog"
    }

    override fun layoutFileCommon(): Int {
        return R.layout.dialog_data_loading
    }
}

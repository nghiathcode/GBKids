package vn.android.thn.gbfilm.views.dialogs

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import vn.android.thn.gbkids.R
import vn.android.thn.library.utils.GBUtils

import vn.android.thn.library.views.dialogs.GBDialogContentEntity
import vn.android.thn.library.views.dialogs.GBDialogFragment


//
// Created by NghiaTH on 2/26/19.
// Copyright (c) 2019

class YoutubeDialog:GBDialogFragment() {
    private  var title_view: TextView? = null
    private  var message_view: TextView? = null
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
        title_view = findViewById<TextView>(R.id.title)
        if (title_view!= null){
            if (dialogContent!= null) {
                if (!GBUtils.isEmpty(dialogContent!!.title)) {
                    title_view!!.text = dialogContent!!.title
                } else {
                    title_view!!.visibility = View.INVISIBLE
                }
            } else{
                title_view!!.visibility = View.INVISIBLE
            }
        }
        message_view = findViewById<TextView>(R.id.txt_message_dialog)
        if (message_view!= null){
            if (dialogContent!= null) {
                message_view!!.text = dialogContent!!.message
            }
        }
        if (dialogContent!= null) {
            if (dialogContent!!.listButton.size>0) {
                for (idButton in dialogContent!!.listButton.keys) {
                    var button = findViewById<Button>(idButton)
                    if (button != null) {
                        if (!GBUtils.isEmpty(dialogContent!!.listButton.get(idButton))) {
                            button.text = dialogContent!!.listButton.get(idButton)
                        }
                        if (dialogContent!!.buttonClick != null) {
                            button.setOnClickListener(dialogContent!!.buttonClick)
                        }
                    }
                }
            }
        }

    }

    override fun dialogName(): String {
        return "YoutubeDialog"
    }

    override fun layoutFileCommon(): Int {
        return R.layout.dialog_data_loading
    }
//    override fun styleDialog(): Int {
//        return android.R.style.Theme_Black_NoTitleBar_Fullscreen
//    }

}

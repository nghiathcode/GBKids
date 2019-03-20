package vn.android.thn.gbkids.views.dialogs

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.views.listener.SearchListener
import vn.android.thn.library.views.dialogs.GBDialogFragment


//
// Created by NghiaTH on 3/19/19.
// Copyright (c) 2019

class HistoryKeyWordDialog : GBDialogFragment() {
    lateinit var ed_key_word:EditText
    lateinit var listener:SearchListener
    var keyword = ""
    override fun initView() {
        isCancelable = true
        ed_key_word = findViewById<EditText>(R.id.ed_key_word)!!
        ed_key_word.requestFocus()
        ed_key_word.isSelected = true
//        viewManager.showKeyboard()
        ed_key_word.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, actionId: Int, p2: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    listener.searchKeyWord(ed_key_word.text.toString().trim())
                    return true
                }
                return false
            }

        })
        ed_key_word.setText(keyword)
    }

    override fun styleDialog(): Int {
        return R.style.AppTheme
    }
    override fun dialogName(): String {
        return "HistoryKeyWordDialog"
    }

    override fun layoutFileCommon(): Int {
        return R.layout.dialog_history_keyword
    }

}

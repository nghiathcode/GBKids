package vn.android.thn.gbkids.views.dialogs

import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import jp.co.tss21.monistor.models.GBDataBase
import vn.android.thn.gbfilm.views.listener.ListItemListener
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.model.db.KeyWordHistory
import vn.android.thn.gbkids.views.adapter.HistorySearchAdapter
import vn.android.thn.gbkids.views.listener.SearchListener
import vn.android.thn.library.utils.GBUtils
import vn.android.thn.library.views.dialogs.GBDialogFragment


//
// Created by NghiaTH on 3/19/19.
// Copyright (c) 2019

class HistoryKeyWordDialog : GBDialogFragment(), ListItemListener {
    lateinit var ed_key_word:EditText
    lateinit var listener:SearchListener
    private lateinit var mListView: RecyclerView
    private lateinit var adapter: HistorySearchAdapter
    private var listData:MutableList<KeyWordHistory> = ArrayList<KeyWordHistory>()
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
                    if (!GBUtils.isEmpty(ed_key_word.text.toString().trim())) {
                        listener.searchKeyWord(ed_key_word.text.toString().trim())
                        var data = KeyWordHistory()
                        data.keyword = ed_key_word.text.toString().trim()
                        data.dateUpdate = GBUtils.dateNow()
                        GBDataBase.insert(data)
                        return true
                    } else {
                        return false
                    }
                }
                return false
            }

        })
        ed_key_word.setText(keyword)
        listData = KeyWordHistory.allData()
        mListView = findViewById<RecyclerView>(R.id.list)!!
        mListView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(activity!!)
        mListView.setLayoutManager(mLayoutManager)
        mListView.setItemAnimator(DefaultItemAnimator())
        adapter = HistorySearchAdapter(activity!!,listData)
        adapter.listener = this
        mListView.adapter = adapter
    }

    override fun onItemClick(obj: Any, pos: Int) {
        listener.searchKeyWord((obj as KeyWordHistory ).keyword)
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

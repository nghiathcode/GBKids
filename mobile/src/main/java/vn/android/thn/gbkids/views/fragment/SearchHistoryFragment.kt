package vn.android.thn.gbkids.views.fragment

import android.support.v4.app.FragmentTransaction
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.views.activity.MainActivity
import vn.android.thn.gbkids.views.view.ToolBarViewType


//
// Created by NghiaTH on 3/16/19.
// Copyright (c) 2019

class SearchHistoryFragment:BaseFragment() {


    override fun fragmentName(): String {
        return "SearchHistoryFragment"
    }

    override fun initView() {

    }

    override fun loadData() {
        (activity as MainActivity).ed_key_word.requestFocus()
        (activity as MainActivity).ed_key_word.isSelected = true

        viewManager.showKeyboard()
    }

    override fun firstInit() {
//        viewManager.showKeyboard()

    }

    override fun layoutFileResourceCommon(): Int {
        return R.layout.fragment_search_history
    }

    override fun setAnimationCustom(animationCustom: FragmentTransaction) {

    }
    override fun showToolBarViewType(): ToolBarViewType {
        return ToolBarViewType.SEARCH_KEYWORD
    }
}

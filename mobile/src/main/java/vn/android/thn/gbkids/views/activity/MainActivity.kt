package vn.android.thn.gbkids.views.activity

import android.os.Bundle
import android.view.View
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.presenter.MainPresenter
import vn.android.thn.gbkids.views.fragment.NewFragment
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.Toolbar
import android.widget.EditText
import vn.android.thn.gbkids.R.id.toolbar
import vn.android.thn.gbkids.views.fragment.SearchHistoryFragment
import vn.android.thn.gbkids.views.view.ToolBarView
import vn.android.thn.gbkids.views.view.ToolBarViewType


//
// Created by NghiaTH on 2/26/19.
// Copyright (c) 2019

class MainActivity : ActivityBase(), MainPresenter.MainMvp {
    override fun apiError() {

    }

    override fun onNetworkFail() {

    }

    override fun onRegister() {
        viewManager.addView(NewFragment::class)
    }

    lateinit var presenter: MainPresenter
    lateinit var ed_key_word:EditText
    lateinit var mn_action_search:View
    lateinit var view_search_bar:View
    override fun onCreate(savedInstanceState: Bundle?) {
        presenter = MainPresenter(this,this)
        super.onCreate(savedInstanceState)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        showToolBar()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
            viewManager.hideKeyboard()
        }

        view_search_bar = findViewById(R.id.view_search_bar)
        mn_action_search = findViewById(R.id.mn_action_search)
        ed_key_word = findViewById(R.id.ed_key_word)
        mn_action_search.setOnClickListener {
            viewManager.pushView(SearchHistoryFragment::class)
        }
    }
    fun toolBarViewMode(toolBarView: ToolBarView = ToolBarView.AUTO_HIDE){
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        val params_toolbar = toolbar.getLayoutParams() as AppBarLayout.LayoutParams

        if (toolBarView == ToolBarView.AUTO_HIDE){
            findViewById<View>(R.id.toolbar).visibility = View.VISIBLE
            params_toolbar.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP or AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)
            val view = findViewById<View>(R.id.fragment_view)
            val params = view.getLayoutParams() as CoordinatorLayout.LayoutParams
            params.setBehavior(AppBarLayout.ScrollingViewBehavior())
            view.requestLayout()

        } else if(toolBarView == ToolBarView.HIDE){
            findViewById<View>(R.id.toolbar).visibility = View.GONE
        } else {
            findViewById<View>(R.id.toolbar).visibility = View.VISIBLE
            params_toolbar.setScrollFlags(AppBarLayout.LayoutParams.WRAP_CONTENT)
            val view = findViewById<View>(R.id.fragment_view)
            val params = view.getLayoutParams() as CoordinatorLayout.LayoutParams
            params.setBehavior(AppBarLayout.ScrollingViewBehavior())
            view.requestLayout()
        }
        toolbar.layoutParams = params_toolbar
        toolbar.requestLayout()
    }
     fun showToolBar(){
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        if (getSupportActionBar() != null){
            if (supportFragmentManager.backStackEntryCount == 0){
                getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
                getSupportActionBar()!!.setDisplayShowHomeEnabled(true)
                toolbar.setNavigationIcon(null)
            } else {
                getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
                getSupportActionBar()!!.setDisplayShowHomeEnabled(true)
                toolbar.setNavigationIcon(R.drawable.ico_mn_back)
            }


        }

    }
    fun showToolBarViewType(toolBarViewType: ToolBarViewType = ToolBarViewType.NORMAL){
        if (toolBarViewType ==ToolBarViewType.NORMAL ){
            mn_action_search.visibility = View.VISIBLE
            view_search_bar.visibility = View.GONE

        } else if (toolBarViewType ==ToolBarViewType.SEARCH_KEYWORD ){
            mn_action_search.visibility = View.GONE
            view_search_bar.visibility = View.VISIBLE
        } else {
            mn_action_search.visibility = View.VISIBLE
            view_search_bar.visibility = View.GONE
        }
    }
    override fun loadData() {
    }

    override fun initView() {
        if (app.appSetting()!= null) {
            viewManager.addView(NewFragment::class)
        } else {
            presenter.register()
        }
    }

    override fun initCommon() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        viewManager.hideKeyboard()
    }
    override fun setThemeApp() {
        setTheme(R.style.AppTheme)
    }

}

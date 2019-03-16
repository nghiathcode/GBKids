package vn.android.thn.gbkids.views.activity

import android.os.Bundle
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.presenter.MainPresenter
import vn.android.thn.gbkids.views.fragment.NewFragment


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
    override fun onCreate(savedInstanceState: Bundle?) {
        presenter = MainPresenter(this,this)
        super.onCreate(savedInstanceState)

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

    override fun setThemeApp() {
//        setTheme(R.style.AppTheme)
    }

}

package vn.android.thn.gbkids.views.fragment

import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import vn.android.thn.gbkids.App
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.presenter.MVPBase
import vn.android.thn.library.views.fragment.GBFragment


//
// Created by NghiaTH on 3/12/19.
// Copyright (c) 2019

abstract class BaseFragment: GBFragment() , MVPBase {
    var app = App.getInstance()
    private  var txt_title_base: TextView? = null
    private  var btn_menu_left: ImageView? = null
    private  var menu: ImageView? = null
    var drawer_layout: DrawerLayout? = null

    override fun isDebugMode(): Boolean {
        return app.isDebugMode()
    }

    override fun layoutFileResourceCommon(): Int {
        return R.layout.fragment_base
    }


    override fun viewCommonID(): Int {
        return R.id.content_view
    }

    override fun contentId(): Int {
        return 0
    }

    override fun initViewCommon() {
        txt_title_base = findViewById(R.id.txt_title_base)
        btn_menu_left = findViewById(R.id.btn_menu_left)
        menu = findViewById(R.id.menu)
        drawer_layout = findViewById(R.id.drawer_layout)
        //
        if (txt_title_base!= null){
            txt_title_base!!.text = getTitle()
        }
        if (btn_menu_left!= null){
            btn_menu_left!!.setOnClickListener {
                if (!drawer_layout!!.isDrawerOpen(GravityCompat.START)) {
                    drawer_layout!!.openDrawer(GravityCompat.START)
                } else {
                    drawer_layout!!.closeDrawers()
                }

            }
        }
        findViewById<View>(R.id.btn_back)!!.setOnClickListener {
            onBack()
        }
        hideBackButton(isShowButtonBack())
    }
    open fun isShowButtonBack(): Boolean {
        if (activity!!.supportFragmentManager.backStackEntryCount == 0) {
            return false
        }
        return true
    }
    open fun hideBackButton(isShow: Boolean) {
        if (isShow) {
            findViewById<View>(R.id.btn_back)!!.visibility = View.VISIBLE
            findViewById<View>(R.id.btn_menu_left)!!.visibility = View.GONE
            findViewById<View>(R.id.left_menu)!!.visibility = View.GONE
            drawer_layout!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        } else {
            drawer_layout!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            findViewById<View>(R.id.btn_menu_left)!!.visibility = View.VISIBLE
            findViewById<View>(R.id.btn_back)!!.visibility = View.GONE
            findViewById<View>(R.id.left_menu)!!.visibility = View.VISIBLE
        }
    }
    override fun setAnimationCustom(animationCustom: FragmentTransaction) {
        animationCustom.setCustomAnimations(
            R.anim.fragment_slide_right_enter,
            R.anim.fragment_slide_left_exit,
            R.anim.fragment_slide_left_enter,
            R.anim.fragment_slide_right_exit
        )

    }


    //
    override fun apiError() {

    }

    override fun onNetworkFail() {

    }
    /**
     * getTitle
     */
    abstract fun getTitle(): String
}


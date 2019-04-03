package vn.android.thn.gbkids.views.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.presenter.MainPresenter
import vn.android.thn.gbkids.views.fragment.NewFragment
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.Toolbar
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import thn.android.vn.draggableview.DraggableListener
import thn.android.vn.draggableview.DraggablePanel
import vn.android.thn.gbkids.R.id.toolbar
import vn.android.thn.gbkids.constants.Constants
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbkids.utils.LogUtils
import vn.android.thn.gbkids.views.dialogs.HistoryKeyWordDialog
import vn.android.thn.gbkids.views.fragment.PlayerFragment
import vn.android.thn.gbkids.views.fragment.PlayerVideoListFragment
import vn.android.thn.gbkids.views.fragment.SearchResultFragment
import vn.android.thn.gbkids.views.listener.SearchListener
import vn.android.thn.gbkids.views.view.ImageLoader
import vn.android.thn.gbkids.views.view.ToolBarView
import vn.android.thn.gbkids.views.view.ToolBarViewType


//
// Created by NghiaTH on 2/26/19.
// Copyright (c) 2019

class MainActivity : ActivityBase(), MainPresenter.MainMvp, SearchListener,ViewTreeObserver.OnGlobalLayoutListener {
    override fun onGlobalLayout() {
        if(top_view_video.measuredHeight>0) {
            top_view_video.getViewTreeObserver().removeGlobalOnLayoutListener(this)
            updateHeightVideoPlay(top_view_video.measuredHeight )
            LogUtils.info("MainActivity","onGlobalLayout:"+top_view_video.measuredHeight.toString())
        }
    }
    override fun searchKeyWord(q: String) {
        viewManager.hideDialog()
        var searchResult = SearchResultFragment()
        searchResult.keyword = q
        viewManager.pushView(searchResult)
    }
    override fun apiError() {

    }

    override fun onNetworkFail() {

    }

    override fun onRegister() {
        viewManager.addView(NewFragment::class)
    }

    lateinit var presenter: MainPresenter
    lateinit var txt_key_word:TextView
    lateinit var draggablePanel: DraggablePanel
    lateinit var mn_action_search:View
    lateinit var thumbnail_video:ImageView
    lateinit var view_search_bar:View
    var keyword = ""
     var player =PlayerFragment()
     var videoListPlayer =PlayerVideoListFragment()
    lateinit var top_view_video:View

    override fun onCreate(savedInstanceState: Bundle?) {
        presenter = MainPresenter(this,this)
        super.onCreate(savedInstanceState)
        draggablePanel = findViewById(R.id.draggable_panel)!!

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        showToolBar()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
            viewManager.hideKeyboard()
        }
        top_view_video = findViewById(R.id.top_view_video)
        view_search_bar = findViewById(R.id.view_search_bar)
        mn_action_search = findViewById(R.id.mn_action_search)
        txt_key_word = findViewById(R.id.txt_key_word)
        mn_action_search.setOnClickListener {
//            viewManager.pushView(SearchHistoryFragment::class)
            val searchFragment = HistoryKeyWordDialog()
            searchFragment.listener = this
            viewManager.showDialog(searchFragment)
        }
        thumbnail_video = findViewById(R.id.thumbnail_video)
        draggablePanel = findViewById(R.id.draggable_panel)!!
        txt_key_word.setOnClickListener {
            val searchFragment = HistoryKeyWordDialog()
            searchFragment.listener = this
            searchFragment.keyword =txt_key_word.text.toString()
            viewManager.showDialog(searchFragment)
        }
        initPlayView()

//        AdRequest.Builder.addTestDevice("BCB68136B98CF003B0B4965411508000")
//        AdRequest.Builder()
//            .addTestDevice("27438b00914bb2f60fb50d2d35bccbd5")
//            .build();
    }
    fun loadThumbnail(videoId:String){
        ImageLoader.loadImage(thumbnail_video, Constants.DOMAIN+"/thumbnail_high/"+videoId,videoId)
    }
    fun initPlayView(){

        draggablePanel.setFragmentManager(supportFragmentManager);
        draggablePanel.setTopFragment(player);
        draggablePanel.setBottomFragment(videoListPlayer);
        draggablePanel.setDraggableListener(object : DraggableListener{
            override fun onMaximized() {
                if (videoPlay!= null){
                    player.playNewVideo(videoPlay!!)
                    videoListPlayer.loadNext(videoPlay!!)
                    videoPlay = null
                }
            }

            override fun onMinimized() {

            }

            override fun onClosedToLeft() {
                player.closeVideo()
                draggablePanel.visibility = View.INVISIBLE
            }

            override fun onClosedToRight() {
                player.closeVideo()
                draggablePanel.visibility = View.INVISIBLE
            }

        })
        draggablePanel.initializeView()
    }
     var  videoPlay:VideoTable? = null
    fun showPlayer(videoId:VideoTable,isShow:Boolean = true){
        videoPlay = videoId
        if (draggablePanel.visibility !=View.VISIBLE) {
            top_view_video.viewTreeObserver.addOnGlobalLayoutListener( this)
            draggablePanel.visibility = View.INVISIBLE
            loadThumbnail(videoId.videoID)
        } else {
            draggablePanel.maximize()
        }
    }
    fun updateHeightVideoPlay(height:Int){
        if (draggablePanel.visibility !=View.VISIBLE) {
            draggablePanel.visibility = View.VISIBLE
        }
        draggablePanel.setTopViewHeight(height)
        draggablePanel.initializeView()
        draggablePanel.maximize()

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
    fun loadKeyWord(keyword:String){
        txt_key_word.text = keyword
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
        if (draggablePanel.visibility == View.VISIBLE && draggablePanel.isMaximized){
            draggablePanel.closeToLeft()
        } else {
            super.onBackPressed()
            viewManager.hideKeyboard()
        }
    }
    override fun setThemeApp() {
        setTheme(R.style.AppTheme)
    }
    fun addBannerAds(){}
    fun loadBannerAds(){}

}

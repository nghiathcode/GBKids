package vn.android.thn.gbkids.views.activity

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.presenter.MainPresenter
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
import vn.android.thn.gbkids.views.listener.SearchListener
import vn.android.thn.gbkids.views.view.ImageLoader
import vn.android.thn.gbkids.views.view.ToolBarView
import vn.android.thn.gbkids.views.view.ToolBarViewType
import android.os.StrictMode.setThreadPolicy
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import jp.co.tss21.monistor.models.GBDataBase
import vn.android.thn.gbfilm.views.dialogs.YoutubeDialog
import vn.android.thn.gbkids.constants.RequestCode
import vn.android.thn.gbkids.model.db.VideoDownLoad
import vn.android.thn.gbkids.views.fragment.*
import vn.android.thn.library.views.dialogs.GBDialogContentEntity


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
    lateinit var drawer_layout: DrawerLayout
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
    var videoTableDownload:VideoTable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        presenter = MainPresenter(this,this)
        super.onCreate(savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        drawer_layout  = findViewById(R.id.drawer_layout)
        draggablePanel = findViewById(R.id.draggable_panel)!!
        findViewById<View>(R.id.btn_list_download).setOnClickListener {
            player.closeVideo()
            draggablePanel.closeToLeft()
            viewManager.pushView(ListDownloadFragment::class)
        }
        findViewById<View>(R.id.btn_list_history).setOnClickListener {
            player.closeVideo()
            draggablePanel.closeToLeft()
            viewManager.pushView(ListHistoryFragment::class)
        }
        findViewById<View>(R.id.btn_list_follow).setOnClickListener {
            player.closeVideo()
            draggablePanel.closeToLeft()
            viewManager.pushView(ListFollowFragment::class)
        }
        findViewById<View>(R.id.btn_close).setOnClickListener {
            player.closeVideo()
            draggablePanel.closeToLeft()
            drawer_layout.closeDrawers()
        }
        findViewById<View>(R.id.btn_list_suggestions).setOnClickListener {
            player.closeVideo()
            draggablePanel.closeToLeft()
            drawer_layout.closeDrawers()
            viewManager.pushView(SuggestionsListFragment::class)
        }

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        showToolBar()
        toolbar.setNavigationOnClickListener {
            if (supportFragmentManager.backStackEntryCount == 0){
                drawer_layout!!.openDrawer(GravityCompat.START)
            } else {
                onBackPressed()
            }
            viewManager.hideKeyboard()
        }

        top_view_video = findViewById(R.id.top_view_video)
        view_search_bar = findViewById(R.id.view_search_bar)
        mn_action_search = findViewById(R.id.mn_action_search)
        txt_key_word = findViewById(R.id.txt_key_word)
        mn_action_search.setOnClickListener {
            player.closeVideo()
            draggablePanel.closeToLeft()
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
        player.listener = videoListPlayer
        draggablePanel.setFragmentManager(supportFragmentManager);
        draggablePanel.setTopFragment(player);
        draggablePanel.setBottomFragment(videoListPlayer);
        draggablePanel.setDraggableListener(object : DraggableListener{
            override fun onMaximized() {
                if(!playLocal){
                    if (videoPlay!= null){
                        player.playNewVideo(videoPlay!!)
                        videoListPlayer.loadNext(videoPlay!!)
                        videoPlay = null
                    }
                } else{
                    if (videoPlayDownLoad!= null){
                        player.playVideoLocal(videoPlayDownLoad!!)
                        videoListPlayer.loadVideoDownLoad(videoPlayDownLoad!!)
                        videoPlayDownLoad = null
                    }
                }

                drawer_layout!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }

            override fun onMinimized() {
                drawer_layout!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
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
    var  videoPlayDownLoad:VideoDownLoad? = null
    var playLocal = false
    fun showPlayer(videoId:VideoTable,isShow:Boolean = true){
        playLocal = false
        videoPlay = videoId
        if (draggablePanel.visibility !=View.VISIBLE) {
            top_view_video.viewTreeObserver.addOnGlobalLayoutListener( this)
            draggablePanel.visibility = View.INVISIBLE
            loadThumbnail(videoId.videoID)
        } else {
            draggablePanel.maximize()

        }
    }
    fun showPlayerDownLoad(videoId:VideoDownLoad,isShow:Boolean = true){
        playLocal = true
        videoPlayDownLoad = videoId
        if (draggablePanel.visibility !=View.VISIBLE) {
            top_view_video.viewTreeObserver.addOnGlobalLayoutListener( this)
            draggablePanel.visibility = View.INVISIBLE
//            loadThumbnail(videoId.videoID)
            ImageLoader.loadImage(thumbnail_video, videoPlayDownLoad!!.thumbnails,videoPlayDownLoad!!.videoID)
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
                toolbar.setNavigationIcon(R.drawable.ico_menu)
                drawer_layout!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                drawer_layout!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
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
            if (supportFragmentManager.backStackEntryCount == 0){
                if (drawer_layout!!.isDrawerOpen(GravityCompat.START)) {
                    drawer_layout!!.closeDrawers()
                    return
                }
            }
            super.onBackPressed()
            viewManager.hideKeyboard()
        }
    }
    override fun setThemeApp() {
        setTheme(R.style.AppTheme)
    }
    fun addBannerAds(){}
    fun loadBannerAds(){}
    fun requestStoragePermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            val lPermissions =
                    arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            val lPermissionsRead = arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE)
            val permissionCheck =
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val permissionCheckRead =
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED && permissionCheckRead == PackageManager.PERMISSION_GRANTED) {
                downLoadVideo()
            } else if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                ) {
                    //show popup require Permissions
                    val dialogContent= GBDialogContentEntity()
                    dialogContent.layoutId = R.layout.dialog_require_permissions
                    dialogContent.listButton.put(R.id.btn_dialog_left,"No")
                    dialogContent.listButton.put(R.id.btn_dialog_right,"Yes")
                    dialogContent.message = "please allow GBKids have Permissions on Storage"
                    dialogContent.buttonClick = object :View.OnClickListener{
                        override fun onClick(v: View?) {
                            if (v!= null){
                                val lPermissions = arrayOf<String>(
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                )
                                if (v.id == R.id.btn_dialog_left) {
                                    viewManager.hideDialog()
                                }
                                if (v.id == R.id.btn_dialog_right) {
                                    viewManager.hideDialog()
                                    ActivityCompat.requestPermissions(
                                            this@MainActivity,
                                            lPermissions,
                                            RequestCode.WRITE_EXTERNAL_STORAGE.value
                                    );
                                }
                            }
                        }
                    }
                    viewManager.showDialog(YoutubeDialog.newInstance(dialogContent))

                } else {
                    ActivityCompat.requestPermissions(
                            this,
                            lPermissions,
                            RequestCode.WRITE_EXTERNAL_STORAGE.value
                    )
                }
            }
        } else {
            downLoadVideo()
        }
    }
    fun downLoadVideo(){
        if (videoTableDownload!= null) {
            var data = GBDataBase.getObject(VideoDownLoad::class.java, "videoID=?", *arrayOf(videoTableDownload!!.videoID))
            if (data == null){
                data = VideoDownLoad()
                data.videoID = videoTableDownload!!.videoID
                data.channelID = videoTableDownload!!.channelID
                data.title = videoTableDownload!!.title
                data.channelTitle = videoTableDownload!!.channelTitle
                data.thumbnails = videoTableDownload!!.thumbnails
                data.save()
                app.downloadVideo(videoTableDownload!!.videoID)
            }
        }
    }
    fun checkDownload(videoTableDownload: VideoTable){
        this.videoTableDownload = videoTableDownload
        requestStoragePermissions()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.size != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == RequestCode.WRITE_EXTERNAL_STORAGE.value) {
                downLoadVideo()
                return
            }
        } else {
            if (requestCode == RequestCode.WRITE_EXTERNAL_STORAGE.value) {
                //show popup require Permissions
                val dialogContent= GBDialogContentEntity()
                dialogContent.layoutId = R.layout.dialog_require_permissions
                dialogContent.listButton.put(R.id.btn_dialog_left,"No")
                dialogContent.listButton.put(R.id.btn_dialog_right,"Yes")
                dialogContent.message = "please allow GBKids have Permissions on Storage"
                dialogContent.buttonClick = object :View.OnClickListener{
                    override fun onClick(v: View?) {
                        if (v!= null){
                            val lPermissions = arrayOf<String>(
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                            if (v.id == R.id.btn_dialog_left) {
                                viewManager.hideDialog()
                            }
                            if (v.id == R.id.btn_dialog_right) {
                                viewManager.hideDialog()
                                ActivityCompat.requestPermissions(
                                        this@MainActivity,
                                        lPermissions,
                                        RequestCode.WRITE_EXTERNAL_STORAGE.value
                                );
                            }
                        }
                    }
                }
                viewManager.showDialog(YoutubeDialog.newInstance(dialogContent))
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}

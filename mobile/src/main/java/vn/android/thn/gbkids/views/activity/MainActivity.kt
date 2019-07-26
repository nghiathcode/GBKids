package vn.android.thn.gbkids.views.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.MobileAds

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.firebase.iid.FirebaseInstanceId
import io.realm.Realm
import thn.android.vn.draggableview.DraggableListener
import thn.android.vn.draggableview.DraggablePanel
import vn.android.thn.commons.GBRealm
import vn.android.thn.commons.listener.DownloadListener
import vn.android.thn.commons.listener.FileDownloadListener
import vn.android.thn.commons.realm.RealmHistorySearch
import vn.android.thn.commons.realm.RealmTableDateUpdate
import vn.android.thn.commons.realm.RealmVideo
import vn.android.thn.commons.view.ToolBarView
import vn.android.thn.gbfilm.views.dialogs.YoutubeDialog
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.constants.RequestCode
import vn.android.thn.gbkids.model.realm.RealmSetting
import vn.android.thn.gbkids.views.dialogs.DownLoadDialog
import vn.android.thn.gbkids.views.dialogs.SearchDialog
import vn.android.thn.gbkids.views.fragment.*
import vn.android.thn.library.utils.GBLog
import vn.android.thn.library.utils.GBUtils
import vn.android.thn.library.views.dialogs.GBDialogContentEntity

class MainActivity : ActivityBase() , DownloadListener , SearchDialog.SearchListener , FileDownloadListener {
    override fun onComplete(videoId: String) {
        var item = RealmVideo.getObject(videoId)
        if (item!= null) {
            Toast.makeText(this,getString(R.string.msg_complete_download,item.title), Toast.LENGTH_SHORT).show()
        }
    }
    override fun onKeyWord(keyword: String) {
        if (GBUtils.isEmpty(keyword)){
            viewManager.hideDialog()
            return
        }
        var history = RealmHistorySearch()
        history.keyword = keyword
        history.dateUpdate = GBUtils.dateNow()
        GBRealm.save(history)
        val fragment = viewManager.getViewCurrent()
        if (fragment!= null){
            if (fragment is BaseFragment){
                fragment.onSearch(keyword)
            }
        }
        viewManager.hideDialog()
    }

    override fun loadKeyWord(): String {
        val fragment = viewManager.getViewCurrent()
        if (fragment!= null){
            if (fragment.childFragmentManager.backStackEntryCount>0){
                var result = fragment.childFragmentManager.findFragmentById(R.id.content_view)
                if (result!= null) {
                    if (result is SearchResultFragment) {
                        return result.currentKeyWord()
                    }
                }
            }

        }
        return ""
    }
    var videoDownLoad = ""
    var home = HomeFragment()
    var top = TopFragment()
    var my =MyFragment()
    var channel = ChannelFragment()
    var offline = ListVideoOfflineFragment()
    var videoListPlayer = ListVideoPlayFragment()
    lateinit var toolbar: Toolbar
    lateinit var btn_back:View
    lateinit var title_bar: TextView
    var tabCurrent = -1
    lateinit var draggablePanel: DraggablePanel
    lateinit var btn_search:View
    lateinit var top_view :ImageView
    lateinit var txt_keyword:TextView
    var player=PlayerFragment()
    lateinit var search_result_view:View
    lateinit var view_delete_action:View
    lateinit var view_no_data:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

    }
    lateinit var view_botton: TabLayout
    override fun onComplete() {
        app.mFileDownloadListener = this
        viewManager.hideDialog()
        app.mDownloadListener = null

//        if (tabCurrent == R.id.bottom_history) {
//            viewManager.pushViewToRoot(watched)
//        }
        loadFinish()
    }
    fun loadFinish(){

        GBLog.info("MainActivity","getScreenHeight():"+getScreenHeight(),isDebugMode())
        GBLog.info("MainActivity","getScreenWidth():"+ getScreenWidth(),isDebugMode())
        GBLog.info("MainActivity","getStatusBarHeight():"+ getStatusBarHeight(),isDebugMode())
        GBLog.info("MainActivity","view_botton_Height:"+ view_botton.height,isDebugMode())
        app.screenHeight = getScreenHeight()
        app.screenWidth = getScreenWidth()
        app.statusBarHeight = getStatusBarHeight()
        app.bottomHeight = view_botton.height
        setUpDraggablePanel()
        view_botton.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                view_no_data.visibility = View.GONE

                when(p0!!.position){
                    0->{
                        val fragment = viewManager.getViewCurrentByTag(home.fragmentName())
                        if (fragment!= null){
                            viewManager.addView(fragment)
                        } else{
                            viewManager.pushView(home)
                        }

                        tabCurrent = 0
                    }
                    1->{
                        val fragment = viewManager.getViewCurrentByTag(top.fragmentName())
                        if (fragment!= null){
                            viewManager.addView(fragment)
                        }else{
                            viewManager.pushView(top)
                        }

                        tabCurrent = 1
                    }
                    2->{
                        val fragment = viewManager.getViewCurrentByTag(channel.fragmentName())
                        if (fragment!= null){
                            viewManager.addView(fragment)
                        } else{
                            viewManager.pushView(channel)
                        }

                        tabCurrent = 2
                    }
                    3->{
                        val fragment = viewManager.getViewCurrentByTag(offline.fragmentName())
                        if (fragment!= null){
                            viewManager.addView(fragment)
                        } else{
                            viewManager.pushView(offline)
                        }

                        tabCurrent = 3
                    }
                    4->{
                        val fragment = viewManager.getViewCurrentByTag(my.fragmentName())
                        if (fragment!= null){
                            viewManager.addView(fragment)
                        } else{
                            viewManager.pushView(my)
                        }
                        tabCurrent = 4
                    }
                }
            }

        })

        if (tabCurrent == -1||tabCurrent == 0) {
            tabCurrent = 0
            viewManager.pushView(home)
        }
        if (tabCurrent ==1) {
            val fragment = viewManager.getViewCurrentByTag(top.fragmentName())
            if (fragment!= null){
                viewManager.addView(fragment)
            }else{
                viewManager.pushView(top)
            }

        }
        if (tabCurrent ==2) {
            val fragment = viewManager.getViewCurrentByTag(channel.fragmentName())
            if (fragment!= null){
                viewManager.addView(fragment)
            } else{
                viewManager.pushView(channel)
            }

        }
        if (tabCurrent ==3) {
            val fragment = viewManager.getViewCurrentByTag(offline.fragmentName())
            if (fragment!= null){
                viewManager.addView(fragment)
            } else{
                viewManager.pushView(offline)
            }

        }
        if (tabCurrent ==4) {
            val fragment = viewManager.getViewCurrentByTag(my.fragmentName())
            if (fragment!= null){
                viewManager.addView(fragment)
            } else{
                viewManager.pushView(my)
            }

        }
        btn_search.setOnClickListener {
            val search = SearchDialog()
            search.listener = this
            viewManager.showDialog(search)
        }
        txt_keyword.setOnClickListener {
            val search = SearchDialog()
            search.listener = this
            viewManager.showDialog(search)
        }

    }
    override fun initView() {
//        var setting = RealmTableDateUpdate.getObject()
//        if (setting != null){
//            if (!GBUtils.isEmpty(setting.dateDown)&&!GBUtils.isEmpty(setting.dateUp)){
//                loadFinish()
//                return
//            }
//        }
        app.mDownloadListener = this
        viewManager.showDialog(DownLoadDialog())
    }

    fun playVideo(video: RealmVideo){
        draggablePanel.maximize()
        if (app.videoCurrent == null){
            app.videoCurrent = video
            player.playVideo(app.videoCurrent!!)
        } else {
            if (!app.videoCurrent!!.videoID.equals(video.videoID,true)){
                app.videoCurrent = video
                player.playVideo(app.videoCurrent!!)
            }
        }
    }
    fun requestListVideo(video: RealmVideo){
        draggablePanel.setBottomHeight(view_botton.height)
        if (draggablePanel.visibility != View.VISIBLE) {
            draggablePanel.visibility = View.VISIBLE
        }

        playVideo(video)

    }
    override fun loadData() {

    }
    fun updateHeightVideoPlay(height:Int){
//        draggablePanel.setTopViewHeight(height)
//        draggablePanel.requestLayout()
//        draggablePanel.postInvalidate()
//        draggablePanel.initializeView()
    }

    override fun initCommon() {
        view_no_data = findViewById(R.id.view_no_data)
        view_delete_action = findViewById(R.id.view_delete_action)!!
        view_botton = findViewById(R.id.view_botton)
        btn_back = findViewById(R.id.btn_back)
        toolbar = findViewById(R.id.toolbar)
        btn_search = findViewById(R.id.btn_search)
        txt_keyword = findViewById(R.id.txt_keyword)
        search_result_view = findViewById(R.id.search_result_view)
        setSupportActionBar(toolbar)
        title_bar = findViewById(R.id.title_bar)
        top_view = findViewById(R.id.top_view)
        if (getSupportActionBar() != null) {
            getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
            getSupportActionBar()!!.setDisplayShowHomeEnabled(true)
            getSupportActionBar()!!.setDisplayShowTitleEnabled(false)
            toolBarViewMode()
            btn_back.setOnClickListener {
                onBackPressed()
                viewManager.hideKeyboard()
            }
            toolbar.setNavigationIcon(null)
            top_view.visibility = View.VISIBLE
        }
        draggablePanel = findViewById(R.id.draggable_panel)!!
        findViewById<View>(R.id.btn_clear_search)!!.setOnClickListener{
            val search = SearchDialog()
            search.listener = this
            search.isClear = true
            viewManager.showDialog(search)
        }

    }
    fun setUpDraggablePanel(){
        draggablePanel.setFragmentManager(supportFragmentManager)
        draggablePanel.setTopFragment(player)
        draggablePanel.setBottomFragment(videoListPlayer)
        draggablePanel.setDraggableListener(object : DraggableListener {
            override fun onMaximized() {
                GBLog.info("MainActivity","onMaximized:"+view_botton.height,isDebugMode())
                view_botton.visibility = View.GONE
                draggablePanel.setBottomHeight(view_botton.height)
                val layoutParams = draggablePanel.getLayoutParams() as RelativeLayout.LayoutParams
                layoutParams.bottomMargin =0
                draggablePanel.requestLayout()
                player.showController(true)
            }

            override fun onMinimized() {
                GBLog.info("MainActivity","onMinimized:"+view_botton.height,isDebugMode())
                view_botton.visibility = View.VISIBLE
                draggablePanel.setBottomHeight(0)
                val layoutParams = draggablePanel.getLayoutParams() as RelativeLayout.LayoutParams
                layoutParams.bottomMargin =view_botton.height
                draggablePanel.requestLayout()
                player.showController(false)
            }

            override fun onClosedToLeft() {
                GBLog.info("MainActivity","onClosedToLeft:"+view_botton.height,isDebugMode())
                view_botton.visibility = View.VISIBLE
                app.videoCurrent = null
                player.releasePlayer()
            }

            override fun onClosedToRight() {
                GBLog.info("MainActivity","onClosedToRight:"+view_botton.height,isDebugMode())
                view_botton.visibility = View.VISIBLE
                app.videoCurrent = null
                player.releasePlayer()
            }

        })
        draggablePanel.setTopViewHeight((app.screenHeight - app.statusBarHeight)/3)
        draggablePanel.isClickToMaximizeEnabled = true
        draggablePanel.initializeView()
        player.mListVideoPlayFragment = videoListPlayer

    }
    override fun setThemeApp() {
        setTheme(R.style.AppTheme)
    }

    @SuppressLint("WrongConstant")
    fun toolBarViewMode(toolBarView: ToolBarView = ToolBarView.AUTO_HIDE) {
        val params_toolbar = toolbar.getLayoutParams() as AppBarLayout.LayoutParams

        if (toolBarView == ToolBarView.AUTO_HIDE) {
            toolbar.visibility = View.VISIBLE
            params_toolbar.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP or AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)
            val view = findViewById<View>(R.id.fragment_view)
            val params = view.getLayoutParams() as CoordinatorLayout.LayoutParams
            params.setBehavior(AppBarLayout.ScrollingViewBehavior())
            view.layoutParams = params
            view.requestLayout()
        } else if (toolBarView == ToolBarView.HIDE) {
            findViewById<View>(R.id.toolbar).visibility = View.GONE
        } else {
            findViewById<View>(R.id.toolbar).visibility = View.VISIBLE
            params_toolbar.scrollFlags = AppBarLayout.LayoutParams.WRAP_CONTENT
            val view = findViewById<View>(R.id.fragment_view)
            val params = view.getLayoutParams() as CoordinatorLayout.LayoutParams
            params.setBehavior(AppBarLayout.ScrollingViewBehavior())
            view.layoutParams = params
            view.requestLayout()
        }
        toolbar.layoutParams = params_toolbar
        toolbar.requestLayout()
    }
    fun updateNoDataText(message:String){
        view_no_data.visibility = View.VISIBLE
        view_no_data.text = message
    }
    fun updateTitle(titleView: String) {
        view_no_data.visibility = View.GONE
        title_bar.text = ""
        val fragment = viewManager.getViewCurrent()
        txt_keyword.visibility = View.GONE
        search_result_view.visibility = View.GONE
        btn_search.visibility = View.VISIBLE
        if (fragment!= null){
            if (fragment.childFragmentManager.backStackEntryCount>0){
                top_view.visibility = View.GONE
                btn_back.visibility = View.VISIBLE
                var result = fragment.childFragmentManager.findFragmentById(R.id.content_view)
                if (result!= null){
                    if (result is SearchResultFragment){
                        txt_keyword.visibility = View.VISIBLE
                        search_result_view.visibility = View.VISIBLE
                        btn_search.visibility = View.GONE
                        txt_keyword.text = result.currentKeyWord()
                    } else{
                        btn_search.visibility = View.VISIBLE
                    }
                } else {
                    btn_search.visibility = View.VISIBLE
                }
            } else{
                btn_back.visibility = View.GONE
                top_view.visibility = View.VISIBLE
                btn_search.visibility = View.VISIBLE
            }
        }
    }
    fun updateTab(tabIndex:Int){
        view_botton.getTabAt(tabIndex)!!.select()
    }
    override fun onBackPressed() {
        view_no_data.visibility = View.GONE
        if (draggablePanel.visibility == View.VISIBLE && draggablePanel.isMaximized) {
            draggablePanel.minimize()
        } else {
            try {
                val fragment = viewManager.getViewCurrent()
                txt_keyword.visibility = View.GONE
                search_result_view.visibility = View.GONE
                btn_search.visibility = View.VISIBLE
                if (fragment != null){
                    if (fragment.childFragmentManager.backStackEntryCount>0){
                        fragment.childFragmentManager.popBackStackImmediate()
                        if (fragment.childFragmentManager.backStackEntryCount==0) {
                            btn_back.visibility = View.GONE
                            txt_keyword.visibility = View.GONE
                            search_result_view.visibility = View.GONE
                            top_view.visibility = View.VISIBLE
                        } else{
                            btn_back.visibility = View.VISIBLE
                            var result = fragment.childFragmentManager.findFragmentById(R.id.content_view)
                            if (result!= null){

                                if (result is SearchResultFragment){
                                    search_result_view.visibility = View.VISIBLE
                                    txt_keyword.visibility = View.VISIBLE
                                    txt_keyword.text = result.currentKeyWord()
                                    btn_search.visibility = View.GONE
                                } else{
                                    txt_keyword.visibility = View.GONE
                                    search_result_view.visibility = View.GONE
                                }
                            } else {
                                txt_keyword.visibility = View.GONE
                                search_result_view.visibility = View.GONE
                            }
                        }
                        return
                    }
                }
                if (fragment is HomeFragment){
                    if (supportFragmentManager.backStackEntryCount <=1){
                        finish()
                        return
                    }
                }
                viewManager.hideKeyboard()
                supportFragmentManager.popBackStackImmediate()
            }catch (e:Exception){
                finish()
            }


        }
    }

    fun requestStoragePermissions(videoDownLoad:String) {
        this.videoDownLoad = videoDownLoad
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
                    val dialogContent = GBDialogContentEntity()
                    dialogContent.layoutId = R.layout.dialog_require_permissions
                    dialogContent.listButton.put(R.id.btn_dialog_left, getString(R.string.lb_no))
                    dialogContent.listButton.put(R.id.btn_dialog_right, getString(R.string.lb_yes))
                    dialogContent.message = getString(R.string.msg_permission)
                    dialogContent.buttonClick = object : View.OnClickListener {
                        override fun onClick(v: View?) {
                            if (v != null) {
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
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.size != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == RequestCode.WRITE_EXTERNAL_STORAGE.value) {
                downLoadVideo()
                return
            }
        } else {
            if (requestCode == RequestCode.WRITE_EXTERNAL_STORAGE.value) {
                //show popup require Permissions
                val dialogContent = GBDialogContentEntity()
                dialogContent.layoutId = R.layout.dialog_require_permissions
                dialogContent.listButton.put(R.id.btn_dialog_left, getString(R.string.lb_no))
                dialogContent.listButton.put(R.id.btn_dialog_right, getString(R.string.lb_yes))
                dialogContent.message = getString(R.string.msg_permission)
                dialogContent.buttonClick = object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        if (v != null) {
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
                                )
                            }
                        }
                    }
                }
                viewManager.showDialog(YoutubeDialog.newInstance(dialogContent))
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    fun downLoadVideo() {
        if (!GBUtils.isEmpty(videoDownLoad)) {
            var data =
                RealmVideo.getObjectDownLoad(videoDownLoad)
            if (data != null) {
                app.downloadVideo(videoDownLoad)
                RealmVideo.updateDownload(videoDownLoad)
                videoDownLoad = ""
            }
        }
    }
    fun onIgnoreVideo(video: RealmVideo,videoPlay:RealmVideo) {
        view_delete_action.visibility = View.VISIBLE
        val txt_comment = findViewById<EditText>(R.id.txt_comment)
        findViewById<View>(R.id.btn_cancle)!!.setOnClickListener {
            view_delete_action.visibility = View.GONE
            viewManager.hideKeyboard()
        }
        findViewById<View>(R.id.btn_delete)!!.setOnClickListener {
            view_delete_action.visibility = View.GONE
            RealmVideo.ignoreVideo(video.videoID)
            requestListVideo(videoPlay)
            app.report(video.videoID,txt_comment.text.toString(),0)
            viewManager.hideKeyboard()
        }


    }
    fun onNoInternet(){
        draggablePanel.closeToLeft()
        updateTab(3)
    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }
}
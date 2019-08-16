package vn.android.thn.gbkids.views.fragment

import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.android.thn.commons.listener.ListItemListener
import vn.android.thn.commons.realm.RealmVideo
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.presenter.ListVideoPlayPresenter
import vn.android.thn.gbkids.views.activity.MainActivity
import vn.android.thn.gbkids.views.adapter.ListVideoPlayAdapter
import vn.android.thn.library.utils.GBLog

class ListVideoPlayFragment : BaseFragment(), ListItemListener {
    private lateinit var mListView: RecyclerView
    var presenter: ListVideoPlayPresenter? = null
    var currentItem = -1
    var mPlayerFragment:PlayerFragment? = null
    lateinit var data_loading:LinearLayout

    override fun titleView(): String {
        return "ListVideo"
    }

    override fun initView() {

        mListView = findViewById<RecyclerView>(R.id.list)!!
        mListView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(activity!!)
        mListView.setLayoutManager(mLayoutManager)
        mListView.setItemAnimator(DefaultItemAnimator())
        data_loading = findViewById<LinearLayout>(R.id.data_loading)!!
        mListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                GBLog.info("HomeFragment","onScrollStateChanged:"+newState,isDebugMode())
                when(newState){
                    RecyclerView.SCROLL_STATE_IDLE->{
                        if (recyclerView.layoutManager!= null){
                            var childCount = recyclerView.layoutManager!!.childCount
                            for (child in 0 until childCount){
                                if (recyclerView.layoutManager!!.getChildAt(child)!=null){
                                    var index = recyclerView.findContainingViewHolder( recyclerView.layoutManager!!.getChildAt(child)!!)!!.layoutPosition -1
                                    if (index>=0 && index<presenter!!.list.size) {
                                        GBLog.info(
                                            "HomeFragment",
                                            "SCROLL_STATE_IDLE:" + recyclerView.layoutManager!!.childCount + ":" + index + "title:" + presenter!!.list.get(
                                                index
                                            ).title,
                                            isDebugMode()
                                        )
                                        app.loadFirstStream(presenter!!.list.get(index).videoID)
                                    }
                                }
                            }
                        }

                    }
                    RecyclerView.SCROLL_STATE_DRAGGING->{
                        GBLog.info("HomeFragment","SCROLL_STATE_DRAGGING:",isDebugMode())
                    }
                    RecyclerView.SCROLL_STATE_SETTLING->{
                        GBLog.info("HomeFragment","SCROLL_STATE_SETTLING:",isDebugMode())
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                GBLog.info("HomeFragment","onScrolled:",isDebugMode())
                if (dx ==0 && dy ==0){
                    if (recyclerView.layoutManager!= null){
                        var childCount = recyclerView.layoutManager!!.childCount
                        for (child in 0 until childCount){
                            if (recyclerView.layoutManager!!.getChildAt(child)!=null){
                                var index = recyclerView.findContainingViewHolder( recyclerView.layoutManager!!.getChildAt(child)!!)!!.layoutPosition -1
                                if (index>=0 && index<presenter!!.list.size) {
                                    GBLog.info(
                                        "HomeFragment",
                                        "SCROLL_STATE_IDLE:" + recyclerView.layoutManager!!.childCount + ":" + index + "title:" + presenter!!.list.get(
                                            index
                                        ).title,
                                        isDebugMode()
                                    )
                                    app.loadFirstStream(presenter!!.list.get(index).videoID)
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    override fun loadData() {

    }

    override fun firstInit() {
        if (presenter == null) {
            presenter = ListVideoPlayPresenter(this, activity)
        }
    }
    fun onListVideo(adapter: ListVideoPlayAdapter){
        if (mListView.adapter== null){
            mListView.adapter = adapter
        } else{
            if(presenter!!.list.size >1) {
                this.mListView.recycledViewPool.clear()
                mListView.adapter!!.notifyDataSetChanged()
            }
        }
        if (mPlayerFragment!= null) {
            mPlayerFragment!!.listVideo = presenter!!.list
            mPlayerFragment!!.currentItem = 0
            mPlayerFragment!!.loadList()
        }
        if (presenter!!.list.size<=35){
            presenter!!.adapter.loadAD()
        }
    }
    fun loadNewList(){
        presenter!!.list.clear()
        if (mListView.adapter!= null) {
            this.mListView.recycledViewPool.clear()
            mListView.adapter!!.notifyDataSetChanged()
        }

        currentItem = 0
        presenter!!.loadListVideo()

    }
    override fun layoutFileResourceContent(): Int {
        return R.layout.fragment_list_video_play
    }
    //ListItemListener
    override fun onItemClick(obj: Any, pos: Int) {
        currentItem = pos
        GBLog.info("ListVideoPlayFragment","onItemClick:",isDebugMode())
        if (activity is MainActivity){
            (activity as MainActivity).requestListVideo(obj as RealmVideo)
        }

    }
    fun startLoading(){
        if (presenter!!.list.size ==0){
            data_loading.visibility = View.VISIBLE
        }

    }
    fun endLoading(){
        data_loading.visibility = View.GONE
    }
    fun download(videoId:String){
        if (activity is MainActivity){
            (activity as MainActivity).requestStoragePermissions(videoId)
        }
    }
    fun onIgnoreVideo(video: RealmVideo) {
        if (activity is MainActivity){
            (activity as MainActivity).onIgnoreVideo(video,presenter!!.list.get(0))
        }
    }
    override fun onResume() {
        presenter!!.resumeAD()
        super.onResume()
    }

    override fun onPause() {
        presenter!!.pauseAD()
        super.onPause()
    }

    override fun onDestroy() {
        presenter!!.destroyAD()
        super.onDestroy()
    }
}
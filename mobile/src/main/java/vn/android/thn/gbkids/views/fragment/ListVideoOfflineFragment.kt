package vn.android.thn.gbkids.views.fragment

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import vn.android.thn.commons.listener.ListItemListener
import vn.android.thn.commons.realm.RealmVideo
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.presenter.ListVideoOfflinePresenter
import vn.android.thn.gbkids.views.activity.MainActivity
import vn.android.thn.gbkids.views.adapter.ListVideoAdapter
import vn.android.thn.library.utils.GBLog

class ListVideoOfflineFragment : BaseFragment(), ListItemListener {
    private lateinit var mListView: RecyclerView
    var presenter: ListVideoOfflinePresenter? = null
    var currentItem = -1
    var listType = 0
    var keyword:String=""
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    override fun titleView(): String {
        return "ListVideo"
    }

    override fun initView() {
        if (activity is MainActivity){
            (activity as MainActivity).updateTab(3)
        }
        mListView = findViewById<RecyclerView>(R.id.list)!!
        mListView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(activity!!)
        mListView.setLayoutManager(mLayoutManager)
        mListView.setItemAnimator(DefaultItemAnimator())
        swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)!!
        swipeRefreshLayout.setColorSchemeResources(
            *intArrayOf(
                R.color.loading,
                R.color.loading,
                R.color.loading
            )
        )
        swipeRefreshLayout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                app.download()
                presenter!!.adapter.isLoadMore = false
                presenter!!.loadListVideoKeyWord(0,keyword)
            }

        })
        mListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                GBLog.info("HomeFragment","onScrollStateChanged:"+newState,isDebugMode())
                when(newState){
                    RecyclerView.SCROLL_STATE_IDLE->{
                        if (recyclerView.layoutManager!= null){
                            var childCount = recyclerView.layoutManager!!.childCount
                            for (child in 0 until childCount){
                                if (recyclerView.layoutManager!!.getChildAt(child)!=null){
                                    var index = recyclerView.findContainingViewHolder( recyclerView.layoutManager!!.getChildAt(child)!!)!!.layoutPosition
                                    if ( index<presenter!!.list.size) {
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
                                var index = recyclerView.findContainingViewHolder( recyclerView.layoutManager!!.getChildAt(child)!!)!!.layoutPosition

                                if ( index<presenter!!.list.size) {
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
        presenter!!.initView()

    }

    override fun loadData() {
        presenter!!.loadListVideoKeyWord(0,keyword)
    }

    override fun firstInit() {
        if (presenter == null) {
            presenter = ListVideoOfflinePresenter(this, activity,listType)
        }
    }
    fun onListVideo(adapter: ListVideoAdapter){
        swipeRefreshLayout.isRefreshing = false
        if (mListView.adapter== null){
            mListView.adapter = adapter

        } else{
            mListView.adapter!!.notifyDataSetChanged()
        }
        if (adapter.list.size == 0){
            updateNoDataText(getString(R.string.no_video_download))
        } else{
            hideNoData()
        }
    }

    override fun layoutFileResourceContent(): Int {
        return R.layout.fragment_list_video_offline
    }
    //ListItemListener
    override fun onItemClick(obj: Any, pos: Int) {
        currentItem = pos
        GBLog.info("ListVideoPlayFragment","onItemClick:",isDebugMode())
        if (activity!= null){
            if (activity is MainActivity){
                (activity as MainActivity).requestListVideo(obj as RealmVideo)
            }
        }
    }

    override fun fragmentName(): String {
        return "ListVideoOfflineFragment"
    }
    fun currentKeyWord():String{
        return keyword
    }
}
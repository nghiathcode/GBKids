package vn.android.thn.gbkids.views.fragment

import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import vn.android.thn.commons.listener.ListItemListener
import vn.android.thn.commons.realm.RealmVideo
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.presenter.HomePresenter
import vn.android.thn.gbkids.views.activity.MainActivity
import vn.android.thn.gbkids.views.adapter.HomeAdapter
import vn.android.thn.library.utils.GBLog

class HomeFragment:BaseFragment(),ListItemListener {


    var presenter:HomePresenter? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mListView: RecyclerView
    override fun initView() {
        if (activity is MainActivity){
            (activity as MainActivity).updateTab(0)
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
                presenter!!.onRefresh()
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
                GBLog.info("HomeFragment","onScrolled:dx="+dx+":dy="+dy,isDebugMode())
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
        presenter!!.loadNew()
    }

    override fun firstInit() {
        if (presenter == null)
            presenter = HomePresenter(this, activity!!)
    }
    fun onListVideo(adapter: HomeAdapter){
        if (mListView.adapter== null){
            mListView.adapter = adapter
        } else{
            mListView.adapter!!.notifyDataSetChanged()
        }
//        if (mListView.layoutManager!= null){
//            if (mListView.layoutManager!!.getChildAt(0)!=null){
//                var index = mListView.findContainingViewHolder( mListView.layoutManager!!.getChildAt(0)!!)!!.layoutPosition
//
//                GBLog.info("HomeFragment","SCROLL_STATE_IDLE:"+mListView.layoutManager!!.childCount+":"+index+"title:"+presenter!!.list.get(index).title,isDebugMode())
//            }
//        }

        swipeRefreshLayout.isRefreshing = false
    }
    override fun layoutFileResourceContent(): Int {
        return R.layout.fragment_home
    }

    override fun titleView(): String {
        return "HonePage"
    }
    override fun fragmentName(): String {
        return "HomeFragment"
    }
    override fun onItemClick(obj: Any, pos: Int) {
//        viewManager.pushViewChild(childFragmentManager,ListVideoPlayFragment::class,null,null,R.id.content_view)
        if (activity is MainActivity){
            (activity as MainActivity).requestListVideo(obj as RealmVideo)
        }
    }

    override fun setAnimationCustom(animationCustom: FragmentTransaction) {

    }
}
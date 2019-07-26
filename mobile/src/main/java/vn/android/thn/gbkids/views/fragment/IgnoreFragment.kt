package vn.android.thn.gbkids.views.fragment

import android.view.View
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import vn.android.thn.commons.listener.ListItemListener
import vn.android.thn.commons.realm.RealmVideo
import vn.android.thn.gbfilm.views.dialogs.YoutubeDialog
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.presenter.IgnorePresenter
import vn.android.thn.gbkids.views.adapter.HomeAdapter
import vn.android.thn.library.utils.GBLog
import vn.android.thn.library.views.dialogs.GBDialogContentEntity

class IgnoreFragment : BaseFragment(), ListItemListener {

    var presenter: IgnorePresenter? = null
    private lateinit var mListView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    override fun initView() {
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
                presenter!!.loadNew()
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
        presenter!!.loadNew()
    }

    override fun firstInit() {
        if (presenter == null)
            presenter = IgnorePresenter(this, activity!!)
    }

    fun onListVideo(adapter: HomeAdapter) {
        swipeRefreshLayout.isRefreshing = false
        if (mListView.adapter== null){
            mListView.adapter = adapter
        } else{
            mListView.adapter!!.notifyDataSetChanged()

        }
        if (adapter.list.size == 0){
            updateNoDataText(getString(R.string.no_video_delete))
        } else{
            hideNoData()
        }
    }

    override fun layoutFileResourceContent(): Int {
        return R.layout.fragment_home
    }

    override fun titleView(): String {
        return "TopPage"
    }

    override fun onItemClick(obj: Any, pos: Int) {
//        if (activity is MainActivity){
//            (activity as MainActivity).requestListVideo(obj as RealmVideo)
//        }
        var videoId = (obj as RealmVideo).videoID
        val dialogContent = GBDialogContentEntity()
        dialogContent.layoutId = R.layout.dialog_require_permissions
        dialogContent.listButton.put(R.id.btn_dialog_left, getString(R.string.lb_no))
        dialogContent.listButton.put(R.id.btn_dialog_right, getString(R.string.lb_yes))
        dialogContent.message = getString(R.string.msg_retore)
        dialogContent.buttonClick = object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (v != null) {
                    if (v.id == R.id.btn_dialog_left) {
                        viewManager.hideDialog()
                    }
                    if (v.id == R.id.btn_dialog_right) {
                        RealmVideo.restoreVideo(videoId)
                        viewManager.hideDialog()
                        presenter!!.adapter.isLoadMore = false
                        presenter!!.loadNew()
                    }
                }
            }
        }
        viewManager.showDialog(YoutubeDialog.newInstance(dialogContent))
    }

    override fun setAnimationCustom(animationCustom: FragmentTransaction) {

    }
}
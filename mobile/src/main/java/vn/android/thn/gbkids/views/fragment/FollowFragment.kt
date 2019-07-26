package vn.android.thn.gbkids.views.fragment

import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import vn.android.thn.commons.listener.ListItemListener
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.model.ChannelEntity
import vn.android.thn.gbkids.presenter.ChannelPresenter
import vn.android.thn.gbkids.presenter.FollowPresenter
import vn.android.thn.gbkids.views.activity.MainActivity
import vn.android.thn.gbkids.views.adapter.ChannelAdapter

class FollowFragment : BaseFragment(), ListItemListener {

    var presenter: FollowPresenter? = null
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
        presenter!!.initView()

    }

    override fun loadData() {
        presenter!!.loadNew()
    }

    override fun firstInit() {
        if (presenter == null)
            presenter = FollowPresenter(this, activity!!)
    }

    fun onListVideo(adapter: ChannelAdapter) {
        swipeRefreshLayout.isRefreshing = false
        if (mListView.adapter== null){
            mListView.adapter = adapter
        } else{
            mListView.adapter!!.notifyDataSetChanged()
        }
        if (adapter.list.size == 0){
            updateNoDataText(getString(R.string.no_video_follow))
        } else{
            hideNoData()
        }
    }

    override fun layoutFileResourceContent(): Int {
        return R.layout.fragment_home
    }
    override fun fragmentName(): String {
        return "FollowFragment"
    }
    override fun titleView(): String {
        return "Follow"
    }

    override fun onItemClick(obj: Any, pos: Int) {
        val listVideo = ListVideoFragment()
        listVideo.listType = 1
        listVideo.channelId = (obj as ChannelEntity).channelId
//        viewManager.pushView(listVideo)
        viewManager.pushViewChild(viewManager.getViewCurrent()!!.childFragmentManager,listVideo,null,null,R.id.content_view)
    }

    override fun setAnimationCustom(animationCustom: FragmentTransaction) {

    }
}
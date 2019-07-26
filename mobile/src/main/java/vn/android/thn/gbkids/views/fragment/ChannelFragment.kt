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
import vn.android.thn.gbkids.views.activity.MainActivity
import vn.android.thn.gbkids.views.adapter.ChannelAdapter

class ChannelFragment : BaseFragment(), ListItemListener {

    var presenter: ChannelPresenter? = null
    private lateinit var mListView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    override fun initView() {
        if (activity is MainActivity){
            (activity as MainActivity).updateTab(2)
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
            presenter = ChannelPresenter(this, activity!!)
    }

    fun onListVideo(adapter: ChannelAdapter) {
        swipeRefreshLayout.isRefreshing = false
        if (mListView.adapter== null){
            mListView.adapter = adapter
        } else{
            mListView.adapter!!.notifyDataSetChanged()
        }

    }

    override fun layoutFileResourceContent(): Int {
        return R.layout.fragment_home
    }
    override fun fragmentName(): String {
        return "ChannelFragment"
    }
    override fun titleView(): String {
        return "Channel"
    }

    override fun onItemClick(obj: Any, pos: Int) {
        val listVideo = ListVideoFragment()
        listVideo.listType = 1
        listVideo.channelId = (obj as ChannelEntity).channelId
//        viewManager.pushView(listVideo)
        viewManager.pushViewChild(childFragmentManager,listVideo,null,null,R.id.content_view)
    }

    override fun setAnimationCustom(animationCustom: FragmentTransaction) {

    }

}
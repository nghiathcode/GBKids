package vn.android.thn.gbkids.views.fragment

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import jp.co.tss21.monistor.models.GBDataBase
import vn.android.thn.gbfilm.views.listener.ListItemListener
import vn.android.thn.gbfilm.views.listener.LoadMoreListener
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.model.db.FollowTable
import vn.android.thn.gbkids.model.db.VideoDownLoad
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbkids.presenter.ChannelPresenter
import vn.android.thn.gbkids.views.activity.MainActivity
import vn.android.thn.gbkids.views.adapter.DownloadListAdapter
import vn.android.thn.gbkids.views.adapter.FollowListAdapter
import vn.android.thn.gbkids.views.adapter.HistoryListAdapter
import vn.android.thn.gbkids.views.adapter.PlayListAdapter
import java.util.ArrayList

class ListChannelFragment :BaseFragment(), ListItemListener ,ChannelPresenter.ChannelMvp, LoadMoreListener {
    override fun onLoadMore() {
        presenter.loadData(offset,false)
    }

    override fun onChannelList(listVideo: MutableList<FollowTable>, offset: Int) {
        list.addAll(listVideo)
        this.offset = offset
        if(list.size == 0){
            no_data.visibility = View.VISIBLE
            no_data.text = getString(R.string.txt_no_follow)
            mListView.visibility = View.GONE
        } else {
            mListView.visibility = View.VISIBLE
            no_data.visibility = View.GONE
        }
        if (offset!=-1){

            adapter.loadMore(true,this)
        } else {
            adapter.loadMore(false,this)
        }
        adapter.notifyDataSetChanged()
    }

    override fun onItemClick(obj: Any, pos: Int) {
//        (activity as MainActivity).showPlayer((obj as VideoTable),true)
        val bundle = Bundle()
        bundle.putString("channelId",(obj as FollowTable).channelID)
        viewManager.pushView(ListVideoChannelFragment::class,bundle)
    }
    var offset:Int = -1
    private lateinit var mListView: RecyclerView
    private lateinit var adapter: FollowListAdapter
    private var list: MutableList<FollowTable> = ArrayList<FollowTable>()
    private lateinit var no_data: TextView
    lateinit var presenter: ChannelPresenter
    override fun fragmentName(): String {
        return "ListChannelFragment"
    }

    override fun initView() {
        no_data = findViewById<TextView>(R.id.no_data)!!
        mListView = findViewById<RecyclerView>(R.id.list)!!
        mListView.adapter = adapter
        adapter.notifyDataSetChanged()
        val mLayoutManager = LinearLayoutManager(activity!!)
        mListView.setLayoutManager(mLayoutManager)
        mListView.setItemAnimator(DefaultItemAnimator())

    }

    override fun loadData() {
        if (!firstLoad){
//            adapter.loadMore(list,false, this)
            presenter.loadData(0,true)
        }

    }

    override fun firstInit() {
        presenter = ChannelPresenter(this,activity!!)
        adapter = FollowListAdapter(activity!!,list)
        adapter.listener = this
    }

    override fun layoutFileResourceCommon(): Int {
        return R.layout.fragment_download_list
    }
}
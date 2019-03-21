package vn.android.thn.gbkids.views.fragment

import android.support.v4.app.FragmentTransaction
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import vn.android.thn.gbfilm.views.listener.ListItemListener
import vn.android.thn.gbfilm.views.listener.LoadMoreListener
import vn.android.thn.gbkids.App
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbkids.presenter.NewVideoPresenter
import vn.android.thn.gbkids.views.activity.MainActivity
import vn.android.thn.gbkids.views.activity.VideoPlayerActivity
import vn.android.thn.gbkids.views.adapter.NewListAdapter
import vn.android.thn.gbkids.views.view.ToolBarView
import vn.android.thn.gbkids.views.view.ToolBarViewType
import vn.android.thn.gbyoutubelibrary.entity.ItemSearchEntity
import vn.android.thn.gbyoutubelibrary.entity.SearchEntity
import vn.android.thn.library.utils.GBLog

import java.util.ArrayList


//
// Created by NghiaTH on 2/27/19.
// Copyright (c) 2019

class NewFragment:BaseFragment(), NewVideoPresenter.SearchMvp, ListItemListener, LoadMoreListener {
    override fun onLoadMore() {
        presenter.loadNew(offset,false)
    }
    override fun onItemClick(obj: Any, pos: Int) {
//        val detail = VideoDetailFragment()
//        detail.videoId = (obj as VideoTable).videoID!!
//        viewManager.pushView(detail)

//        viewManager.startActivity(VideoPlayerActivity::class.java)
        (activity as MainActivity).showPlayer((obj as VideoTable),true)
    }

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mListView: RecyclerView
    private lateinit var adapter: NewListAdapter
    lateinit var presenter:NewVideoPresenter
    var offset:Int = -1
    private var list: MutableList<VideoTable> = ArrayList<VideoTable>()
    override fun getTitle(): String {
        return "new video"
    }

    override fun fragmentName(): String {
        return "NewFragment"
    }

    override fun initView() {
        mListView = findViewById<RecyclerView>(R.id.list)!!
        swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)!!
        mListView.adapter = adapter

        adapter.notifyDataSetChanged()
        val mLayoutManager = LinearLayoutManager(activity!!)
        mListView.setLayoutManager(mLayoutManager)
        mListView.setItemAnimator(DefaultItemAnimator())
        swipeRefreshLayout.setColorSchemeResources(
            *intArrayOf(
                R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary
            )
        )
        swipeRefreshLayout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                list.clear()
                presenter.nextPageToken = ""
                offset =0
                presenter.loadNew(0,false)
            }

        })
    }

    override fun toolBarViewMode(): ToolBarView {
        return ToolBarView.AUTO_HIDE
    }

    override fun showToolBarViewType(): ToolBarViewType {
        return ToolBarViewType.NORMAL
    }
    override fun loadData() {
        if (!firstLoad){
            presenter.loadNew()
            adapter.loadMore(false, this)
        }

    }

    override fun firstInit() {
        presenter = NewVideoPresenter(this,activity)
        adapter = NewListAdapter(activity!!,list)
        adapter.listener = this
    }

    override fun layoutFileResourceCommon(): Int {
        return R.layout.fragment_new_list
    }
    override fun onSearch(result: MutableList<VideoTable>,offset:Int) {
        firstLoad = true
        swipeRefreshLayout.isRefreshing = false
        if (result!= null){
            this.offset = offset
            list.addAll(result)

            if (offset!=-1){
                adapter.loadMore(true,this)
            } else {
                adapter.loadMore(false,this)
            }
            adapter.notifyDataSetChanged()
        }
    }


}

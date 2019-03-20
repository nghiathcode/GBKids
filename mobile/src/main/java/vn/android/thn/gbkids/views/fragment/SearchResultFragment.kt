package vn.android.thn.gbkids.views.fragment

import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import vn.android.thn.gbfilm.views.listener.ListItemListener
import vn.android.thn.gbfilm.views.listener.LoadMoreListener
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbkids.presenter.NewVideoPresenter
import vn.android.thn.gbkids.presenter.SearchVideoPresenter
import vn.android.thn.gbkids.views.activity.MainActivity
import vn.android.thn.gbkids.views.adapter.NewListAdapter
import vn.android.thn.gbkids.views.adapter.SearchListAdapter
import vn.android.thn.gbkids.views.view.ToolBarView
import vn.android.thn.gbkids.views.view.ToolBarViewType
import java.util.ArrayList


//
// Created by NghiaTH on 3/16/19.
// Copyright (c) 2019

class SearchResultFragment:BaseFragment(),SearchVideoPresenter.SearchMvp , ListItemListener , LoadMoreListener {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mListView: RecyclerView
    private lateinit var adapter: SearchListAdapter
    lateinit var presenter: SearchVideoPresenter
    var offset:Int = -1
    private var list: MutableList<VideoTable> = ArrayList<VideoTable>()
    var keyword = ""
    override fun fragmentName(): String {
        return "SearchResultFragment"
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

                offset =0
                presenter.searchKeyword(keyword,0,false)
            }

        })
        (activity as MainActivity).loadKeyWord(keyword)
    }

    override fun loadData() {
        if (!firstLoad){
            presenter.searchKeyword(keyword)
        }

    }
    override fun firstInit() {
        presenter = SearchVideoPresenter(this,activity)
        adapter = SearchListAdapter(activity!!,list)
        adapter.listener = this
    }

    override fun showToolBarViewType(): ToolBarViewType {
        return ToolBarViewType.SEARCH_KEYWORD
    }

    override fun toolBarViewMode(): ToolBarView {
        return ToolBarView.NORMAL
    }
    override fun layoutFileResourceCommon(): Int {
        return R.layout.fragment_search_result
    }

    override fun onSearch(result: MutableList<VideoTable>, offset: Int) {
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

    override fun onItemClick(obj: Any, pos: Int) {
        val detail = VideoDetailFragment()
        detail.videoId = (obj as VideoTable).videoID!!
        viewManager.pushView(detail)
    }

    override fun onLoadMore() {
        presenter.searchKeyword(keyword,offset,false)
    }
}

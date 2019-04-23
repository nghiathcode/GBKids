package vn.android.thn.gbkids.views.fragment

import android.support.v4.app.FragmentTransaction
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import vn.android.thn.gbfilm.views.listener.ListItemListener
import vn.android.thn.gbfilm.views.listener.LoadMoreListener
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbkids.presenter.NewVideoPresenter
import vn.android.thn.gbkids.views.activity.MainActivity
import vn.android.thn.gbkids.views.adapter.NewListAdapter
import vn.android.thn.gbkids.views.view.ToolBarView
import vn.android.thn.gbkids.views.view.ToolBarViewType

import java.util.ArrayList
import vn.android.thn.gbkids.R.string.AD_UNIT_ID
import com.google.android.gms.ads.AdView
import vn.android.thn.gbkids.utils.LogUtils


//
// Created by NghiaTH on 2/27/19.
// Copyright (c) 2019

class NewFragment:BaseFragment(), NewVideoPresenter.SearchMvp, ListItemListener, LoadMoreListener {
    override fun onLoadMore() {
        presenter.loadNew(offset,false)
    }
    override fun onItemClick(obj: Any, pos: Int) {
        (activity as MainActivity).showPlayer((obj as VideoTable),true)
    }

    val ITEMS_PER_AD = 8
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
        mListView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(activity!!)
        mListView.setLayoutManager(mLayoutManager)
        mListView.setItemAnimator(DefaultItemAnimator())
        swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)!!
        mListView.adapter = adapter

        adapter.notifyDataSetChanged()

        swipeRefreshLayout.setColorSchemeResources(
            *intArrayOf(
                R.color.loading,
                R.color.loading,
                R.color.loading
            )
        )
        swipeRefreshLayout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                list.clear()

                adapter.loadMore(list,false, this@NewFragment)
                adapter.notifyDataSetChanged()
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
            adapter.loadMore(list,false, this)
            presenter.loadNew()
        }

    }

    override fun firstInit() {
        presenter = NewVideoPresenter(this,activity)
        adapter = NewListAdapter(activity!!,list)
        adapter.loadMore(list,false, this)
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

            if (offset!=-1){
                adapter.loadMore(result,true,this)
            } else {
                adapter.loadMore(result,false,this)
            }

            adapter.notifyDataSetChanged()
        }
    }
}

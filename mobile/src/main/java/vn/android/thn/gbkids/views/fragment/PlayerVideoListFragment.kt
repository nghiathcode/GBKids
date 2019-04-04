package vn.android.thn.gbkids.views.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vn.android.thn.gbfilm.views.listener.ListItemListener
import vn.android.thn.gbfilm.views.listener.LoadMoreListener
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbkids.presenter.NextVideoPresenter
import vn.android.thn.gbkids.views.activity.MainActivity
import vn.android.thn.gbkids.views.adapter.SearchListAdapter
import java.util.ArrayList


//
// Created by NghiaTH on 3/19/19.
// Copyright (c) 2019

class PlayerVideoListFragment : Fragment() ,NextVideoPresenter.NextVideoMvp, ListItemListener, LoadMoreListener {
    override fun onItemClick(obj: Any, pos: Int) {
        (activity as MainActivity).showPlayer((obj as VideoTable),true)
    }

    override fun onLoadMore() {
        presenter.loadData(keyword,offset,false)
    }


    lateinit var presenter:NextVideoPresenter
    private lateinit var mListView: RecyclerView
    private lateinit var adapter: SearchListAdapter
    private lateinit var data_loading:View
    var offset:Int = -1
    var keyword = ""
    private var isNextVideo = true
    private var list: MutableList<VideoTable> = ArrayList<VideoTable>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = NextVideoPresenter(this,activity)
        adapter = SearchListAdapter(activity!!,list)
        adapter.listener = this
        adapter.loadMore(false,this)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =inflater.inflate(R.layout.fragment_video_list_player,container,false)
        mListView = view.findViewById<RecyclerView>(R.id.list)!!
        data_loading = view.findViewById(R.id.data_loading)!!
        data_loading.visibility = View.INVISIBLE
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mListView.adapter = adapter
        adapter.notifyDataSetChanged()
        val mLayoutManager = LinearLayoutManager(activity!!)
        mListView.setLayoutManager(mLayoutManager)
        mListView.setItemAnimator(DefaultItemAnimator())
    }
    fun loadNext(video:VideoTable){
        keyword= video.videoID
        adapter.loadMore(false,this)
        isNextVideo = true
        presenter.loadData(video.videoID)
    }
    override fun onNextVideo(listVideo: MutableList<VideoTable>, offset: Int) {
        activity!!.runOnUiThread {
            if (isNextVideo){
                list.clear()
                isNextVideo = false
                adapter.notifyDataSetChanged()
            }
            if (listVideo.size>0){
                this.offset = offset
                list.addAll(listVideo)
                if (offset!=-1){
                    adapter.loadMore(true,this)
                } else {
                    adapter.loadMore(false,this)
                }
                adapter.notifyDataSetChanged()
            }
        }

    }

    override fun apiError() {

    }

    override fun onNetworkFail() {

    }

    override fun onStartLoad() {
        data_loading.visibility = View.VISIBLE
    }

    override fun onComplete() {
        data_loading.visibility = View.GONE
    }
}

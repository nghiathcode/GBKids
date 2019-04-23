package vn.android.thn.gbkids.views.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.co.tss21.monistor.models.GBDataBase
import vn.android.thn.gbfilm.views.listener.ListItemListener
import vn.android.thn.gbfilm.views.listener.LoadMoreListener
import vn.android.thn.gbfilm.views.listener.PlayListItemListener
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.model.db.VideoDownLoad
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbkids.model.entity.ChannelLogoEntity
import vn.android.thn.gbkids.presenter.NextVideoPresenter
import vn.android.thn.gbkids.views.activity.MainActivity
import vn.android.thn.gbkids.views.adapter.PlayListAdapter
import vn.android.thn.gbkids.views.adapter.SearchListAdapter
import java.util.ArrayList


//
// Created by NghiaTH on 3/19/19.
// Copyright (c) 2019

class PlayerVideoListFragment : Fragment() ,NextVideoPresenter.NextVideoMvp, PlayListItemListener, LoadMoreListener ,
    PlayerFragment.PlayerListener {
    override fun nextVideo():VideoTable? {
        indexPlay = indexPlay+1
        if (list.size>indexPlay){
           var data = list.get(indexPlay)
            if (data is VideoTable){
                adapter.loadHeader(data)
                presenter.channelLogo(data.channelID)
                return data
            } else {
                return null
            }

        }
        return null
    }
    override fun onItemClick(obj: Any, pos: Int) {
        if (obj is VideoTable){
            (activity as MainActivity).showPlayer((obj as VideoTable),true)
//            presenter.channelLogo((obj as VideoTable).channelID)
            var videoPlay = (obj as VideoTable)
            videoPlay.save()
        } else{
            (activity as MainActivity).showPlayerDownLoad((obj as VideoDownLoad),true)
        }

    }

    override fun onDownload(videoTable: VideoTable) {
        (activity as MainActivity).checkDownload(videoTable)
    }
    override fun onLoadMore() {
        presenter.loadData(keyword,offset,false)
    }


    lateinit var presenter:NextVideoPresenter
    private lateinit var mListView: RecyclerView
    private lateinit var adapter: PlayListAdapter
    private lateinit var data_loading:View

    var keyword = ""
    var offset:Int = -1
    private var isNextVideo = true
    var indexPlay = -1
    private var list: MutableList<Any> = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = NextVideoPresenter(this,activity)
        adapter = PlayListAdapter(activity!!,list.toMutableList())
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
        adapter.localList = false
        adapter.loadHeader(video)

        indexPlay = 0
        keyword= video.videoID
        adapter.loadMore(false,this)
        isNextVideo = true
        presenter.loadData(video.videoID)
    }
    fun loadVideoDownLoad(video:VideoDownLoad){
        adapter.loadMore(false,this)
        adapter.localList = true
        list.clear()
        val localList = GBDataBase.getList(VideoDownLoad::class.java,"isComplete=1")
        list.addAll(localList)
        adapter.list =list
        adapter.notifyDataSetChanged()

    }
    override fun onNextVideo(listVideo: MutableList<VideoTable>, offset: Int) {
        activity!!.runOnUiThread {
            if (isNextVideo){
                list.clear()
                isNextVideo = false
                adapter.list =list
//                adapter.notifyDataSetChanged()
                mListView.scrollToPosition(0)
            }
            if (listVideo.size>0){
                this.offset = offset
                list.addAll(listVideo)
                if (offset!=-1){
                    adapter.loadMore(true,this)
                } else {
                    adapter.loadMore(false,this)
                }
//                adapter.list =list
                adapter.notifyDataSetChanged()

            }
            presenter.channelLogo(adapter.headerData!!.channelID)
        }

    }

    override fun onChannelLogo(logo: ChannelLogoEntity?) {
        adapter.loadChannelLogo(logo)
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

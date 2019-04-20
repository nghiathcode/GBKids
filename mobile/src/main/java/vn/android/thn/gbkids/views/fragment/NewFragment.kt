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
//        val detail = VideoDetailFragment()
//        detail.videoId = (obj as VideoTable).videoID!!
//        viewManager.pushView(detail)

//        viewManager.startActivity(VideoPlayerActivity::class.java)
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
        //load ad
        if (!firstLoad) {
//            addBannerAds()
//            loadBannerAds()
        }
        //end
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
//                addBannerAds()
//                loadBannerAds()
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
//            list.addAll(result)

            if (offset!=-1){
                adapter.loadMore(result,true,this)
            } else {
                adapter.loadMore(result,false,this)
            }

            adapter.notifyDataSetChanged()
        }
    }
    fun addBannerAds(){
//        var i = 0
//        while (i <= list.size) {
//            val adView = AdView(activity)
//            adView.adSize = AdSize.BANNER
//            adView.setAdUnitId(getString(R.string.AD_UNIT_ID))
//            list.add(i, adView)
//            i += ITEMS_PER_AD
//
//        }
    }
//    fun addBannerAdsList (listNew: MutableList<Any> ):MutableList<Any>{
//        var i = 0
//        while (i <= listNew.size) {
//            val adView = AdView(activity)
//            adView.adSize = AdSize.BANNER
//            adView.setAdUnitId(getString(R.string.AD_UNIT_ID))
//            listNew.add(i, adView)
//            i += ITEMS_PER_AD
//
//        }
//        return listNew
//    }
    fun loadBannerAds(){
//        loadBannerAd(0)
    }
    fun loadBannerAd(index:Int){
//        if (index >= list.size){
//            return
//        }
//        var item = list.get(index)
//        if (!(item is AdView)){
//            return
//        }
//        val adView = item as AdView
//        adView.adListener = object : AdListener() {
//            override fun onAdLoaded() {
//                super.onAdLoaded()
//                loadBannerAd(0);
//            }
//
//            override fun onAdFailedToLoad(p0: Int) {
//                super.onAdFailedToLoad(p0)
//                LogUtils.info("onAdFailedToLoad:",p0.toString())
//                loadBannerAd(0);
//            }
//        }
//        if (app.isDebugMode()) {
//            adView.loadAd(AdRequest.Builder().addTestDevice("BCB68136B98CF003B0B4965411508000").build())
//        } else {
//            adView.loadAd(AdRequest.Builder().build())
//        }
    }

    override fun onResume() {
//        for (item in list) {
//            if (item is AdView) {
//                val adView = item as AdView
//                adView.resume()
//            }
//        }
        super.onResume()
    }

    override fun onPause() {
//        for (item in list) {
//            if (item is AdView) {
//                val adView = item as AdView
//                adView.pause()
//            }
//        }
        super.onPause()

    }

    override fun onDestroy() {
//        for (item in list) {
//            if (item is AdView) {
//                val adView = item as AdView
//                adView.destroy()
//            }
//        }
        super.onDestroy()

    }
}

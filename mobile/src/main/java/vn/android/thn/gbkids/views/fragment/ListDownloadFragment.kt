package vn.android.thn.gbkids.views.fragment

import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import jp.co.tss21.monistor.models.GBDataBase
import vn.android.thn.gbfilm.views.listener.ListItemListener
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.model.api.GBRequestName
import vn.android.thn.gbkids.model.api.GBTubeRequestCallBack
import vn.android.thn.gbkids.model.api.request.GBTubeRequest
import vn.android.thn.gbkids.model.api.response.GBTubeResponse
import vn.android.thn.gbkids.model.api.response.LogoChannelResponse
import vn.android.thn.gbkids.model.db.VideoDownLoad
import vn.android.thn.gbkids.views.adapter.DownloadListAdapter
import vn.android.thn.gbkids.views.view.ToolBarView
import vn.android.thn.library.net.GBRequestError
import java.util.ArrayList

class ListDownloadFragment :BaseFragment(), ListItemListener {
    override fun onItemClick(obj: Any, pos: Int) {
//        (activity as MainActivity).showPlayerDownLoad((obj as VideoDownLoad),true)
        player_view_content.visibility = View.VISIBLE
        player.playVideoLocal((obj as VideoDownLoad))
        adapter.headerData = (obj as VideoDownLoad)
        mListView.scrollToPosition(0)
        val api = GBTubeRequest(String.format(GBRequestName.LOGO_CHANNEL,(obj as VideoDownLoad).channelID),activity!!)
        api.get().execute(object : GBTubeRequestCallBack {
            override fun onResponse(httpCode: Int, response: GBTubeResponse, request: GBTubeRequest) {
                adapter.channelLogoEntity = response.toResponse(LogoChannelResponse::class)!!.logo
                adapter.notifyDataSetChanged()
            }

            override fun onRequestError(errorRequest: GBRequestError, request: GBTubeRequest) {
                adapter.notifyDataSetChanged()
            }

        })
    }
    var player =SinglePlayerFragment()
    private lateinit var mListView: RecyclerView
    private lateinit var player_view_content: FrameLayout
    private lateinit var adapter: DownloadListAdapter
    private lateinit var no_data:TextView
    private var list: MutableList<VideoDownLoad> = ArrayList<VideoDownLoad>()
    override fun fragmentName(): String {
        return "ListDownloadFragment"
    }

    override fun initView() {
        no_data = findViewById<TextView>(R.id.no_data)!!
        player_view_content = findViewById<FrameLayout>(R.id.player_view_content)!!
        mListView = findViewById<RecyclerView>(R.id.list)!!
        mListView.adapter = adapter
        adapter.notifyDataSetChanged()
        val mLayoutManager = LinearLayoutManager(activity!!)
        mListView.setLayoutManager(mLayoutManager)
        mListView.setItemAnimator(DefaultItemAnimator())

    }

    override fun loadData() {
        viewManager.addView(player,null,null,R.id.player_view_content)
    }

    override fun firstInit() {
        list  = GBDataBase.getList(VideoDownLoad::class.java,"isComplete=1")
        if(list.size == 0){
            no_data.visibility = View.VISIBLE
            no_data.text = getString(R.string.txt_no_download)
            mListView.visibility = View.GONE
        } else {
            mListView.visibility = View.VISIBLE
            no_data.visibility = View.GONE
        }
        adapter = DownloadListAdapter(activity!!,list)
        adapter.listener = this
    }

    override fun layoutFileResourceCommon(): Int {
        return R.layout.fragment_download_list
    }
    override fun toolBarViewMode(): ToolBarView {
        return ToolBarView.AUTO_HIDE
    }
    override fun onDestroy() {
        player.closeVideo()
        adapter.destroyAD()
        super.onDestroy()
    }
    override fun onResume() {
        adapter.resumeAD()
        super.onResume()
    }

    override fun onPause() {
        adapter.pauseAD()
        super.onPause()
    }


}
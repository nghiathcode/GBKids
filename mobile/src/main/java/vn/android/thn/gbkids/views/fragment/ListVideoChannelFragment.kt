package vn.android.thn.gbkids.views.fragment

import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import jp.co.tss21.monistor.models.GBDataBase
import vn.android.thn.gbfilm.views.listener.ListItemListener
import vn.android.thn.gbfilm.views.listener.LoadMoreListener
import vn.android.thn.gbfilm.views.listener.PlayListItemListener
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.model.db.VideoDownLoad
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbkids.presenter.VideoChannelPresenter
import vn.android.thn.gbkids.utils.LogUtils
import vn.android.thn.gbkids.views.activity.MainActivity
import vn.android.thn.gbkids.views.adapter.DownloadListAdapter
import vn.android.thn.gbkids.views.adapter.HistoryListAdapter
import vn.android.thn.gbkids.views.adapter.PlayListAdapter
import vn.android.thn.gbkids.views.adapter.VideoChannelListAdapter
import vn.android.thn.gbkids.views.view.ToolBarView
import java.util.ArrayList

class ListVideoChannelFragment :BaseFragment(), PlayListItemListener, VideoChannelPresenter.VideoChannelMvp,LoadMoreListener{
    override fun onDownload(videoTable: VideoTable) {
        (activity as MainActivity).checkDownload(videoTable)
    }

    val TAG = "SinglePlayerFragment"

    override fun onLoadMore() {
        presenter.loadData(channelID,offset,false)
    }

    override fun onListVideo(listVideo: MutableList<VideoTable>, offset: Int) {
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
        list.addAll(listVideo)
        adapter.notifyDataSetChanged()
    }

    override fun onStartLoad() {
        presenter.showLoading()
    }

    override fun onComplete() {
        presenter.hideLoading()
    }

    override fun onItemClick(obj: Any, pos: Int) {
        player_view_content.visibility = View.VISIBLE
        val  videoPlay = (obj as VideoTable)
        player.playNewVideo(videoPlay)
        mListView.scrollToPosition(0)
        adapter.headerData = (videoPlay)

        if (videoPlay!= null){
            if (videoPlay is VideoTable){
                (videoPlay as VideoTable).save()
            }

        }
    }
    var offset:Int = -1
    var player =SinglePlayerFragment()
    private var isNextVideo = true
    private lateinit var presenter:VideoChannelPresenter
    private lateinit var mListView: RecyclerView
    private lateinit var adapter: VideoChannelListAdapter
    private var list: MutableList<VideoTable> = ArrayList<VideoTable>()
    var channelID = ""
    private lateinit var player_view_content:FrameLayout
    override fun fragmentName(): String {
        return "ListDownloadFragment"
    }

    override fun initView() {
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
        presenter.loadData(channelID,0,true)
    }

    override fun firstInit() {
        presenter = VideoChannelPresenter(this,activity!!)
        channelID = arguments!!.getString("channelId")
        adapter = VideoChannelListAdapter(activity!!,list)
        adapter.listener = this
    }

    override fun layoutFileResourceCommon(): Int {
        return R.layout.fragment_video_channel_list
    }
    override fun toolBarViewMode(): ToolBarView {
        return ToolBarView.AUTO_HIDE
    }

}
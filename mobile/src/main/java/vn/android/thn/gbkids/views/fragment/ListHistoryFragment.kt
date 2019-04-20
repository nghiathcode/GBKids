package vn.android.thn.gbkids.views.fragment

import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import jp.co.tss21.monistor.models.GBDataBase
import vn.android.thn.gbfilm.views.listener.ListItemListener
import vn.android.thn.gbkids.R
import vn.android.thn.gbkids.model.db.VideoDownLoad
import vn.android.thn.gbkids.model.db.VideoTable
import vn.android.thn.gbkids.views.activity.MainActivity
import vn.android.thn.gbkids.views.adapter.DownloadListAdapter
import vn.android.thn.gbkids.views.adapter.HistoryListAdapter
import vn.android.thn.gbkids.views.adapter.PlayListAdapter
import java.util.ArrayList

class ListHistoryFragment :BaseFragment(), ListItemListener {
    override fun onItemClick(obj: Any, pos: Int) {
        (activity as MainActivity).showPlayer((obj as VideoTable),true)
    }

    private lateinit var mListView: RecyclerView
    private lateinit var adapter: HistoryListAdapter
    private var list: MutableList<VideoTable> = ArrayList<VideoTable>()
    override fun fragmentName(): String {
        return "ListDownloadFragment"
    }

    override fun initView() {
        mListView = findViewById<RecyclerView>(R.id.list)!!
        mListView.adapter = adapter
        adapter.notifyDataSetChanged()
        val mLayoutManager = LinearLayoutManager(activity!!)
        mListView.setLayoutManager(mLayoutManager)
        mListView.setItemAnimator(DefaultItemAnimator())
    }

    override fun loadData() {

    }

    override fun firstInit() {
        list  = GBDataBase.getList(VideoTable::class.java,"isDelete=0")
        adapter = HistoryListAdapter(activity!!,list.toMutableList())
        adapter.listener = this
    }

    override fun layoutFileResourceCommon(): Int {
        return R.layout.fragment_download_list
    }
}
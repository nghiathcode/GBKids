package vn.android.thn.gbkids.presenter

import android.os.AsyncTask
import androidx.fragment.app.FragmentActivity
import io.realm.Realm
import io.realm.Sort

import vn.android.thn.commons.listener.LoadMoreListener
import vn.android.thn.commons.realm.RealmVideo
import vn.android.thn.gbkids.model.ChannelEntity
import vn.android.thn.gbkids.views.adapter.ChannelAdapter
import vn.android.thn.gbkids.views.fragment.ChannelFragment

class ChannelPresenter(view: ChannelFragment, mActivity: FragmentActivity?) :
    PresenterBase<ChannelFragment>(view, mActivity) , LoadMoreListener {
    var adapter: ChannelAdapter
    var list = ArrayList<ChannelEntity>()
    init {
        adapter = ChannelAdapter(mActivity!!,list)
        adapter.loadMoreListener = this
        adapter.listener = view
    }

    override fun initView() {
        if (view!= null) {
            view!!.onListVideo(adapter)
        }
    }

    override fun onLoadMore() {
        loadNew(list.size)
    }
    fun loadNew(offset:Int = 0){
        LoadNextListTask().execute(offset)
    }
    inner class LoadNextListTask : AsyncTask<Int, String, Int>() {
        override fun doInBackground(vararg params: Int?): Int {
            if (view!= null) {
                var offset = params[0]!!
                if (offset ==0){
                    list.clear()
                    adapter.isLoadMore = false
                }
                realm = Realm.getDefaultInstance()
                var listNew:List<RealmVideo> = realm.where(RealmVideo::class.java)
                    .equalTo("isDelete",0.toInt())
                    .distinct("channelId")
                    .sort("viewCount",Sort.DESCENDING)
                    .findAll()
                if (listNew.size>(MAX_ROW+offset)){
                    listNew = listNew.subList(offset,MAX_ROW+offset)
                    for (video in listNew){
                        var channel = ChannelEntity()
                        channel.channelId = video.channelId
                        channel.channelTitle = video.channelTitle
                        channel.channelImage  = video.channelImage
                        list.add(channel)
                    }
                    adapter.isLoadMore = listNew.size >= MAX_ROW
                } else {
                    for (video in listNew){
                        var channel = ChannelEntity()
                        channel.channelId = video.channelId
                        channel.channelTitle = video.channelTitle
                        channel.channelImage  = video.channelImage
                        list.add(channel)
                    }
                    adapter.isLoadMore = false
                }
                //Select().from(VideoTable::class.java).where("isDelete=0").groupBy("channelId").orderBy("viewCount DESC").limit(MAX_ROW).offset(offset).execute()
                realm.close()
                return offset
            }
            return 0
        }
        override fun onPreExecute() {
            if (view!= null) {
                if (list.size ==0)
                showLoading()
            }
        }
        override fun onPostExecute(result: Int) {
            if (view!= null) {
                view!!.onListVideo(adapter)
                if (result ==0)
                hideLoading()
            }
        }
    }
}
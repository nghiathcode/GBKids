package vn.android.thn.gbkids.presenter

import android.os.AsyncTask
import androidx.fragment.app.FragmentActivity
import io.realm.Realm
import io.realm.Sort
import vn.android.thn.commons.listener.LoadMoreListener
import vn.android.thn.commons.realm.RealmVideo
import vn.android.thn.gbkids.views.adapter.ListVideoAdapter
import vn.android.thn.gbkids.views.fragment.ListVideoFragment

class ListVideoPresenter(view: ListVideoFragment, mActivity: FragmentActivity?,listType:Int) :
    PresenterBase<ListVideoFragment>(view, mActivity) , LoadMoreListener {
    var adapter: ListVideoAdapter
    var list = ArrayList<RealmVideo>()
    var videoCurrent:RealmVideo? = null
    var listType = 0
    var channelId = ""
    init {
        this.listType = listType
        adapter = ListVideoAdapter(mActivity!!,list)
        adapter.loadMoreListener = this
        adapter.listener = view
    }

    override fun onLoadMore() {
        if (listType ==1){
            loadListVideoChannel(list.size,this.channelId)
        }else {
            loadListVideo(list.size)
        }
    }
    override fun initView() {
        if (view!= null) {
            view!!.onListVideo(adapter)
        }
    }
    fun loadListVideo(offset:Int = 0){
        if (offset==0){
            list.clear()
            adapter.isLoadMore = false
        }
        LoadNextListTask().execute(offset)
    }
    fun loadListVideoChannel(offset:Int = 0,channelId:String){
        this.channelId = channelId
        LoadNextListTask().execute(offset)
    }
    inner class LoadNextListTask : AsyncTask<Int, String, Int>() {
        override fun doInBackground(vararg params: Int?): Int {
            var offset = params[0]!!
            if (offset==0){
                list.clear()
            }
            realm = Realm.getDefaultInstance()
            var listNew:List<RealmVideo> = realm.where(RealmVideo::class.java)
                .equalTo("isDelete",0.toInt()).and().equalTo("ignore",0.toInt())
                .and()
                .equalTo("channelId",channelId)
                .sort("viewCount", Sort.DESCENDING,"publishedAt", Sort.DESCENDING)
                .findAll()
//            val listNew:List<VideoTable> = Select().from(VideoTable::class.java).where("isDelete=0 and channelId=?",channelId) .orderBy("viewCount,publishedAt DESC").limit(MAX_ROW).offset(offset).execute()
            if (listNew.size>(MAX_ROW+offset)){
                listNew = listNew.subList(offset,MAX_ROW+offset)
                list.addAll(realm.copyFromRealm(listNew))
                adapter.isLoadMore = listNew.size >= MAX_ROW
            } else {
                list.addAll(realm.copyFromRealm(listNew))
                adapter.isLoadMore = false
            }
            realm.close()
            return offset
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
package vn.android.thn.gbkids.presenter

import android.os.AsyncTask
import androidx.fragment.app.FragmentActivity
import io.realm.Realm
import io.realm.Sort
import vn.android.thn.commons.listener.LoadMoreListener
import vn.android.thn.commons.realm.RealmVideo
import vn.android.thn.gbkids.views.adapter.HomeAdapter
import vn.android.thn.gbkids.views.fragment.WatchedFragment

class WatchedPresenter(view: WatchedFragment, mActivity: FragmentActivity?) :
    PresenterBase<WatchedFragment>(view, mActivity) , LoadMoreListener {
    var adapter: HomeAdapter
    var list = ArrayList<RealmVideo>()
    init {
        adapter = HomeAdapter(mActivity!!,list)
        adapter.isLoadMore = false
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
        if (offset==0){
            list.clear()
            adapter.isLoadMore = false
        }
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
                .equalTo("isDelete",0.toInt())
                .and()
                .equalTo("isWatched",1.toInt())
                .and().equalTo("ignore",0.toInt())
                .sort("watchedTime", Sort.DESCENDING)
                .findAll()
//            val listNew:List<VideoTable> = Select().from(VideoTable::class.java).where("isDelete=0 and isWatched=1").orderBy("watchedTime DESC").limit(MAX_ROW).offset(offset).execute()
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
package vn.android.thn.gbkids.presenter

import android.os.AsyncTask
import androidx.fragment.app.FragmentActivity
import io.realm.Realm
import io.realm.Sort
import vn.android.thn.commons.listener.LoadMoreListener
import vn.android.thn.commons.realm.RealmVideo
import vn.android.thn.gbkids.views.adapter.HomeAdapter
import vn.android.thn.gbkids.views.fragment.HomeFragment
import kotlin.random.Random

class HomePresenter(view:HomeFragment, mActivity: FragmentActivity?) :
    PresenterBase<HomeFragment>(view, mActivity), LoadMoreListener {
    override fun onLoadMore() {
        loadNew(list.size)
    }
    var adapter: HomeAdapter
    var list = ArrayList<RealmVideo>()
    var listOrderBy = ArrayList<String>()
    var flagQuery = -1
    init {
        adapter = HomeAdapter(mActivity!!,list)
        adapter.loadMoreListener = this
        adapter.listener = view
        flagQuery = Random.nextInt(0,6)
    }
    fun onRefresh(){
        app.download()
        flagQuery = Random.nextInt(0,6)
        adapter.isLoadMore = false
        loadNew()
    }
    override fun initView() {
        if (view!= null) {
            view!!.onListVideo(adapter)
        }
    }
    fun loadNew(offset:Int = 0){
        LoadNextListTask().execute(offset)
    }
    inner class LoadNextListTask : AsyncTask<Int, String, Int>() {
        override fun doInBackground(vararg params: Int?): Int {
            if (view!= null) {
                var offset = params[0]!!
                if (offset==0){
                    list.clear()
                    adapter.isLoadMore = false
                }
                realm = Realm.getDefaultInstance()
                val realmQuery = realm.where(RealmVideo::class.java)
                realmQuery.equalTo("isDelete",0.toInt())
                realmQuery.and().equalTo("ignore",0.toInt())
                var listNew:List<RealmVideo>
                when(flagQuery){
                    0->{
                        realmQuery.sort("publishedAt", Sort.DESCENDING)
                    }
                    1->{
                        realmQuery.sort("dateUpdate", Sort.DESCENDING)
                    }
                    2->{
                        realmQuery.sort("viewCount", Sort.DESCENDING)
                    }
                    3->{
                        realmQuery.sort("publishedAt", Sort.ASCENDING)
                    }
                    4->{
                        realmQuery.sort("dateUpdate", Sort.ASCENDING)
                    }
                    5->{
                        realmQuery.sort("viewCount", Sort.ASCENDING)
                    }
                }
                listNew =realmQuery.findAll()

                if (listNew.size>(MAX_ROW+offset)){
                    listNew = listNew.subList(offset,MAX_ROW+offset)
                    list.addAll(realm.copyFromRealm(listNew))
                    adapter.isLoadMore = listNew.size >= MAX_ROW
                } else {
                    list.addAll(realm.copyFromRealm(listNew))
                    adapter.isLoadMore = false
                }
//                val listNew:List<VideoTable> = Select().from(VideoTable::class.java).where("isDelete=0").orderBy(orderBy).limit(MAX_ROW).offset(offset).execute()
                realm.close()
                return offset!!
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
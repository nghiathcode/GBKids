package vn.android.thn.gbkids.presenter

import android.os.AsyncTask
import androidx.fragment.app.FragmentActivity
import io.realm.Realm
import io.realm.Sort
import vn.android.thn.commons.listener.LoadMoreListener
import vn.android.thn.commons.realm.RealmVideo
import vn.android.thn.gbkids.views.adapter.ListVideoAdapter
import vn.android.thn.gbkids.views.fragment.SearchResultFragment
import vn.android.thn.library.utils.GBUtils

class SearchResultPresenter(view: SearchResultFragment, mActivity: FragmentActivity?, listType:Int) :
    PresenterBase<SearchResultFragment>(view, mActivity) , LoadMoreListener {
    var adapter: ListVideoAdapter
    var list = ArrayList<RealmVideo>()
    var videoCurrent:RealmVideo? = null
    var listType = 0
    var keyword = ""
    init {
        this.listType = listType
        adapter = ListVideoAdapter(mActivity!!,list)
        adapter.loadMoreListener = this
        adapter.listener = view
    }

    override fun onLoadMore() {
        loadListVideoKeyWord(list.size,this.keyword)
    }
    override fun initView() {
        if (view!= null) {
            view!!.onListVideo(adapter)
        }
    }

    fun loadListVideoKeyWord(offset:Int = 0,keyword:String){
        this.keyword = keyword
        if (offset==0){
            list.clear()
            adapter.isLoadMore = false
        }
        LoadNextListTask().execute(offset)
    }
    inner class LoadNextListTask : AsyncTask<Int, String, Int>() {
        override fun doInBackground(vararg params: Int?): Int {
            var offset = params[0]
            if (offset==0){
                list.clear()
            }
            //select * from videos where  instr(tags,'baby shark')>0 or  instr(tags,'BIBI TV')>0
            realm = Realm.getDefaultInstance()

            val realmQuery = realm.where(RealmVideo::class.java)
            realmQuery.equalTo("isDelete",0.toInt())
            realmQuery.and().equalTo("ignore",0.toInt())
            var listNew:List<RealmVideo>
            if (!GBUtils.isEmpty(keyword)){
                realmQuery.beginGroup()
                realmQuery.contains("tags",keyword).or().contains("title",keyword).or().contains("description",keyword)
                    .or().contains("channelTitle",keyword)
                realmQuery.endGroup()

                realmQuery.sort("viewCount", Sort.DESCENDING,"publishedAt", Sort.DESCENDING)
                listNew = realmQuery.findAll()
            } else {
                listNew =  realm.where(RealmVideo::class.java)
                    .equalTo("isDelete",0.toInt()).and().equalTo("ignore",0.toInt())
                    .sort("viewCount", Sort.DESCENDING,"publishedAt", Sort.DESCENDING)
                    .findAll()
            }

            if (listNew.size>(MAX_ROW+offset!!)){
                listNew = listNew.subList(offset,MAX_ROW+offset)
                list.addAll(realm.copyFromRealm(listNew).shuffled())
                adapter.isLoadMore = listNew.size >= MAX_ROW
            } else {
                list.addAll(realm.copyFromRealm(listNew).shuffled())
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
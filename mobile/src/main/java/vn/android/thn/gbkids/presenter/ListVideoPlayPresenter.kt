package vn.android.thn.gbkids.presenter

import android.os.AsyncTask
import androidx.fragment.app.FragmentActivity
import io.realm.Case
import io.realm.Realm
import io.realm.Sort
import vn.android.thn.commons.listener.DownloadVideoListener
import vn.android.thn.commons.listener.LoadMoreListener
import vn.android.thn.commons.realm.RealmVideo
import vn.android.thn.gbkids.views.adapter.ListVideoPlayAdapter
import vn.android.thn.gbkids.views.fragment.ListVideoPlayFragment
import vn.android.thn.library.utils.GBUtils

class ListVideoPlayPresenter(view: ListVideoPlayFragment, mActivity: FragmentActivity?) :
    PresenterBase<ListVideoPlayFragment>(view, mActivity) , LoadMoreListener ,DownloadVideoListener{
    var adapter: ListVideoPlayAdapter
    var list = ArrayList<RealmVideo>()
    var videoCurrent:RealmVideo? = null
    private var mLoadNextListTask: LoadNextListTask? = null
    init {
        adapter = ListVideoPlayAdapter(mActivity!!,list)
        adapter.loadMoreListener = this
        adapter.listener = view
        adapter.downloadListener = this

    }

    override fun onLoadMore() {
        loadListVideo(list.size)
    }
    override fun initView() {

    }

    override fun onDownload(videoId: String) {
        if (view!= null) {
            view!!.download(videoId)
        }
    }

    override fun onIgnoreVideo(video: RealmVideo) {
        if (view!= null) {
            view!!.onIgnoreVideo(video)
        }

    }
    fun loadListVideo(offset:Int = 0){
        if (offset==0){
            list.clear()
            adapter.isLoadMore = false
        }
        adapter.headerData = videoCurrent
        if (list.size == 0){
            adapter.showDescription = false
        }
        mLoadNextListTask = LoadNextListTask()
        mLoadNextListTask!!.execute(offset)
    }
    fun loadWhere():List<String>{

        if (videoCurrent!= null){
            var sql = StringBuilder()
            var tags = videoCurrent!!.tags.replace("(","").replace(")","").split(",")
            return tags
        }
        return emptyList()
    }
    fun resumeAD(){
        adapter.resumeAD()
    }
    fun pauseAD(){
        adapter.pauseAD()
    }
    fun destroyAD(){
        adapter.destroyAD()
    }
    inner class LoadNextListTask : AsyncTask<Int, String, Boolean>() {
        override fun onPreExecute() {
            if (view!= null) {
                view!!.startLoading()
            }
        }
        override fun doInBackground(vararg params: Int?): Boolean {
            if (view!= null) {
                var offset = params[0]
                if (offset==0){
                    list.clear()
                }
                //select * from videos where  instr(tags,'baby shark')>0 or  instr(tags,'BIBI TV')>0
                var where = loadWhere()
                realm = Realm.getDefaultInstance()
                val realmQuery = realm.where(RealmVideo::class.java)
                realmQuery.equalTo("isDelete",0.toInt())
                realmQuery.and().equalTo("ignore",0.toInt())
                realmQuery.and()
                realmQuery.notEqualTo("videoID",videoCurrent!!.videoID)
                var listNew:List<RealmVideo>
                if (where.size>0){
                    realmQuery.beginGroup()
                    for (index in 0 until where.size){
                        if (index<(where.size-1)) {
                            realmQuery.contains("tags",where.get(index))
                            realmQuery.or()
                        }
                    }
                    realmQuery.contains("tags",where.get(where.size-1))
                    realmQuery.endGroup()
                    realmQuery.sort("viewCount", Sort.DESCENDING,"publishedAt", Sort.DESCENDING)
                    listNew = realmQuery.findAll()
                    if(listNew.size ==0){
                        listNew =  realm.where(RealmVideo::class.java)
                            .equalTo("isDelete",0.toInt()).and().notEqualTo("videoID",videoCurrent!!.videoID)
                            .sort("viewCount", Sort.DESCENDING,"publishedAt", Sort.DESCENDING)
                            .findAll()
                    }
                } else {
                    listNew =  realm.where(RealmVideo::class.java)
                        .equalTo("isDelete",0.toInt()).and().equalTo("ignore",0.toInt())
                        .sort("viewCount", Sort.DESCENDING,"publishedAt", Sort.DESCENDING)
                        .findAll()
                }


//                var listNew:List<VideoTable> = Select().from(VideoTable::class.java).where("isDelete=0").orderBy("viewCount,publishedAt DESC").limit(MAX_ROW).offset(offset!!).execute()
//                if (!GBUtils.isEmpty(where)){
//                    try {
//                        listNew = Select().from(VideoTable::class.java).where(" isDelete=0 and videoID<>? and ("+where+ " )",videoCurrent!!.videoID).orderBy("viewCount,publishedAt DESC").limit(MAX_ROW).offset(offset).execute()
//                    }catch (e:Exception){
//                        listNew = Select().from(VideoTable::class.java).where("isDelete=0").orderBy("viewCount,publishedAt DESC").limit(MAX_ROW).offset(offset).execute()
//                    }
//
//                }
                if (listNew.size>(MAX_ROW+offset!!)){
                    listNew = listNew.subList(offset,MAX_ROW+offset)
                    list.addAll(realm.copyFromRealm(listNew).shuffled())
                    adapter.isLoadMore = listNew.size >= MAX_ROW
                } else {
                    list.addAll(realm.copyFromRealm(listNew).shuffled())
                    adapter.isLoadMore = false
                }

            }
            realm.close()
            return true
        }
        override fun onPostExecute(result: Boolean) {
            if (view!= null) {
                view!!.onListVideo(adapter)
                view!!.endLoading()
            }
        }

    }
}
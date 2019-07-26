package vn.android.thn.commons.realm

import io.realm.Realm
import io.realm.RealmObject
import io.realm.Sort
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.json.JSONObject
import vn.android.thn.commons.App
import vn.android.thn.commons.GBRealm
import vn.android.thn.library.utils.GBLog
import vn.android.thn.library.utils.GBUtils

open class RealmVideo : RealmObject() {
    @PrimaryKey
    var videoID: String = ""
    var title: String=""
    var description: String=""
    var channelId: String=""
    var channelTitle: String = ""
    var playListId: String = ""
    var playListName: String = ""
    var playListImage: String = ""
    var imageLarger:String = ""
    var imageSmall:String = ""
    var publishedAt: String = ""
    var tags:String = ""
    var isDelete: Int = 0
    var dateUpdate: String = ""
    var appID: String = ""
    var videoType:Int = 0//0:youtube,1:driver,2:facebook,3:link
    var linkPlay:String = ""
    var liveStream = 0
    var viewCount = "0"
    var categoryVideoId:Int = 0
    var categoryName = ""
    var typeVideoListId:String= ""
    var showWeb = 1
    var channelImage = ""
    var isWatched = 0
    var videoTime = 0L
    var videoTimeCurrent = 0L
    var expiredVideo=""
    var isDownLoaded=0
    var watchedTime = ""
    var isLike = 0
    var ignore = 0
    companion object{
        fun maxDate():String{
            val realm = Realm.getDefaultInstance()
            var result:RealmVideo? = realm.where(RealmVideo::class.java).sort("dateUpdate",Sort.DESCENDING).findFirst()
//            try {
//                realm.beginTransaction()
//                result=realm.where(RealmVideo::class.java).sort("dateUpdate",Sort.DESCENDING).findFirst()
//                realm.commitTransaction()
//            }catch (e:Exception){
//                GBLog.info("Realm", "Insert error:"+e.message, App.getInstance()!!.isDebugMode())
//                realm.close()
//                return ""
//            }finally {
//                realm.close()
//            }
            if (result == null) return ""
            return realm.copyFromRealm(result)!!.dateUpdate

            //
//            var result=""
//            var sql = StringBuilder()
//            sql.append("select max(dateUpdate) as dateUpdate from videos" )
//            val c = ActiveAndroid.getDatabase().rawQuery(sql.toString(),null)
//            if (c != null) {
//                if (c.moveToFirst()) {
//                    do {
//                        result = c.getString(c.getColumnIndex("dateUpdate"))
//                    } while (c.moveToNext())
//                }
//
//            }
//            c.close()
//            return result
        }
        fun minDate():String{

            var result:RealmVideo? = null
            val realm = Realm.getDefaultInstance()
            result=realm.where(RealmVideo::class.java).sort("dateUpdate",Sort.ASCENDING).findFirst()
//            try {
//                realm.beginTransaction()
//                result=realm.where(RealmVideo::class.java).sort("dateUpdate",Sort.ASCENDING).findFirst()
//                realm.commitTransaction()
//            }catch (e:Exception){
//                GBLog.info("Realm", "Insert error:"+e.message, App.getInstance()!!.isDebugMode())
//                realm.close()
//                return ""
//            }finally {
//                realm.close()
//            }
            return result!!.dateUpdate

//            var result=""
//            var sql = StringBuilder()
//            sql.append("select min(dateUpdate) as dateUpdate from videos" )
//            val c = ActiveAndroid.getDatabase().rawQuery(sql.toString(),null)
//            if (c != null) {
//                if (c.moveToFirst()) {
//                    do {
//                        result = c.getString(c.getColumnIndex("dateUpdate"))
//                    } while (c.moveToNext())
//                }
//
//            }
//            c.close()
//            return result
        }

        fun updateWatched(videoID:String){
            val realm = Realm.getDefaultInstance()
            try {
                var item  = realm.where(RealmVideo::class.java).equalTo("videoID",videoID).findFirst()
                if (item!=null) {
                    realm.beginTransaction()
                    item.isWatched = 1
                    item.watchedTime = GBUtils.dateNow()
                    realm.insertOrUpdate(realm.copyFromRealm(item))
                    realm.commitTransaction()
                }
            }catch (e:Exception){
                GBLog.info("Realm", "Insert error:"+e.message, App.getInstance()!!.isDebugMode())
                realm.close()

            }finally {
                realm.close()
            }

//            var item = GBDataBase.getObject(RealmVideo::class.java,"videoID=?",*arrayOf(videoID))
//            if (item!= null){
//                item.isWatched = 1
//                item.watchedTime = GBUtils.dateNow()
//                item.save()
//            }
        }
        fun updateDelete(videoID:String){
            val realm = Realm.getDefaultInstance()
            try {
                var item  = realm.where(RealmVideo::class.java).equalTo("videoID",videoID).findFirst()
                if (item!=null) {
                    realm.beginTransaction()
                    item.isDelete = 1
                    realm.insertOrUpdate(realm.copyFromRealm(item))
                    realm.commitTransaction()
                }
            }catch (e:Exception){
                GBLog.info("Realm", "Insert error:"+e.message, App.getInstance()!!.isDebugMode())
                realm.close()

            }finally {
                realm.close()
            }
//            var item = GBDataBase.getObject(RealmVideo::class.java,"videoID=?",*arrayOf(videoID))
//            if (item!= null){
//                item.isDelete = 1
//                item.save()
//            }

        }
        fun updateDownload(videoID:String){
            val realm = Realm.getDefaultInstance()
            try {
                var item  = realm.where(RealmVideo::class.java).equalTo("videoID",videoID).findFirst()
                if (item!=null) {
                    realm.beginTransaction()
                    item.isDownLoaded = 1
                    realm.insertOrUpdate(realm.copyFromRealm(item))
                    realm.commitTransaction()
                }
            }catch (e:Exception){
                GBLog.info("Realm", "Insert error:"+e.message, App.getInstance()!!.isDebugMode())
                realm.close()

            }finally {
                realm.close()
            }
//            var item = GBDataBase.getObject(RealmVideo::class.java,"videoID=?",*arrayOf(videoID))
//            if (item!= null){
//                item.isDownLoaded = 1
//                item.save()
//            }
        }
        fun updateDownloadComplete(videoID:String){
            val realm = Realm.getDefaultInstance()
            try {
                var item  = realm.where(RealmVideo::class.java).equalTo("videoID",videoID).findFirst()
                if (item!=null) {
                    realm.beginTransaction()
                    item.isDownLoaded = 2
                    realm.insertOrUpdate(realm.copyFromRealm(item))
                    realm.commitTransaction()
                }
            }catch (e:Exception){
                GBLog.info("Realm", "Insert error:"+e.message, App.getInstance()!!.isDebugMode())
                realm.close()

            }finally {
                realm.close()
            }
//            var item = GBDataBase.getObject(RealmVideo::class.java,"videoID=?",*arrayOf(videoID))
//            if (item!= null){
//                item.isDownLoaded = 1
//                item.save()
//            }
        }

        fun updateTime(videoID:String,videoTime:Long,currentTime:Long){
            val realm = Realm.getDefaultInstance()
            try {
                var item  = realm.where(RealmVideo::class.java).equalTo("videoID",videoID).findFirst()
                if (item!=null) {
                    realm.beginTransaction()
                    item.videoTime = videoTime
                    item.videoTimeCurrent = currentTime
                    realm.insertOrUpdate(realm.copyFromRealm(item))
                    realm.commitTransaction()
                }
            }catch (e:Exception){
                GBLog.info("Realm", "Insert error:"+e.message, App.getInstance()!!.isDebugMode())
                realm.close()

            }finally {
                realm.close()
            }
//            var item = GBDataBase.getObject(RealmVideo::class.java,"videoID=?",*arrayOf(videoID))
//            if (item!= null){
//                item.videoTime = videoTime
//                item.videoTimeCurrent = currentTime
//                item.save()
//            }
        }
        fun videoDetail(videoID:String,expiredVideo:String):RealmVideo?{
            val realm = Realm.getDefaultInstance()
            var item  = realm.where(RealmVideo::class.java).equalTo("videoID",videoID).and().equalTo("expiredVideo",expiredVideo).findFirst()
//            var item = GBDataBase.getObject(RealmVideo::class.java,"videoID=? and expiredVideo=?",*arrayOf(videoID,expiredVideo))
            return item
        }
        fun isLike(videoID:String):Boolean{
            val realm = Realm.getDefaultInstance()
            var item  = realm.where(RealmVideo::class.java).equalTo("videoID",videoID).and().equalTo("isLike",1.toInt()).findFirst()
//            var item = GBDataBase.getObject(RealmVideo::class.java,"videoID=? and isLike=?",*arrayOf(videoID,1))
            return item != null
        }
        fun isDownLoaded(videoID:String):Boolean{
            val realm = Realm.getDefaultInstance()
            var item  = realm.where(RealmVideo::class.java).equalTo("videoID",videoID).and().equalTo("isDownLoaded",2.toInt()).findFirst()
            return item != null
        }
        fun getObject(videoID: String):RealmVideo?{
            val realm = Realm.getDefaultInstance()
            var item  = realm.where(RealmVideo::class.java).equalTo("videoID",videoID).findFirst()
            if (item!= null) {
                val result = realm.copyFromRealm(item)
                realm.close()
                return result
            }
            return null
        }
        fun getObjectDownLoad(videoID: String):RealmVideo?{
            val realm = Realm.getDefaultInstance()
            var item  = realm.where(RealmVideo::class.java).equalTo("videoID",videoID).and().equalTo("isDownLoaded",0.toInt()).findFirst()
            return realm.copyFromRealm(item)
        }
        fun resetLink(videoID:String,expiredVideo:String){
            val realm = Realm.getDefaultInstance()
            var item  = realm.where(RealmVideo::class.java).equalTo("videoID",videoID).and().equalTo("expiredVideo",expiredVideo).findFirst()
            if (item != null) {
                try {
                    realm.beginTransaction()
                    item.linkPlay = ""
                    item.expiredVideo = ""
                    realm.insertOrUpdate(item)
                    realm.commitTransaction()
                }catch (e:Exception){
                    GBLog.info("Realm", "Insert error:"+e.message, App.getInstance()!!.isDebugMode())
                    realm.close()

                }finally {
                    realm.close()
                }
            }
        }
        fun ignoreVideo(videoID: String){
            val realm = Realm.getDefaultInstance()
            var item  = realm.where(RealmVideo::class.java).equalTo("videoID",videoID).findFirst()
            if (item != null) {
                try {
                    realm.beginTransaction()
                    item.ignore = 1
                    realm.insertOrUpdate(item)
                    realm.commitTransaction()
                }catch (e:Exception){
                    GBLog.info("Realm", "Insert error:"+e.message, App.getInstance()!!.isDebugMode())
                    realm.close()

                }finally {
                    realm.close()
                }
            }
        }
        fun restoreVideo(videoID: String){
            val realm = Realm.getDefaultInstance()
            var item  = realm.where(RealmVideo::class.java).equalTo("videoID",videoID).findFirst()
            if (item != null) {
                try {
                    realm.beginTransaction()
                    item.ignore = 0
                    realm.insertOrUpdate(item)
                    realm.commitTransaction()
                }catch (e:Exception){
                    GBLog.info("Realm", "Insert error:"+e.message, App.getInstance()!!.isDebugMode())
                    realm.close()

                }finally {
                    realm.close()
                }
            }
        }
    }
    fun toLink():HashMap<Int, String>{
        try {
            val result = HashMap<Int, String>()
            val jObj = JSONObject(linkPlay)
            for (iTag in jObj.keys()){
                result.put(iTag.toInt(),jObj.getString(iTag))
            }
            return result
        }catch (e:Exception){
            return HashMap<Int, String>()
        }

    }

}
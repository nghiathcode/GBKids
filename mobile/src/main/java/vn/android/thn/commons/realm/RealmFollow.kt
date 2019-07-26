package vn.android.thn.commons.realm

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class RealmFollow: RealmObject() {
    @PrimaryKey
    @Required
    var channelId: String=""
    var channelTitle: String = ""
    var channelImage = ""
    companion object{
        fun isFollow(channelId:String):Boolean{
//            var item = GBDataBase.getObject(RealmFollow::class.java,"channelId=?",*arrayOf(channelId))
//            if (item==null) return false
            val realm = Realm.getDefaultInstance()
            var item  = realm.where(RealmFollow::class.java).equalTo("channelId",channelId).findFirst()
            return item!=null
        }
        fun unFollow(channelId:String){
//            GBDataBase.deleteTable(RealmFollow::class.java,"channelId=?",*arrayOf(channelId))
            val realm = Realm.getDefaultInstance()
            var item  = realm.where(RealmFollow::class.java).equalTo("channelId",channelId).findFirst()
            if (item!=null){
                realm.beginTransaction()
                item.deleteFromRealm()
                realm.commitTransaction()
            }
        }
    }
}
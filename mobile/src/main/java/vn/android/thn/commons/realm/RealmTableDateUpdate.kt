package vn.android.thn.commons.realm
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import vn.android.thn.commons.App
import vn.android.thn.library.utils.GBLog

open class RealmTableDateUpdate: RealmObject(){
    @PrimaryKey
    var tableName: String = ""
    var dateUp: String = ""
    var dateDown: String = ""
    var dateDownStop: String = ""
    companion object{
        fun getObject(tableName:String = "videos"):RealmTableDateUpdate?{
            val realm = Realm.getDefaultInstance()
            var result:RealmTableDateUpdate? = null
            result=realm.where(RealmTableDateUpdate::class.java).equalTo("tableName",tableName).findFirst()
//            try {
//                realm.beginTransaction()
//                result=realm.where(RealmTableDateUpdate::class.java).equalTo("tableName",tableName).findFirst()
//                realm.commitTransaction()
//            }catch (e:Exception){
//                GBLog.info("Realm", "Insert error:"+e.message, App.getInstance()!!.isDebugMode())
//                realm.close()
//            }finally {
//                realm.close()
//            }
            if (result!=null)return realm.copyFromRealm(result)
            return null
        }
    }
}
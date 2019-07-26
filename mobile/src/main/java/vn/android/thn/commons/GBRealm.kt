package vn.android.thn.commons

import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery
import vn.android.thn.library.utils.GBLog

object GBRealm {
    fun <T:RealmObject>insertList(list:List<T>){
        val realm = Realm.getDefaultInstance()
        try {
            var start = System.currentTimeMillis()
            realm.beginTransaction()
            realm.insertOrUpdate(list)
            realm.commitTransaction()
            var end = System.currentTimeMillis()
            GBLog.info("Realm", "Insert Complete:"+(end-start)+":size="+list.size, App.getInstance()!!.isDebugMode())
        }catch (e:Exception){
            GBLog.info("Realm", "Insert error:"+e.message, App.getInstance()!!.isDebugMode())

            realm.close()
        }finally {
            realm.close()
        }
    }
    fun <T:RealmObject>save(realmObject: T){
        val realm = Realm.getDefaultInstance()
        try {
            realm.beginTransaction()
            realm.insertOrUpdate(realmObject)
            realm.commitTransaction()
        }catch (e:Exception){
            GBLog.info("Realm", "Insert error:"+e.message, App.getInstance()!!.isDebugMode())
            realm.close()
        }finally {
            realm.close()
        }
    }
    fun <T:RealmObject>getObject(realmClass: Class<T>,realmQuery:RealmQuery<T>):T?{
        val realm = Realm.getDefaultInstance()
        try {
            realm.beginTransaction()
            var query = realm.where(realmClass)
            query = realmQuery
            query.findFirst()
            realm.commitTransaction()
        }catch (e:Exception){
            GBLog.info("Realm", "Insert error:"+e.message, App.getInstance()!!.isDebugMode())
            realm.close()
            return null
        }finally {
            realm.close()
        }
        return null
    }
}
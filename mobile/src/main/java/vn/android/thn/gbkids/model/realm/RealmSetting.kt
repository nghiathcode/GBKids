package vn.android.thn.gbkids.model.realm
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmSetting: RealmObject() {
    @PrimaryKey
    var id = 1
    var settingEntity = ""
    companion object{
        fun getObject():RealmSetting?{
            val realm = Realm.getDefaultInstance()
            var item  = realm.where(RealmSetting::class.java).equalTo("id",1.toInt()).findFirst()
            if (item!= null) {
                return realm.copyFromRealm(item)
            }
            return null
        }
    }
}
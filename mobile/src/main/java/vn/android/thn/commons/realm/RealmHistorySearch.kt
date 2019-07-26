package vn.android.thn.commons.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class RealmHistorySearch : RealmObject() {
    @PrimaryKey
    @Required
    var keyword: String=""
    var dateUpdate:String = ""
}
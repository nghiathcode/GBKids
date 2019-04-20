package vn.android.thn.gbkids.model.db

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table

@Table(name = "follow")
class FollowTable : Model() {
    @Column(name = "channelID", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    var channelID: String =""
    @Column(name = "channelTitle")
    var channelTitle: String=""
    @Column(name = "thumbnails")
    var thumbnails: String = ""
}
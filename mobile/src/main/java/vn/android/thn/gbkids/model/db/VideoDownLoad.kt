package vn.android.thn.gbkids.model.db

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table

@Table(name = "videos_download")
class VideoDownLoad : Model() {
    @Column(name = "videoID", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    var videoID: String =""
    @Column(name = "title")
    var title: String=""
    @Column(name = "thumbnails")
    var thumbnails: String = ""
    @Column(name = "channelID")
    var channelID: String =""
    @Column(name = "channelTitle")
    var channelTitle: String=""
}
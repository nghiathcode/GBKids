package vn.android.thn.gbkids.model

import vn.android.thn.commons.realm.RealmVideo
import java.io.Serializable

class VideoReportEntity :Serializable{
    var videoID: String = ""
    var title: String=""
    var imageLarger:String = ""
    var imageSmall:String = ""
    var flagReport = 0//0:client 1:delete
    var comment = ""
    var isDebug = 0
    constructor(videoID:String,comment:String="",isDelete:Boolean = true){
        val video = RealmVideo.getObject(videoID)
        if (video!= null) {
            this.videoID = videoID
            this.imageLarger = video.imageLarger
            this.imageSmall = video.imageSmall
            this.title = video.title
        }
        if (isDelete){
            this.flagReport = 1
        } else {
            this.flagReport = 0
        }
        this.comment = comment
    }
}
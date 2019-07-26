package vn.android.thn.commons.listener

import vn.android.thn.commons.realm.RealmVideo

interface DownloadVideoListener {
    fun onDownload(videoId:String)
    fun onIgnoreVideo(video:RealmVideo)
}
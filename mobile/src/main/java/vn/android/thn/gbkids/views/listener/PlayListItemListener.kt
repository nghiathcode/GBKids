package vn.android.thn.gbfilm.views.listener

import vn.android.thn.gbkids.model.db.VideoTable

interface PlayListItemListener {
    fun onItemClick(obj:Any,pos:Int)
    fun onDownload(videoTable: VideoTable)
}

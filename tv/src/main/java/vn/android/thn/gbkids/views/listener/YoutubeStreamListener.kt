package vn.android.thn.gbfilm.views.listener

import vn.android.thn.gbkids.model.entity.StreamEntity

interface YoutubeStreamListener {
    fun onStartStream()
    fun onStream(list_stream:ArrayList<StreamEntity>)
    fun onStreamError()
}

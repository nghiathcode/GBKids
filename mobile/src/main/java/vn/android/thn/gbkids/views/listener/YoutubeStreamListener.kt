package vn.android.thn.gbfilm.views.listener

interface YoutubeStreamListener {
    fun onStartStream()
    fun onStream(list_stream:ArrayList<String>)
    fun onStreamError()
}

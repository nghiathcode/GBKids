package vn.android.thn.commons.listener

interface YoutubeStreamListener {
    fun onStartStream()
    fun onStream(videoId:String,stream_video:String)
    fun onStreamError()
    fun onNoInternet()
}
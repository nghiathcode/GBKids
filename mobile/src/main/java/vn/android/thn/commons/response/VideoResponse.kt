package vn.android.thn.commons.response

import org.json.JSONArray
import org.json.JSONObject
import vn.android.thn.commons.realm.RealmVideo
import java.util.ArrayList

class VideoResponse:GBTubeResponse() {
    var listRealmVideo = ArrayList<RealmVideo>()
    var video = RealmVideo()
    override fun onJsonData(data: JSONObject) {
        video = app.gson.fromJson<RealmVideo>(data.toString(),RealmVideo::class.java)
    }

    override fun onJsonArrayData(data: JSONArray) {
        for (i in 0 until data.length()){
            val realmItem =app.gson.fromJson<RealmVideo>(data.getJSONObject(i).toString(),RealmVideo::class.java)
            listRealmVideo.add( realmItem)
        }
    }
}
package vn.android.thn.gbkids.model.db

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table
import com.google.gson.Gson
import org.json.JSONObject
import vn.android.thn.gbkids.model.entity.ChannelLogoEntity
import vn.android.thn.library.utils.GBUtils

@Table(name = "follow")
class FollowTable : Model() {
    @Column(name = "channelID", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    var channelID: String =""
    @Column(name = "channelTitle")
    var channelTitle: String=""
    @Column(name = "thumbnails")
    var thumbnails: String = ""
    fun getUrlImage():String{
        if (!GBUtils.isEmpty(thumbnails)){
            try {
                var data= JSONObject(thumbnails)
                var logo  = Gson().fromJson<ChannelLogoEntity>(data.toString(),ChannelLogoEntity::class.java)

                var url = ""
                if (logo!!.high!= null){
                    url = logo.high!!.url
                } else if (logo.medium!= null){
                    url = logo.medium!!.url
                }else if(logo.default!= null){
                    url = logo.default!!.url
                }
                return url
            }catch (e:Exception){
                return  ""
            }

        } else {
            return  ""
        }

    }
}
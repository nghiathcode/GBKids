package vn.android.thn.commons.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.realm.Realm
import vn.android.thn.commons.App
import vn.android.thn.commons.GBRealm
import vn.android.thn.commons.realm.RealmVideo
import vn.android.thn.library.utils.GBLog

class CompleteReciver  : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val realm = Realm.getDefaultInstance()
        val listDownLoad = realm.copyFromRealm(realm.where(RealmVideo::class.java).greaterThan("isDownLoaded",0.toInt()).findAll())
        var pathDirectory  = App.getInstance().filesDir.listFiles()//context!!.getExternalFilesDir(null).listFiles()
        for (video in listDownLoad){
            for (directory in pathDirectory){
                if (directory.isDirectory){
                    if (directory.name.equals(video.videoID,true)){
                        var isFull = 2
                        val listFile = directory.listFiles()
                        if (listFile.size >=2 ){
                            for (file in listFile){
                                if (file.isFile){
                                    if (file.name.contains("img_"+video.videoID,true)){
                                        isFull = isFull-1
                                        video.imageLarger = file.path
                                    }
                                    if (file.name.contains("video_"+video.videoID,true)){
                                        isFull = isFull-1
                                        video.linkPlay = file.path
                                    }
                                    GBLog.info("download complete",file.path, App.getInstance().isDebugMode())
                                }
                            }
                            if (isFull <= 0) {
                                video.isDownLoaded = 2

                            } else {
                                video.isDownLoaded = 0
                            }
                            GBRealm.save(video)
                        }
                    }
                }
            }
        }
    }
}
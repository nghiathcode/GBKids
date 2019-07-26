package vn.android.thn.commons.service

import android.app.IntentService
import android.content.Intent
import io.realm.Realm
import vn.android.thn.commons.App
import vn.android.thn.commons.GBRealm
import vn.android.thn.commons.realm.RealmVideo
import vn.android.thn.library.utils.GBLog

class ScanFileDownLoadService: IntentService("ScanFileDownLoadService")  {
    override fun onHandleIntent(intent: Intent?) {

        var pathDirectory = App.getInstance().filesDir.listFiles()//App.getInstance()!!.getExternalFilesDir(null).listFiles()
        for (directory in pathDirectory){
            if (directory.isDirectory){
                var videoDownLoad = RealmVideo.getObject(directory.name)
                if (videoDownLoad!= null){
                    val listFile = directory.listFiles()
                    var isFull = 2
                    if (listFile.size >=2 ){
                        for (file in listFile){
                            if (file.isFile){
                                if (file.name.contains("img_"+videoDownLoad.videoID,true)) {
                                    videoDownLoad.imageLarger = file.path
                                    isFull = isFull-1
                                }
                                if (file.name.contains("video_"+videoDownLoad.videoID,true)) {
                                    videoDownLoad.linkPlay = file.path
                                    isFull = isFull-1
                                }
                            }
                        }
                        if (isFull <= 0) {
                            videoDownLoad.isDownLoaded = 2
                        } else {
                            videoDownLoad.isDownLoaded = 0
                        }
                        GBRealm.save(videoDownLoad)
                    }
                }
            }

        }
        val realm = Realm.getDefaultInstance()
        val listDownLoad = realm.copyFromRealm(realm.where(RealmVideo::class.java).greaterThan("isDownLoaded",0.toInt()).findAll())
        for (video in listDownLoad){
            var isExist = false
            for (directory in pathDirectory){
                if (directory.isDirectory){
                    if (directory.name.equals(video.videoID)){
                        isExist = true
                        break
                    }
                }
            }
            if (!isExist){
                video.isDownLoaded = 0
                GBRealm.save(video)
            }
        }
    }
}